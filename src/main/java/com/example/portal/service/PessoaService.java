package com.example.portal.service;

import com.example.portal.dto.PessoaResponse;
import com.example.portal.repository.PessoaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PessoaService {

	private final PessoaRepository pessoaRepository;

	@Transactional(readOnly = true)
	public List<PessoaResponse> listarAtivas() {
		return pessoaRepository.findByAtivoTrueOrderByNomeAsc().stream()
				.map(p -> new PessoaResponse(p.getId(), p.getNome(), p.getAtivo(), p.getDataCriacao()))
				.toList();
	}
}
