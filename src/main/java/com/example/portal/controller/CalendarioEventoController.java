package com.example.portal.controller;

import com.example.portal.dto.CalendarioEventoCreateRequest;
import com.example.portal.dto.CalendarioEventoResponse;
import com.example.portal.dto.CalendarioEventoUpdateRequest;
import com.example.portal.service.CalendarioEventoService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calendario-eventos")
@RequiredArgsConstructor
public class CalendarioEventoController {

	private final CalendarioEventoService service;

	@GetMapping
	public List<CalendarioEventoResponse> listar(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
		return service.listar(dataInicio, dataFim);
	}

	@GetMapping("/{id}")
	public CalendarioEventoResponse porId(@PathVariable Integer id) {
		return service.buscar(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CalendarioEventoResponse criar(@Valid @RequestBody CalendarioEventoCreateRequest body) {
		return service.criar(body);
	}

	@PutMapping("/{id}")
	public CalendarioEventoResponse atualizar(
			@PathVariable Integer id,
			@Valid @RequestBody CalendarioEventoUpdateRequest body) {
		return service.atualizar(id, body);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Integer id) {
		service.excluir(id);
	}
}
