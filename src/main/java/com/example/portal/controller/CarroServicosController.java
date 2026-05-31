package com.example.portal.controller;

import com.example.portal.dto.CarroServicoCreateRequest;
import com.example.portal.dto.CarroServicoResponse;
import com.example.portal.service.CarroServicosService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carro/servicos")
@RequiredArgsConstructor
public class CarroServicosController {

	private final CarroServicosService service;

	@GetMapping
	public List<CarroServicoResponse> listar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestParam(required = false) String descricao) {
		return service.listar(authorization, descricao);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CarroServicoResponse criar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@Valid @RequestBody CarroServicoCreateRequest body) {
		return service.criar(authorization, body);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id) {
		service.excluir(authorization, id);
	}
}
