package com.example.portal.controller;

import com.example.portal.dto.AlterarSenhaRequest;
import com.example.portal.dto.LoginRequest;
import com.example.portal.dto.LoginResponse;
import com.example.portal.dto.MensagemResponse;
import com.example.portal.dto.PerfilUsuarioResponse;
import com.example.portal.dto.PerfilUsuarioUpdateRequest;
import com.example.portal.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	/**
	 * Resumo dos endpoints de autenticação (útil no Postman).
	 */
	@GetMapping
	public Map<String, Object> infoAuth() {
		return Map.of(
				"servico", "portal-auth",
				"loginPost", Map.of(
						"metodo", "POST",
						"url", "/api/auth/login",
						"bodyJson", "{ \"email\": \"...\", \"senha\": \"...\" }"),
				"loginGetDev", Map.of(
						"metodo", "GET",
						"url", "/api/auth/login?email=...&senha=...",
						"aviso", "Senha na URL é insegura; use só em desenvolvimento."),
				"alterarSenha", Map.of(
						"metodo", "PUT",
						"url", "/api/auth/senha",
						"bodyJson", "{ \"email\": \"...\", \"senhaAtual\": \"...\", \"novaSenha\": \"...\" }"),
				"perfilAtual", Map.of(
						"metodo", "GET",
						"url", "/api/auth/me",
						"cabecalho", "Authorization: Bearer <token retornado no login>"),
				"uploadImagem", Map.of(
						"metodo", "POST",
						"url", "/api/auth/imagem",
						"cabecalho", "Authorization: Bearer <token>",
						"body", "multipart/form-data, campo file"));
	}

	/**
	 * Login via query string (ex.: Postman GET). Em produção prefira {@link #login(LoginRequest)} (POST + JSON).
	 */
	@GetMapping("/login")
	public ResponseEntity<LoginResponse> loginGet(
			@RequestParam String email,
			@RequestParam String senha) {
		if (email.isBlank() || senha.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe email e senha");
		}
		return ResponseEntity.ok(authService.login(new LoginRequest(email.trim(), senha)));
	}

	/**
	 * Autentica por e-mail e senha, persiste um token opaco em {@code login_usuario.token} e retorna os dados (sem hash de senha).
	 */
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest body) {
		return ResponseEntity.ok(authService.login(body));
	}

	/**
	 * Dados do utilizador autenticado (nome, imagem, contactos, endereço, redes).
	 * Requer {@code Authorization: Bearer <token>} igual ao devolvido no login.
	 */
	@GetMapping("/me")
	public ResponseEntity<PerfilUsuarioResponse> perfilAtual(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
		return ResponseEntity.ok(authService.perfilDoToken(authorization));
	}

	@PutMapping("/perfil")
	public ResponseEntity<PerfilUsuarioResponse> atualizarPerfil(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
			@Valid @RequestBody PerfilUsuarioUpdateRequest body) {
		return ResponseEntity.ok(authService.atualizarPerfil(authorization, body));
	}

	/**
	 * Atualiza a foto de perfil (campo multipart {@code file}). Grava ficheiro em disco e persiste o caminho em
	 * {@code login_usuario.imagem} (URL pública {@code /api/files/avatars/...}).
	 */
	@PostMapping(value = "/imagem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<PerfilUsuarioResponse> uploadImagem(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
			@RequestPart("file") MultipartFile file) {
		return ResponseEntity.ok(authService.atualizarImagemPerfil(authorization, file));
	}

	/**
	 * Atualiza {@code senha_hash} com BCrypt da nova senha. Exige {@code senhaAtual} correta. Limpa {@code token} (faça login de novo).
	 */
	@PutMapping("/senha")
	public ResponseEntity<MensagemResponse> alterarSenha(@Valid @RequestBody AlterarSenhaRequest body) {
		authService.alterarSenha(body);
		return ResponseEntity.ok(new MensagemResponse("Senha atualizada com sucesso"));
	}
}
