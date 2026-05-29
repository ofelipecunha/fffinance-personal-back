package com.example.portal.controller;

import com.example.portal.dto.TipoListaCardResponse;
import com.example.portal.service.TipoListaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tipos-lista")
@RequiredArgsConstructor
public class TipoListaController {

	private final TipoListaService service;

	/** Cards da tela LISTA (tipos + totais agregados). */
	@GetMapping
	public List<TipoListaCardResponse> listar() {
		return service.listarCards();
	}
}
