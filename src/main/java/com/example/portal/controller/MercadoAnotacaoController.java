package com.example.portal.controller;

import com.example.portal.dto.AnotacaoCardResponse;
import com.example.portal.dto.AnotacaoCreateRequest;
import com.example.portal.dto.AnotacaoDetalheResponse;
import com.example.portal.dto.AnotacaoItemLinhaRequest;
import com.example.portal.dto.AnotacaoItemResponse;
import com.example.portal.dto.AnotacaoRapidoRequest;
import com.example.portal.dto.VirarListaResponse;
import com.example.portal.service.MercadoAnotacaoService;
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
@RequestMapping("/api/mercado/anotacoes")
@RequiredArgsConstructor
public class MercadoAnotacaoController {

	private final MercadoAnotacaoService service;

	@GetMapping
	public List<AnotacaoCardResponse> listarAbertas(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestParam(required = false) Long idLogin) {
		return service.listarAbertas(authorization, idLogin);
	}

	@GetMapping("/{id}")
	public AnotacaoDetalheResponse buscar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id) {
		return service.buscar(authorization, id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public AnotacaoCardResponse criar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@Valid @RequestBody AnotacaoCreateRequest body) {
		return service.criar(authorization, body);
	}

	@PostMapping("/rapido")
	public AnotacaoDetalheResponse adicionarRapido(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@Valid @RequestBody AnotacaoRapidoRequest body) {
		return service.adicionarRapido(authorization, body);
	}

	@PostMapping("/{id}/itens")
	@ResponseStatus(HttpStatus.CREATED)
	public AnotacaoItemResponse adicionarItem(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id,
			@Valid @RequestBody AnotacaoItemLinhaRequest body) {
		return service.adicionarItem(authorization, id, body);
	}

	@PostMapping("/{id}/virar-lista")
	public VirarListaResponse virarLista(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id) {
		return service.virarLista(authorization, id);
	}

	@DeleteMapping("/{id}/itens/{itemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluirItem(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id,
			@PathVariable Integer itemId) {
		service.excluirItem(authorization, id, itemId);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@PathVariable Integer id) {
		service.excluirAnotacao(authorization, id);
	}
}
