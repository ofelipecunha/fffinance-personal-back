package com.example.portal.controller;

import com.example.portal.dto.FormaPagamentoCreateRequest;
import com.example.portal.dto.FormaPagamentoResponse;
import com.example.portal.dto.FormaPagamentoUpdateRequest;
import com.example.portal.service.FormaPagamentoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/formas-pagamento")
@RequiredArgsConstructor
public class FormaPagamentoController {

	private final FormaPagamentoService service;

	@GetMapping
	public List<FormaPagamentoResponse> listar() {
		return service.listar();
	}

	@GetMapping("/{id}")
	public FormaPagamentoResponse porId(@PathVariable Integer id) {
		return service.buscar(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FormaPagamentoResponse criar(@Valid @RequestBody FormaPagamentoCreateRequest body) {
		return service.criar(body);
	}

	@PutMapping("/{id}")
	public FormaPagamentoResponse atualizar(
			@PathVariable Integer id,
			@Valid @RequestBody FormaPagamentoUpdateRequest body) {
		return service.atualizar(id, body);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Integer id) {
		service.excluir(id);
	}
}
