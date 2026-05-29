package com.example.portal.controller;

import com.example.portal.dto.PessoaResponse;
import com.example.portal.service.PessoaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pessoas")
@RequiredArgsConstructor
public class PessoaController {

	private final PessoaService pessoaService;

	@GetMapping
	public List<PessoaResponse> listar() {
		return pessoaService.listarAtivas();
	}
}
