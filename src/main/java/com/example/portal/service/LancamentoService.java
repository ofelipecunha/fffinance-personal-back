package com.example.portal.service;

import com.example.portal.dto.LancamentoCreateRequest;
import com.example.portal.dto.LancamentoResponse;
import com.example.portal.dto.LancamentoUpdateRequest;
import com.example.portal.entity.Categoria;
import com.example.portal.entity.FormaPagamento;
import com.example.portal.entity.Lancamento;
import com.example.portal.repository.CategoriaRepository;
import com.example.portal.repository.FormaPagamentoRepository;
import com.example.portal.repository.LancamentoRepository;
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
public class LancamentoService {

	private final LancamentoRepository repository;
	private final CategoriaRepository categoriaRepository;
	private final FormaPagamentoRepository formaPagamentoRepository;

	public List<LancamentoResponse> listar(LocalDate dataInicio, LocalDate dataFim) {
		boolean filtraData = dataInicio != null || dataFim != null;
		if (!filtraData) {
			return repository.findAllOrdered().stream().map(this::paraResponse).toList();
		}
		return repository.findAllOrderedFiltrado(dataInicio, dataFim).stream().map(this::paraResponse).toList();
	}

	public LancamentoResponse buscar(Integer id) {
		return repository.findDetalhe(id)
				.map(this::paraResponse)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lançamento não encontrado"));
	}

	@Transactional
	public LancamentoResponse criar(LancamentoCreateRequest dto) {
		Lancamento e = new Lancamento();
		aplicarCampos(e, dto.descricao(), dto.valor(), dto.dataLancamento(), dto.categoriaId(), dto.tipo(),
				dto.formaPagamentoId(), dto.pago() != null ? dto.pago() : Boolean.FALSE);
		return paraResponse(repository.save(e));
	}

	@Transactional
	public LancamentoResponse atualizar(Integer id, LancamentoUpdateRequest dto) {
		Lancamento e = repository.findDetalhe(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lançamento não encontrado"));
		aplicarCampos(e, dto.descricao(), dto.valor(), dto.dataLancamento(), dto.categoriaId(), dto.tipo(),
				dto.formaPagamentoId(), dto.pago());
		return paraResponse(repository.save(e));
	}

	@Transactional
	public void excluir(Integer id) {
		if (!repository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lançamento não encontrado");
		}
		repository.deleteById(id);
	}

	private void aplicarCampos(
			Lancamento e,
			String descricao,
			java.math.BigDecimal valor,
			java.time.LocalDate dataLancamento,
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

		e.setTipo(normalizarTipoLancamento(tipoRaw));

		if (formaPagamentoId != null) {
			FormaPagamento fp = formaPagamentoRepository.findById(formaPagamentoId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Forma de pagamento inválida"));
			e.setFormaPagamentoRef(fp);
			e.setFormaPagamento(mapTipoParaLegado(fp.getTipo()));
		} else {
			e.setFormaPagamentoRef(null);
			e.setFormaPagamento(null);
		}

		e.setPago(pago);
	}

	private LancamentoResponse paraResponse(Lancamento l) {
		FormaPagamento fp = l.getFormaPagamentoRef();
		Integer fpId = fp != null ? fp.getId() : null;
		String fpNome = fp != null ? fp.getNome() : null;
		return new LancamentoResponse(
				l.getId(),
				l.getDescricao(),
				l.getValor(),
				l.getDataLancamento(),
				l.getCategoria().getId(),
				l.getCategoria().getNome(),
				l.getTipo(),
				l.getFormaPagamento(),
				fpId,
				fpNome,
				l.getPago(),
				l.getDataCriacao());
	}

	private static String normalizarTipoLancamento(String raw) {
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

	/** Mapeia o tipo cadastrado em forma_pagamento para o campo legado (CARTAO, PIX, DINHEIRO). */
	static String mapTipoParaLegado(String tipoForma) {
		if (tipoForma == null) {
			return null;
		}
		return switch (tipoForma.trim().toUpperCase(Locale.ROOT)) {
			case "DINHEIRO" -> "DINHEIRO";
			case "DIGITAL" -> "PIX";
			case "CREDITO", "DEBITO" -> "CARTAO";
			default -> null;
		};
	}
}
