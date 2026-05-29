package com.example.portal.service;

import com.example.portal.dto.ContinhasCardResponse;
import com.example.portal.dto.ContinhasPagoRequest;
import com.example.portal.dto.ContinhasResumoResponse;
import com.example.portal.dto.LancamentoCreateRequest;
import com.example.portal.dto.LancamentoUpdateRequest;
import com.example.portal.entity.Categoria;
import com.example.portal.entity.FormaPagamento;
import com.example.portal.entity.Lancamento;
import com.example.portal.entity.LoginUsuario;
import com.example.portal.repository.CategoriaRepository;
import com.example.portal.repository.FormaPagamentoRepository;
import com.example.portal.repository.LancamentoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ContinhasService {

	private final AuthService authService;
	private final LancamentoRepository repository;
	private final CategoriaRepository categoriaRepository;
	private final FormaPagamentoRepository formaPagamentoRepository;

	public List<ContinhasCardResponse> listar(String authorization, LocalDate dataInicio, LocalDate dataFim) {
		Long idLogin = idLogin(authorization);
		validarPeriodo(dataInicio, dataFim);
		return repository.findByUsuarioAndPeriodo(idLogin, dataInicio, dataFim).stream()
				.map(this::paraCard)
				.toList();
	}

	public ContinhasResumoResponse resumo(String authorization, LocalDate dataInicio, LocalDate dataFim) {
		Long idLogin = idLogin(authorization);
		validarPeriodo(dataInicio, dataFim);
		BigDecimal receitas = BigDecimal.ZERO;
		BigDecimal despesas = BigDecimal.ZERO;
		BigDecimal pagas = BigDecimal.ZERO;
		BigDecimal pendentes = BigDecimal.ZERO;

		for (Lancamento l : repository.findByUsuarioAndPeriodo(idLogin, dataInicio, dataFim)) {
			BigDecimal valor = l.getValor();
			if ("RECEITA".equals(l.getTipo())) {
				receitas = receitas.add(valor);
			} else if ("DESPESA".equals(l.getTipo())) {
				despesas = despesas.add(valor);
				if (Boolean.TRUE.equals(l.getPago())) {
					pagas = pagas.add(valor);
				} else {
					pendentes = pendentes.add(valor);
				}
			}
		}

		BigDecimal saldo = receitas.subtract(despesas);
		return new ContinhasResumoResponse(receitas, despesas, pagas, pendentes, saldo);
	}

	@Transactional
	public ContinhasCardResponse criar(String authorization, LancamentoCreateRequest dto) {
		LoginUsuario usuario = authService.requireUsuarioPorBearer(authorization);
		Lancamento e = new Lancamento();
		e.setIdLogin(usuario.getIdLogin());
		aplicarCampos(e, dto.descricao(), dto.valor(), dto.dataLancamento(), dto.categoriaId(), dto.tipo(),
				dto.formaPagamentoId(), dto.pago() != null ? dto.pago() : Boolean.FALSE);
		return paraCard(repository.save(e));
	}

	@Transactional
	public ContinhasCardResponse atualizar(String authorization, Integer id, LancamentoUpdateRequest dto) {
		Lancamento e = requireLancamento(authorization, id);
		aplicarCampos(e, dto.descricao(), dto.valor(), dto.dataLancamento(), dto.categoriaId(), dto.tipo(),
				dto.formaPagamentoId(), dto.pago());
		return paraCard(repository.save(e));
	}

	@Transactional
	public ContinhasCardResponse alternarPago(String authorization, Integer id, ContinhasPagoRequest body) {
		Lancamento e = requireLancamento(authorization, id);
		boolean pago = Boolean.TRUE.equals(body.pago());
		e.setPago(pago);
		e.setDataPagamento(pago ? LocalDate.now() : null);
		return paraCard(repository.save(e));
	}

	@Transactional
	public void excluir(String authorization, Integer id) {
		Lancamento e = requireLancamento(authorization, id);
		repository.delete(e);
	}

	private Lancamento requireLancamento(String authorization, Integer id) {
		Long idLogin = idLogin(authorization);
		return repository.findDetalheDoUsuario(id, idLogin)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));
	}

	private Long idLogin(String authorization) {
		return authService.requireUsuarioPorBearer(authorization).getIdLogin();
	}

	private static void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
		if (dataInicio == null || dataFim == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe dataInicio e dataFim");
		}
		if (!dataFim.isAfter(dataInicio)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dataFim deve ser posterior a dataInicio");
		}
	}

	private void aplicarCampos(
			Lancamento e,
			String descricao,
			BigDecimal valor,
			LocalDate dataLancamento,
			Integer categoriaId,
			String tipoRaw,
			Integer formaPagamentoId,
			boolean pago) {

		e.setDescricao(trimToNull(descricao));
		e.setValor(valor);
		e.setDataLancamento(dataLancamento);

		Categoria cat = categoriaRepository.findById(categoriaId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria inválida"));
		e.setCategoria(cat);
		e.setTipo(normalizarTipo(tipoRaw));

		if (formaPagamentoId != null) {
			FormaPagamento fp = formaPagamentoRepository.findById(formaPagamentoId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Forma de pagamento inválida"));
			e.setFormaPagamentoRef(fp);
			e.setFormaPagamento(LancamentoService.mapTipoParaLegado(fp.getTipo()));
		} else {
			e.setFormaPagamentoRef(null);
			e.setFormaPagamento(null);
		}

		e.setPago(pago);
		e.setDataPagamento(pago ? (e.getDataPagamento() != null ? e.getDataPagamento() : LocalDate.now()) : null);
	}

	private ContinhasCardResponse paraCard(Lancamento l) {
		FormaPagamento fp = l.getFormaPagamentoRef();
		return new ContinhasCardResponse(
				l.getId(),
				l.getDescricao(),
				l.getValor(),
				l.getTipo(),
				l.getCategoria().getId(),
				l.getCategoria().getNome(),
				fp != null ? fp.getId() : null,
				fp != null ? fp.getNome() : null,
				Boolean.TRUE.equals(l.getPago()) ? "S" : "N",
				l.getDataLancamento(),
				l.getDataPagamento());
	}

	private static String normalizarTipo(String raw) {
		String t = raw.trim().toUpperCase(Locale.ROOT);
		if (!"RECEITA".equals(t) && !"DESPESA".equals(t)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo deve ser RECEITA ou DESPESA");
		}
		return t;
	}

	private static String trimToNull(String s) {
		if (s == null) {
			return null;
		}
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}

}
