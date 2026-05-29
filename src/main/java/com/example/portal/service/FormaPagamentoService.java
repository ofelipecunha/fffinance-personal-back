package com.example.portal.service;

import com.example.portal.dto.FormaPagamentoCreateRequest;
import com.example.portal.dto.FormaPagamentoResponse;
import com.example.portal.dto.FormaPagamentoUpdateRequest;
import com.example.portal.entity.FormaPagamento;
import com.example.portal.repository.FormaPagamentoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FormaPagamentoService {

	private final FormaPagamentoRepository repository;

	public List<FormaPagamentoResponse> listar() {
		return repository.findAllOrderedByNomeIgnoreCase().stream()
				.map(this::paraResponse)
				.toList();
	}

	public FormaPagamentoResponse buscar(Integer id) {
		return repository.findById(id)
				.map(this::paraResponse)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Forma de pagamento não encontrada"));
	}

	@Transactional
	public FormaPagamentoResponse criar(FormaPagamentoCreateRequest dto) {
		FormaPagamento e = new FormaPagamento();
		e.setNome(dto.nome().trim());
		e.setTipo(dto.tipo() == null ? null : trimOrNull(dto.tipo()));
		e.setAtivo(dto.ativo() != null ? dto.ativo() : Boolean.TRUE);
		return paraResponse(repository.save(e));
	}

	@Transactional
	public FormaPagamentoResponse atualizar(Integer id, FormaPagamentoUpdateRequest dto) {
		FormaPagamento e = repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Forma de pagamento não encontrada"));
		e.setNome(dto.nome().trim());
		e.setTipo(dto.tipo() == null ? null : trimOrNull(dto.tipo()));
		e.setAtivo(dto.ativo());
		return paraResponse(repository.save(e));
	}

	@Transactional
	public void excluir(Integer id) {
		if (!repository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Forma de pagamento não encontrada");
		}
		repository.deleteById(id);
	}

	private FormaPagamentoResponse paraResponse(FormaPagamento e) {
		return new FormaPagamentoResponse(e.getId(), e.getNome(), e.getTipo(), e.getAtivo(), e.getDataCriacao());
	}

	private static String trimOrNull(String s) {
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}
}
