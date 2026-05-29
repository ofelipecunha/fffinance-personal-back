package com.example.portal.service;

import com.example.portal.dto.CategoriaCreateRequest;
import com.example.portal.dto.CategoriaResponse;
import com.example.portal.dto.CategoriaUpdateRequest;
import com.example.portal.entity.Categoria;
import com.example.portal.repository.CategoriaRepository;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CategoriaService {

	private final CategoriaRepository repository;

	public List<CategoriaResponse> listar() {
		return repository.findAllOrderedByNomeIgnoreCase().stream()
				.map(this::paraResponse)
				.toList();
	}

	public CategoriaResponse buscar(Integer id) {
		return repository.findById(id)
				.map(this::paraResponse)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
	}

	@Transactional
	public CategoriaResponse criar(CategoriaCreateRequest dto) {
		Categoria e = new Categoria();
		e.setNome(dto.nome().trim());
		e.setTipo(normalizarTipo(dto.tipo()));
		e.setAtivo(dto.ativo() != null ? dto.ativo() : Boolean.TRUE);
		return paraResponse(repository.save(e));
	}

	@Transactional
	public CategoriaResponse atualizar(Integer id, CategoriaUpdateRequest dto) {
		Categoria e = repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
		e.setNome(dto.nome().trim());
		e.setTipo(normalizarTipo(dto.tipo()));
		e.setAtivo(dto.ativo());
		return paraResponse(repository.save(e));
	}

	@Transactional
	public void excluir(Integer id) {
		if (!repository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada");
		}
		repository.deleteById(id);
	}

	private CategoriaResponse paraResponse(Categoria e) {
		return new CategoriaResponse(e.getId(), e.getNome(), e.getTipo(), e.getAtivo());
	}

	/** Espera RECEITA ou DESPESA (case insensitive). Persiste em maiúsculas. */
	private static String normalizarTipo(String raw) {
		String t = raw.trim().toUpperCase(Locale.ROOT);
		if (!"RECEITA".equals(t) && !"DESPESA".equals(t)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo deve ser RECEITA ou DESPESA");
		}
		return t;
	}
}
