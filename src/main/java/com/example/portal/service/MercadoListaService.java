package com.example.portal.service;

import com.example.portal.dto.ItemListaCompraResponse;
import com.example.portal.dto.ItemListaCompraUpdateRequest;
import com.example.portal.dto.ItemListaLinhaRequest;
import com.example.portal.dto.MercadoListaCardResponse;
import com.example.portal.dto.MercadoListaCreateRequest;
import com.example.portal.dto.MercadoListaDetalheResponse;
import com.example.portal.dto.MercadoListaResumoResponse;
import com.example.portal.dto.MercadoListaUpdateRequest;
import com.example.portal.entity.ItemListaCompra;
import com.example.portal.entity.ListaCompra;
import com.example.portal.entity.LoginUsuario;
import com.example.portal.entity.TipoLista;
import com.example.portal.repository.ItemListaCompraRepository;
import com.example.portal.repository.ListaCompraRepository;
import com.example.portal.repository.LoginUsuarioRepository;
import com.example.portal.repository.TipoListaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
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
public class MercadoListaService {

	private final AuthService authService;
	private final ListaCompraRepository listaRepo;
	private final ItemListaCompraRepository itemRepo;
	private final TipoListaRepository tipoRepo;
	private final LoginUsuarioRepository loginUsuarioRepository;

	public List<MercadoListaCardResponse> listar(
			String authorization, LocalDate dataInicio, LocalDate dataFim, Long idLoginFiltro) {
		authService.requireUsuarioPorBearer(authorization);
		validarPeriodo(dataInicio, dataFim);
		List<ListaCompra> listas = listaRepo.findMercadoPorPeriodo(dataInicio, dataFim, idLoginFiltro);
		Map<Long, String> nomes = carregarNomesUsuarios(listas);
		return listas.stream().map(l -> paraCard(l, nomes)).toList();
	}

	public MercadoListaResumoResponse resumo(
			String authorization, LocalDate dataInicio, LocalDate dataFim, Long idLoginFiltro) {
		authService.requireUsuarioPorBearer(authorization);
		validarPeriodo(dataInicio, dataFim);
		List<ListaCompra> listas = listaRepo.findMercadoPorPeriodo(dataInicio, dataFim, idLoginFiltro);
		BigDecimal valorTotal = BigDecimal.ZERO;
		int abertas = 0;
		int finalizadas = 0;
		for (ListaCompra l : listas) {
			valorTotal = valorTotal.add(itemRepo.sumTotalByListaId(l.getId()));
			if (Boolean.TRUE.equals(l.getFinalizada())) {
				finalizadas++;
			} else {
				abertas++;
			}
		}
		return new MercadoListaResumoResponse(listas.size(), valorTotal, abertas, finalizadas);
	}

	public MercadoListaDetalheResponse buscar(String authorization, Integer id) {
		authService.requireUsuarioPorBearer(authorization);
		ListaCompra l = requireListaMercado(id);
		return montarDetalhe(l, carregarNomesUsuarios(List.of(l)));
	}

	@Transactional
	public MercadoListaCardResponse criar(String authorization, MercadoListaCreateRequest dto) {
		LoginUsuario usuario = authService.requireUsuarioPorBearer(authorization);
		TipoLista tipo = tipoMercado();
		ListaCompra lista = new ListaCompra();
		lista.setTipo(tipo);
		lista.setNome(dto.nome().trim());
		lista.setDataCriacao(Instant.now());
		lista.setIdLogin(usuario.getIdLogin());
		lista.setDataCompra(dto.dataCompra() != null ? dto.dataCompra() : LocalDate.now());
		lista.setFinalizada(Boolean.FALSE);
		lista = listaRepo.save(lista);
		Map<Long, String> nomes = carregarNomesUsuarios(List.of(lista));
		return paraCard(lista, nomes);
	}

	@Transactional
	public MercadoListaCardResponse atualizar(String authorization, Integer id, MercadoListaUpdateRequest dto) {
		authService.requireUsuarioPorBearer(authorization);
		ListaCompra l = requireListaMercado(id);
		l.setNome(dto.nome().trim());
		if (dto.dataCompra() != null) {
			l.setDataCompra(dto.dataCompra());
		}
		if (dto.finalizada() != null) {
			l.setFinalizada(dto.finalizada());
		}
		listaRepo.save(l);
		return paraCard(l, carregarNomesUsuarios(List.of(l)));
	}

	@Transactional
	public void excluir(String authorization, Integer id) {
		authService.requireUsuarioPorBearer(authorization);
		ListaCompra l = requireListaMercado(id);
		listaRepo.delete(l);
	}

	@Transactional
	public ItemListaCompraResponse adicionarItem(String authorization, Integer listaId, ItemListaLinhaRequest dto) {
		authService.requireUsuarioPorBearer(authorization);
		ListaCompra lista = requireListaMercado(listaId);
		ItemListaCompra it = salvarNovoItem(lista, dto);
		itemRepo.flush();
		return paraItemResponse(it);
	}

	@Transactional
	public ItemListaCompraResponse atualizarItem(
			String authorization, Integer listaId, Integer itemId, ItemListaCompraUpdateRequest dto) {
		authService.requireUsuarioPorBearer(authorization);
		requireListaMercado(listaId);
		ItemListaCompra it = requireItemDaLista(listaId, itemId);
		it.setNome(dto.nome().trim());
		it.setQuantidade(dto.quantidade());
		it.setValorUnitario(normalizarMoeda(dto.valorUnitario()));
		itemRepo.save(it);
		itemRepo.flush();
		return paraItemResponse(itemRepo.findById(itemId).orElseThrow());
	}

	@Transactional
	public void excluirItem(String authorization, Integer listaId, Integer itemId) {
		authService.requireUsuarioPorBearer(authorization);
		requireListaMercado(listaId);
		ItemListaCompra it = requireItemDaLista(listaId, itemId);
		itemRepo.delete(it);
	}

	private ListaCompra requireListaMercado(Integer id) {
		ListaCompra l = listaRepo.findByIdComTipo(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista não encontrada"));
		if (l.getTipo() == null || !"MERCADO".equals(l.getTipo().getCodigo())) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista não encontrada");
		}
		return l;
	}

	private ItemListaCompra requireItemDaLista(Integer listaId, Integer itemId) {
		ItemListaCompra it = itemRepo.findById(itemId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));
		if (!it.getLista().getId().equals(listaId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item não pertence a esta lista");
		}
		return it;
	}

	private TipoLista tipoMercado() {
		return tipoRepo.findByCodigo("MERCADO")
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Tipo MERCADO não configurado"));
	}

	private MercadoListaDetalheResponse montarDetalhe(ListaCompra l, Map<Long, String> nomes) {
		List<ItemListaCompra> itens = itemRepo.findByLista_IdOrderByIdAsc(l.getId());
		List<ItemListaCompraResponse> linhas = itens.stream().map(this::paraItemResponse).toList();
		Long idLogin = l.getIdLogin();
		String nomeUsuario = idLogin != null ? nomes.getOrDefault(idLogin, null) : null;
		return new MercadoListaDetalheResponse(
				l.getId(),
				l.getNome(),
				l.getDataCompra(),
				l.getFinalizada(),
				idLogin,
				nomeUsuario,
				itemRepo.sumTotalByListaId(l.getId()),
				linhas);
	}

	private MercadoListaCardResponse paraCard(ListaCompra l, Map<Long, String> nomes) {
		Long idLogin = l.getIdLogin();
		String nomeUsuario = idLogin != null ? nomes.getOrDefault(idLogin, null) : null;
		long qtd = itemRepo.findByLista_IdOrderByIdAsc(l.getId()).size();
		return new MercadoListaCardResponse(
				l.getId(),
				l.getNome(),
				l.getDataCompra(),
				itemRepo.sumTotalByListaId(l.getId()),
				(int) qtd,
				l.getFinalizada(),
				idLogin,
				nomeUsuario);
	}

	private ItemListaCompra salvarNovoItem(ListaCompra lista, ItemListaLinhaRequest dto) {
		ItemListaCompra it = new ItemListaCompra();
		it.setLista(lista);
		it.setNome(dto.nome().trim());
		it.setQuantidade(dto.quantidade());
		it.setValorUnitario(normalizarMoeda(dto.valorUnitario()));
		return itemRepo.save(it);
	}

	private ItemListaCompraResponse paraItemResponse(ItemListaCompra i) {
		BigDecimal total = i.getTotal();
		if (total == null) {
			int qtd = i.getQuantidade() != null ? i.getQuantidade() : 0;
			BigDecimal unit = i.getValorUnitario() != null ? i.getValorUnitario() : BigDecimal.ZERO;
			total = BigDecimal.valueOf(qtd).multiply(unit);
		}
		return new ItemListaCompraResponse(
				i.getId(),
				i.getNome(),
				i.getQuantidade(),
				i.getValorUnitario(),
				total.setScale(2, RoundingMode.HALF_UP));
	}

	private Map<Long, String> carregarNomesUsuarios(List<ListaCompra> listas) {
		Set<Long> ids = listas.stream()
				.map(ListaCompra::getIdLogin)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		if (ids.isEmpty()) {
			return Map.of();
		}
		Map<Long, String> mapa = new HashMap<>();
		loginUsuarioRepository.findAllById(ids).forEach(u -> mapa.put(u.getIdLogin(), u.getNome()));
		return mapa;
	}

	private static void validarPeriodo(LocalDate inicio, LocalDate fim) {
		if (inicio == null || fim == null || !inicio.isBefore(fim)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Período inválido");
		}
	}

	private static BigDecimal normalizarMoeda(BigDecimal v) {
		if (v == null) {
			return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		}
		return v.setScale(2, RoundingMode.HALF_UP);
	}
}
