package com.example.portal.controller;

import com.example.portal.dto.EmprestimoCreateRequest;
import com.example.portal.dto.EmprestimoResponse;
import com.example.portal.dto.EmprestimoUpdateRequest;
import com.example.portal.service.EmprestimoService;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

	private final EmprestimoService service;

	@GetMapping
	public List<EmprestimoResponse> listar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestParam(required = false) String descricao) {
		return service.listar(authorization, descricao);
	}

	@GetMapping("/{id}")
	public EmprestimoResponse buscar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id) {
		return service.buscar(authorization, id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EmprestimoResponse criar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@Valid @RequestBody EmprestimoCreateRequest body) {
		return service.criar(authorization, body);
	}

	@PutMapping("/{id}")
	public EmprestimoResponse atualizar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id,
			@Valid @RequestBody EmprestimoUpdateRequest body) {
		return service.atualizar(authorization, id, body);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id) {
		service.excluir(authorization, id);
	}
}
