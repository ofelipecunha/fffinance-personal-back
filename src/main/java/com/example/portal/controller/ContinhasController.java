package com.example.portal.controller;

import com.example.portal.dto.ContinhasCardResponse;
import com.example.portal.dto.ContinhasPagoRequest;
import com.example.portal.dto.ContinhasResumoResponse;
import com.example.portal.dto.LancamentoCreateRequest;
import com.example.portal.dto.LancamentoUpdateRequest;
import com.example.portal.service.ContinhasService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/api/continhas")
@RequiredArgsConstructor
public class ContinhasController {

	private final ContinhasService service;

	@GetMapping
	public List<ContinhasCardResponse> listar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
		return service.listar(authorization, dataInicio, dataFim);
	}

	@GetMapping("/resumo")
	public ContinhasResumoResponse resumo(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
		return service.resumo(authorization, dataInicio, dataFim);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ContinhasCardResponse criar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@Valid @RequestBody LancamentoCreateRequest body) {
		return service.criar(authorization, body);
	}

	@PutMapping("/{id}")
	public ContinhasCardResponse atualizar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id,
			@Valid @RequestBody LancamentoUpdateRequest body) {
		return service.atualizar(authorization, id, body);
	}

	@PatchMapping("/{id}/pago")
	public ContinhasCardResponse alternarPago(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id,
			@Valid @RequestBody ContinhasPagoRequest body) {
		return service.alternarPago(authorization, id, body);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id) {
		service.excluir(authorization, id);
	}
}
