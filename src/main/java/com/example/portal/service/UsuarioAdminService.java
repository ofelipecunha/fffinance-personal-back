package com.example.portal.service;

import com.example.portal.dto.UsuarioCreateRequest;
import com.example.portal.dto.UsuarioDto;
import com.example.portal.dto.UsuarioUpdateRequest;
import com.example.portal.entity.LoginUsuario;
import com.example.portal.repository.LoginUsuarioRepository;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UsuarioAdminService {

	private static final String PERFIL_PADRAO = "USUARIO";
	private static final Set<String> PERFIS_VALIDOS = Set.of("ADMIN", "USUARIO");

	private final LoginUsuarioRepository loginUsuarioRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public List<UsuarioDto> listar(String nome) {
		String filtro = nome != null ? nome.trim() : "";
		return loginUsuarioRepository.listarPorNome(filtro).stream().map(UsuarioAdminService::toDto).toList();
	}

	@Transactional(readOnly = true)
	public UsuarioDto buscarPorId(Long id) {
		LoginUsuario usuario =
				loginUsuarioRepository
						.findById(id)
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
		return toDto(usuario);
	}

	@Transactional
	public UsuarioDto criar(UsuarioCreateRequest body) {
		validarUnicidadeLoginEmail(body.getLogin(), body.getEmail(), null);

		LoginUsuario usuario = new LoginUsuario();
		usuario.setNome(body.getNome().trim());
		usuario.setLogin(body.getLogin().trim());
		usuario.setEmail(body.getEmail().trim());
		usuario.setSenhaHash(passwordEncoder.encode(body.getSenha().trim()));
		usuario.setTelefone(trimOrNull(body.getTelefone()));
		usuario.setPerfil(normalizarPerfil(body.getPerfil()));
		usuario.setAtivo(normalizarAtivo(body.getAtivo()));
		usuario.setToken(null);

		return toDto(loginUsuarioRepository.save(usuario));
	}

	@Transactional
	public UsuarioDto atualizar(Long id, UsuarioUpdateRequest body) {
		LoginUsuario usuario =
				loginUsuarioRepository
						.findById(id)
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

		validarUnicidadeLoginEmail(body.getLogin(), body.getEmail(), id);

		usuario.setNome(body.getNome().trim());
		usuario.setLogin(body.getLogin().trim());
		usuario.setEmail(body.getEmail().trim());
		if (StringUtils.hasText(body.getSenha())) {
			String senha = body.getSenha().trim();
			if (senha.length() < 6) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha deve ter no mínimo 6 caracteres");
			}
			usuario.setSenhaHash(passwordEncoder.encode(senha));
			usuario.setToken(null);
		}
		usuario.setTelefone(trimOrNull(body.getTelefone()));
		usuario.setPerfil(normalizarPerfil(body.getPerfil()));
		usuario.setAtivo(normalizarAtivo(body.getAtivo()));

		return toDto(loginUsuarioRepository.save(usuario));
	}

	@Transactional
	public void excluir(Long id) {
		if (!loginUsuarioRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
		}
		loginUsuarioRepository.deleteById(id);
	}

	private void validarUnicidadeLoginEmail(String login, String email, Long idExcluir) {
		String loginNorm = login != null ? login.trim() : "";
		String emailNorm = email != null ? email.trim() : "";
		if (idExcluir == null) {
			if (loginUsuarioRepository.existsByLoginNormalized(loginNorm)) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já cadastrado");
			}
			if (loginUsuarioRepository.existsByEmailNormalized(emailNorm)) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
			}
		} else {
			if (loginUsuarioRepository.existsLoginForOther(loginNorm, idExcluir)) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já cadastrado");
			}
			if (loginUsuarioRepository.existsEmailForOther(emailNorm, idExcluir)) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
			}
		}
	}

	private static String normalizarPerfil(String perfil) {
		if (!StringUtils.hasText(perfil)) {
			return PERFIL_PADRAO;
		}
		String codigo = perfil.trim().toUpperCase(Locale.ROOT);
		if (!PERFIS_VALIDOS.contains(codigo)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Perfil inválido");
		}
		return codigo;
	}

	private static String normalizarAtivo(String ativo) {
		if (!StringUtils.hasText(ativo)) {
			return "S";
		}
		String flag = ativo.trim().toUpperCase(Locale.ROOT);
		return flag.equals("N") ? "N" : "S";
	}

	private static String trimOrNull(String value) {
		if (value == null) {
			return null;
		}
		String t = value.trim();
		return t.isEmpty() ? null : t;
	}

	private static UsuarioDto toDto(LoginUsuario u) {
		String login = StringUtils.hasText(u.getLogin()) ? u.getLogin() : u.getEmail();
		return UsuarioDto.builder()
				.idUsuario(u.getIdLogin() != null ? u.getIdLogin().intValue() : null)
				.nome(u.getNome())
				.login(login)
				.email(u.getEmail())
				.telefone(u.getTelefone())
				.perfil(u.getPerfil())
				.ativo(u.getAtivo() != null ? u.getAtivo() : "S")
				.dataCadastro(u.getDataCriacao())
				.build();
	}
}
