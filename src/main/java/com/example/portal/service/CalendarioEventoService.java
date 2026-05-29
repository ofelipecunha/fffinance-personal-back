package com.example.portal.service;

import com.example.portal.dto.CalendarioEventoCreateRequest;
import com.example.portal.dto.CalendarioEventoResponse;
import com.example.portal.dto.CalendarioEventoUpdateRequest;
import com.example.portal.entity.CalendarioEvento;
import com.example.portal.repository.CalendarioEventoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CalendarioEventoService {

	private final CalendarioEventoRepository repository;

	public List<CalendarioEventoResponse> listar(LocalDate dataInicio, LocalDate dataFim) {
		List<CalendarioEvento> lista;
		if (dataInicio != null && dataFim != null) {
			lista = repository.findByDataEventoBetweenOrderByDataEventoAscIdAsc(dataInicio, dataFim);
		} else {
			lista = repository.findAll();
		}
		return lista.stream().map(this::paraResponse).toList();
	}

	public CalendarioEventoResponse buscar(Integer id) {
		return repository.findById(id)
				.map(this::paraResponse)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
	}

	@Transactional
	public CalendarioEventoResponse criar(CalendarioEventoCreateRequest dto) {
		CalendarioEvento e = new CalendarioEvento();
		e.setDescricao(dto.descricao().trim());
		e.setDataEvento(dto.dataEvento());
		e.setTipo(normalizarTipo(dto.tipo()));
		e.setConcluido(dto.concluido() != null ? dto.concluido() : Boolean.FALSE);
		return paraResponse(repository.save(e));
	}

	@Transactional
	public CalendarioEventoResponse atualizar(Integer id, CalendarioEventoUpdateRequest dto) {
		CalendarioEvento e = repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
		e.setDescricao(dto.descricao().trim());
		e.setDataEvento(dto.dataEvento());
		e.setTipo(normalizarTipo(dto.tipo()));
		e.setConcluido(dto.concluido());
		return paraResponse(repository.save(e));
	}

	@Transactional
	public void excluir(Integer id) {
		if (!repository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado");
		}
		repository.deleteById(id);
	}

	private CalendarioEventoResponse paraResponse(CalendarioEvento e) {
		return new CalendarioEventoResponse(
				e.getId(),
				e.getDescricao(),
				e.getDataEvento(),
				e.getTipo(),
				e.getConcluido(),
				e.getDataCriacao()
		);
	}

	private static String normalizarTipo(String raw) {
		if (raw == null || raw.isBlank()) {
			return "NOTACAO";
		}
		return raw.trim().toUpperCase(Locale.ROOT);
	}
}
