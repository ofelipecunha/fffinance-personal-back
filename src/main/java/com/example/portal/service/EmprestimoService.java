package com.example.portal.service;

import com.example.portal.dto.EmprestimoCreateRequest;
import com.example.portal.dto.EmprestimoResponse;
import com.example.portal.dto.EmprestimoUpdateRequest;
import com.example.portal.entity.Emprestimo;
import com.example.portal.entity.LoginUsuario;
import com.example.portal.repository.EmprestimoRepository;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

	private static final Set<String> BANCOS_VALIDOS =
			Set.of(
					"NUBANK",
					"BANCO_DO_BRASIL",
					"SANTANDER",
					"ITAU",
					"BRADESCO",
					"BANCO_PAN",
					"WILL",
					"NEON");

	private final EmprestimoRepository repository;
	private final AuthService authService;

	@Transactional(readOnly = true)
	public List<EmprestimoResponse> listar(String authorization, String descricao) {
		Long idLogin = authService.requireUsuarioPorBearer(authorization).getIdLogin();
		String filtro = descricao != null ? descricao.trim() : "";
		return repository.listarPorUsuario(idLogin, filtro).stream().map(this::paraResponse).toList();
	}

	@Transactional(readOnly = true)
	public EmprestimoResponse buscar(String authorization, Integer id) {
		Long idLogin = authService.requireUsuarioPorBearer(authorization).getIdLogin();
		return repository
				.findByIdAndIdLogin(id, idLogin)
				.map(this::paraResponse)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empréstimo não encontrado"));
	}

	@Transactional
	public EmprestimoResponse criar(String authorization, EmprestimoCreateRequest body) {
		LoginUsuario usuario = authService.requireUsuarioPorBearer(authorization);
		Emprestimo e = new Emprestimo();
		e.setIdLogin(usuario.getIdLogin());
		aplicarDados(e, body.descricao(), body.banco(), body.valorEmprestimo(), body.quantidadeParcelas());
		return paraResponse(repository.save(e));
	}

	@Transactional
	public EmprestimoResponse atualizar(String authorization, Integer id, EmprestimoUpdateRequest body) {
		Long idLogin = authService.requireUsuarioPorBearer(authorization).getIdLogin();
		Emprestimo e =
				repository
						.findByIdAndIdLogin(id, idLogin)
						.orElseThrow(
								() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empréstimo não encontrado"));
		aplicarDados(e, body.descricao(), body.banco(), body.valorEmprestimo(), body.quantidadeParcelas());
		return paraResponse(repository.save(e));
	}

	@Transactional
	public void excluir(String authorization, Integer id) {
		Long idLogin = authService.requireUsuarioPorBearer(authorization).getIdLogin();
		Emprestimo e =
				repository
						.findByIdAndIdLogin(id, idLogin)
						.orElseThrow(
								() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empréstimo não encontrado"));
		repository.delete(e);
	}

	private void aplicarDados(
			Emprestimo e, String descricao, String banco, java.math.BigDecimal valor, Integer parcelas) {
		e.setDescricao(descricao.trim());
		e.setBanco(normalizarBanco(banco));
		e.setValorEmprestimo(valor);
		e.setQuantidadeParcelas(parcelas);
	}

	private static String normalizarBanco(String banco) {
		if (!StringUtils.hasText(banco)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Banco inválido");
		}
		String codigo = banco.trim().toUpperCase(Locale.ROOT);
		if (!BANCOS_VALIDOS.contains(codigo)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Banco inválido");
		}
		return codigo;
	}

	private EmprestimoResponse paraResponse(Emprestimo e) {
		return EmprestimoResponse.builder()
				.id(e.getId())
				.descricao(e.getDescricao())
				.banco(e.getBanco())
				.valorEmprestimo(e.getValorEmprestimo())
				.quantidadeParcelas(e.getQuantidadeParcelas())
				.dataCriacao(e.getDataCriacao())
				.build();
	}
}
