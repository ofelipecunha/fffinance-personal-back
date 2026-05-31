package com.example.portal.service;

import com.example.portal.dto.CarroServicoCreateRequest;
import com.example.portal.dto.CarroServicoResponse;
import com.example.portal.entity.Categoria;
import com.example.portal.entity.Lancamento;
import com.example.portal.entity.LoginUsuario;
import com.example.portal.repository.CategoriaRepository;
import com.example.portal.repository.LancamentoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CarroServicosService {

	private static final String CATEGORIA_NOME = "CARRO";
	private static final String CATEGORIA_TIPO = "DESPESA";

	private final AuthService authService;
	private final LancamentoRepository lancamentoRepository;
	private final CategoriaRepository categoriaRepository;

	public List<CarroServicoResponse> listar(String authorization, String descricao) {
		authService.requireUsuarioPorBearer(authorization);
		Categoria cat = requireCategoriaCarro();
		String filtro = normalizarFiltroDescricao(descricao);
		return lancamentoRepository.findByCategoriaIdAndDescricaoOpcional(cat.getId(), filtro).stream()
				.map(this::paraResponse)
				.toList();
	}

	@Transactional
	public CarroServicoResponse criar(String authorization, CarroServicoCreateRequest dto) {
		LoginUsuario usuario = authService.requireUsuarioPorBearer(authorization);
		Categoria cat = requireCategoriaCarro();

		Lancamento e = new Lancamento();
		e.setIdLogin(usuario.getIdLogin());
		e.setDescricao(dto.descricao().trim());
		e.setValor(dto.valor());
		e.setDataLancamento(dto.dataLancamento());
		e.setCategoria(cat);
		e.setTipo(CATEGORIA_TIPO);
		e.setFormaPagamentoRef(null);
		e.setFormaPagamento(null);
		e.setPago(Boolean.FALSE);
		e.setDataPagamento(null);

		return paraResponse(lancamentoRepository.save(e));
	}

	@Transactional
	public void excluir(String authorization, Integer id) {
		authService.requireUsuarioPorBearer(authorization);
		Categoria cat = requireCategoriaCarro();
		Lancamento e = lancamentoRepository.findDetalhe(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"));
		if (!cat.getId().equals(e.getCategoria().getId())) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado");
		}
		lancamentoRepository.delete(e);
	}

	private Categoria requireCategoriaCarro() {
		return categoriaRepository
				.findFirstByNomeIgnoreCaseAndTipoIgnoreCase(CATEGORIA_NOME, CATEGORIA_TIPO)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Categoria CARRO (DESPESA) não configurada. Execute as migrations do banco."));
	}

	private CarroServicoResponse paraResponse(Lancamento l) {
		return new CarroServicoResponse(l.getId(), l.getDescricao(), l.getValor(), l.getDataLancamento());
	}

	private static String normalizarFiltroDescricao(String descricao) {
		if (descricao == null) {
			return null;
		}
		String t = descricao.trim();
		return t.isEmpty() ? null : t;
	}
}
