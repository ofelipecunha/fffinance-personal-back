package com.example.portal.controller;

import com.example.portal.dto.ItemListaCompraResponse;
import com.example.portal.dto.ItemListaCompraUpdateRequest;
import com.example.portal.dto.ItemListaLinhaRequest;
import com.example.portal.dto.ListaCompraCreateRequest;
import com.example.portal.dto.ListaCompraDetalheResponse;
import com.example.portal.dto.ListaCompraResumoResponse;
import com.example.portal.dto.ListaCompraUpdateRequest;
import com.example.portal.service.ListaCompraService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/listas-compra")
@RequiredArgsConstructor
public class ListaCompraController {

	private final ListaCompraService service;

	@GetMapping
	public List<ListaCompraResumoResponse> listarPorTipo(@RequestParam("tipoId") Integer tipoId) {
		return service.listarPorTipo(tipoId);
	}

	@GetMapping("/{id}")
	public ListaCompraDetalheResponse buscar(@PathVariable Integer id) {
		return service.buscar(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ListaCompraDetalheResponse criar(@Valid @RequestBody ListaCompraCreateRequest body) {
		return service.criar(body);
	}

	@PutMapping("/{id}")
	public ListaCompraDetalheResponse atualizar(@PathVariable Integer id, @Valid @RequestBody ListaCompraUpdateRequest body) {
		return service.atualizarLista(id, body);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Integer id) {
		service.excluirLista(id);
	}

	@PostMapping("/{listaId}/itens")
	@ResponseStatus(HttpStatus.CREATED)
	public ItemListaCompraResponse adicionarItem(
			@PathVariable Integer listaId,
			@Valid @RequestBody ItemListaLinhaRequest body
	) {
		return service.adicionarItem(listaId, body);
	}

	@PutMapping("/{listaId}/itens/{itemId}")
	public ItemListaCompraResponse atualizarItem(
			@PathVariable Integer listaId,
			@PathVariable Integer itemId,
			@Valid @RequestBody ItemListaCompraUpdateRequest body
	) {
		return service.atualizarItem(listaId, itemId, body);
	}

	@DeleteMapping("/{listaId}/itens/{itemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluirItem(@PathVariable Integer listaId, @PathVariable Integer itemId) {
		service.excluirItem(listaId, itemId);
	}
}
