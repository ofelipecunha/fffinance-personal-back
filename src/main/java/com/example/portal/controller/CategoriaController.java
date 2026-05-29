package com.example.portal.controller;

import com.example.portal.dto.CategoriaCreateRequest;
import com.example.portal.dto.CategoriaResponse;
import com.example.portal.dto.CategoriaUpdateRequest;
import com.example.portal.service.CategoriaService;
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
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

	private final CategoriaService service;

	@GetMapping
	public List<CategoriaResponse> listar() {
		return service.listar();
	}

	/** Detalhe (edição ou leitura). */
	@GetMapping("/{id}")
	public CategoriaResponse porId(@PathVariable Integer id) {
		return service.buscar(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CategoriaResponse criar(@Valid @RequestBody CategoriaCreateRequest body) {
		return service.criar(body);
	}

	@PutMapping("/{id}")
	public CategoriaResponse atualizar(@PathVariable Integer id, @Valid @RequestBody CategoriaUpdateRequest body) {
		return service.atualizar(id, body);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Integer id) {
		service.excluir(id);
	}
}
