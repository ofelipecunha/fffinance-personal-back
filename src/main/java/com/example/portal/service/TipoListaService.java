package com.example.portal.service;

import com.example.portal.dto.TipoListaCardResponse;
import com.example.portal.repository.TipoListaRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TipoListaService {

	private final TipoListaRepository repository;

	public List<TipoListaCardResponse> listarCards() {
		return repository.findCardResumoRows().stream().map(this::mapearLinha).toList();
	}

	private TipoListaCardResponse mapearLinha(Object[] r) {
		return new TipoListaCardResponse(
				((Number) r[0]).intValue(),
				(String) r[1],
				(String) r[2],
				r[3] != null ? (String) r[3] : null,
				r[4] != null ? (String) r[4] : null,
				((Number) r[5]).intValue(),
				numeroParaLong(r[6]),
				numeroParaBigDecimal(r[7])
		);
	}

	private static long numeroParaLong(Object v) {
		if (v == null) {
			return 0L;
		}
		return ((Number) v).longValue();
	}

	private static BigDecimal numeroParaBigDecimal(Object v) {
		if (v == null) {
			return BigDecimal.ZERO;
		}
		if (v instanceof BigDecimal bd) {
			return bd;
		}
		return BigDecimal.valueOf(((Number) v).doubleValue());
	}
}
