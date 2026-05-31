package com.example.portal.controller;

import com.example.portal.dto.ContinhasUsuarioFiltroResponse;
import com.example.portal.dto.ItemListaCompraResponse;
import com.example.portal.dto.ItemListaCompraUpdateRequest;
import com.example.portal.dto.ItemListaLinhaRequest;
import com.example.portal.dto.MercadoListaCardResponse;
import com.example.portal.dto.MercadoListaCreateRequest;
import com.example.portal.dto.MercadoListaDetalheResponse;
import com.example.portal.dto.MercadoListaResumoResponse;
import com.example.portal.dto.MercadoListaUpdateRequest;
import com.example.portal.service.ContinhasService;
import com.example.portal.service.MercadoListaService;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mercado/listas")
@RequiredArgsConstructor
public class MercadoListaController {

	private final MercadoListaService service;
	private final ContinhasService continhasService;

	@GetMapping("/usuarios")
	public List<ContinhasUsuarioFiltroResponse> usuariosFiltro(
			@RequestHeader(value = "Authorization", required = false) String authorization) {
		return continhasService.listarUsuariosFiltro(authorization);
	}

	@GetMapping
	public List<MercadoListaCardResponse> listar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
			@RequestParam(required = false) Long idLogin) {
		return service.listar(authorization, dataInicio, dataFim, idLogin);
	}

	@GetMapping("/resumo")
	public MercadoListaResumoResponse resumo(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
			@RequestParam(required = false) Long idLogin) {
		return service.resumo(authorization, dataInicio, dataFim, idLogin);
	}

	@GetMapping("/{id}")
	public MercadoListaDetalheResponse buscar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id) {
		return service.buscar(authorization, id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MercadoListaCardResponse criar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@Valid @RequestBody MercadoListaCreateRequest body) {
		return service.criar(authorization, body);
	}

	@PutMapping("/{id}")
	public MercadoListaCardResponse atualizar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id,
			@Valid @RequestBody MercadoListaUpdateRequest body) {
		return service.atualizar(authorization, id, body);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id) {
		service.excluir(authorization, id);
	}

	@PostMapping("/{id}/itens")
	@ResponseStatus(HttpStatus.CREATED)
	public ItemListaCompraResponse adicionarItem(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id,
			@Valid @RequestBody ItemListaLinhaRequest body) {
		return service.adicionarItem(authorization, id, body);
	}

	@PutMapping("/{id}/itens/{itemId}")
	public ItemListaCompraResponse atualizarItem(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id,
			@PathVariable Integer itemId,
			@Valid @RequestBody ItemListaCompraUpdateRequest body) {
		return service.atualizarItem(authorization, id, itemId, body);
	}

	@DeleteMapping("/{id}/itens/{itemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluirItem(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id,
			@PathVariable Integer itemId) {
		service.excluirItem(authorization, id, itemId);
	}
}
