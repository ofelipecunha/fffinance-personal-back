package com.example.portal.service;

import com.example.portal.dto.AnotacaoCardResponse;
import com.example.portal.dto.AnotacaoCreateRequest;
import com.example.portal.dto.AnotacaoDetalheResponse;
import com.example.portal.dto.AnotacaoItemLinhaRequest;
import com.example.portal.dto.AnotacaoItemResponse;
import com.example.portal.dto.AnotacaoRapidoRequest;
import com.example.portal.dto.VirarListaResponse;
import com.example.portal.entity.AnotacaoMercado;
import com.example.portal.entity.ItemAnotacao;
import com.example.portal.entity.ItemListaCompra;
import com.example.portal.entity.ListaCompra;
import com.example.portal.entity.LoginUsuario;
import com.example.portal.entity.TipoLista;
import com.example.portal.repository.AnotacaoMercadoRepository;
import com.example.portal.repository.ItemAnotacaoRepository;
import com.example.portal.repository.ItemListaCompraRepository;
import com.example.portal.repository.ListaCompraRepository;
import com.example.portal.repository.LoginUsuarioRepository;
import com.example.portal.repository.TipoListaRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MercadoAnotacaoService {

	private static final String TITULO_INBOX = "Faltando em casa";
	private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private final AuthService authService;
	private final AnotacaoMercadoRepository anotacaoRepo;
	private final ItemAnotacaoRepository itemRepo;
	private final ListaCompraRepository listaRepo;
	private final ItemListaCompraRepository itemListaRepo;
	private final TipoListaRepository tipoRepo;
	private final LoginUsuarioRepository loginUsuarioRepository;

	public List<AnotacaoCardResponse> listarAbertas(String authorization, Long idLoginFiltro) {
		authService.requireUsuarioPorBearer(authorization);
		Map<Long, String> nomes = carregarNomes(anotacaoRepo.findAbertas(idLoginFiltro));
		return anotacaoRepo.findAbertas(idLoginFiltro).stream().map(a -> paraCard(a, nomes)).toList();
	}

	public AnotacaoDetalheResponse buscar(String authorization, Integer id) {
		authService.requireUsuarioPorBearer(authorization);
		AnotacaoMercado a = requireAberta(id);
		return montarDetalhe(a);
	}

	@Transactional
	public AnotacaoCardResponse criar(String authorization, AnotacaoCreateRequest dto) {
		LoginUsuario usuario = authService.requireUsuarioPorBearer(authorization);
		AnotacaoMercado a = novaAnotacao(usuario.getIdLogin(), dto.titulo().trim());
		a = anotacaoRepo.save(a);
		return paraCard(a, Map.of());
	}

	/** Dashboard: adiciona item na anotação aberta do usuário (cria inbox se não existir). */
	@Transactional
	public AnotacaoDetalheResponse adicionarRapido(String authorization, AnotacaoRapidoRequest dto) {
		LoginUsuario usuario = authService.requireUsuarioPorBearer(authorization);
		AnotacaoMercado a = anotacaoRepo
				.findFirstByIdLoginAndConvertidaFalseOrderByDataCriacaoDesc(usuario.getIdLogin())
				.orElseGet(() -> anotacaoRepo.save(novaAnotacao(usuario.getIdLogin(), TITULO_INBOX)));
		salvarItem(a, dto.nome(), dto.quantidade());
		anotacaoRepo.flush();
		return montarDetalhe(requireAberta(a.getId()));
	}

	@Transactional
	public AnotacaoItemResponse adicionarItem(String authorization, Integer anotacaoId, AnotacaoItemLinhaRequest dto) {
		authService.requireUsuarioPorBearer(authorization);
		AnotacaoMercado a = requireAberta(anotacaoId);
		ItemAnotacao it = salvarItem(a, dto.nome(), dto.quantidade());
		return paraItem(it);
	}

	@Transactional
	public void excluirItem(String authorization, Integer anotacaoId, Integer itemId) {
		authService.requireUsuarioPorBearer(authorization);
		requireAberta(anotacaoId);
		ItemAnotacao it = itemRepo.findById(itemId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));
		if (!it.getAnotacao().getId().equals(anotacaoId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item não pertence a esta anotação");
		}
		itemRepo.delete(it);
	}

	@Transactional
	public void excluirAnotacao(String authorization, Integer id) {
		authService.requireUsuarioPorBearer(authorization);
		AnotacaoMercado a = requireAberta(id);
		anotacaoRepo.delete(a);
	}

	@Transactional
	public VirarListaResponse virarLista(String authorization, Integer anotacaoId) {
		LoginUsuario usuario = authService.requireUsuarioPorBearer(authorization);
		AnotacaoMercado a = requireAberta(anotacaoId);
		List<ItemAnotacao> itens = itemRepo.findByAnotacao_IdOrderByIdAsc(a.getId());
		if (itens.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Adicione pelo menos um item antes de virar lista");
		}

		TipoLista tipo = tipoRepo.findByCodigo("MERCADO")
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Tipo MERCADO não configurado"));

		String nomeLista = a.getTitulo() + " — " + FMT_DATA.format(LocalDate.now());
		ListaCompra lista = new ListaCompra();
		lista.setTipo(tipo);
		lista.setNome(nomeLista);
		lista.setDataCriacao(Instant.now());
		lista.setIdLogin(usuario.getIdLogin());
		lista.setDataCompra(LocalDate.now());
		lista.setFinalizada(Boolean.FALSE);
		lista = listaRepo.save(lista);

		for (ItemAnotacao item : itens) {
			ItemListaCompra linha = new ItemListaCompra();
			linha.setLista(lista);
			linha.setNome(item.getNome());
			linha.setQuantidade(item.getQuantidade());
			linha.setValorUnitario(BigDecimal.ZERO);
			itemListaRepo.save(linha);
		}

		a.setConvertida(Boolean.TRUE);
		a.setListaId(lista.getId());
		anotacaoRepo.save(a);

		return new VirarListaResponse(lista.getId(), lista.getNome());
	}

	private AnotacaoMercado novaAnotacao(Long idLogin, String titulo) {
		AnotacaoMercado a = new AnotacaoMercado();
		a.setIdLogin(idLogin);
		a.setTitulo(titulo);
		a.setDataCriacao(Instant.now());
		a.setConvertida(Boolean.FALSE);
		return a;
	}

	private ItemAnotacao salvarItem(AnotacaoMercado a, String nome, Integer quantidade) {
		ItemAnotacao it = new ItemAnotacao();
		it.setAnotacao(a);
		it.setNome(nome.trim());
		it.setQuantidade(quantidade);
		return itemRepo.save(it);
	}

	private AnotacaoMercado requireAberta(Integer id) {
		return anotacaoRepo.findAbertaById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anotação não encontrada"));
	}

	private AnotacaoDetalheResponse montarDetalhe(AnotacaoMercado a) {
		List<AnotacaoItemResponse> itens = itemRepo.findByAnotacao_IdOrderByIdAsc(a.getId()).stream()
				.map(this::paraItem)
				.toList();
		return new AnotacaoDetalheResponse(a.getId(), a.getTitulo(), a.getDataCriacao(), itens);
	}

	private AnotacaoCardResponse paraCard(AnotacaoMercado a, Map<Long, String> nomes) {
		int qtd = (int) itemRepo.countByAnotacaoId(a.getId());
		Long idLogin = a.getIdLogin();
		String usuarioNome = idLogin != null ? nomes.getOrDefault(idLogin, null) : null;
		return new AnotacaoCardResponse(a.getId(), a.getTitulo(), a.getDataCriacao(), qtd, idLogin, usuarioNome);
	}

	private AnotacaoItemResponse paraItem(ItemAnotacao i) {
		return new AnotacaoItemResponse(i.getId(), i.getNome(), i.getQuantidade());
	}

	private Map<Long, String> carregarNomes(List<AnotacaoMercado> lista) {
		Set<Long> ids = lista.stream()
				.map(AnotacaoMercado::getIdLogin)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		if (ids.isEmpty()) {
			return Map.of();
		}
		Map<Long, String> mapa = new HashMap<>();
		loginUsuarioRepository.findAllById(ids).forEach(u -> mapa.put(u.getIdLogin(), u.getNome()));
		return mapa;
	}
}
