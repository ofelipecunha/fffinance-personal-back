package com.example.portal.service;

import com.example.portal.dto.AlterarSenhaRequest;
import com.example.portal.dto.LoginRequest;
import com.example.portal.dto.LoginResponse;
import com.example.portal.dto.PerfilUsuarioResponse;
import com.example.portal.dto.PerfilUsuarioUpdateRequest;
import com.example.portal.entity.LoginUsuario;
import com.example.portal.repository.LoginUsuarioRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

	private static final Logger log = LoggerFactory.getLogger(AuthService.class);
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
			MediaType.IMAGE_JPEG_VALUE,
			MediaType.IMAGE_PNG_VALUE,
			MediaType.IMAGE_GIF_VALUE,
			"image/webp");

	private final LoginUsuarioRepository loginUsuarioRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.upload.avatars-dir:data/avatars}")
	private String avatarsUploadDir;

	@Value("${app.upload.max-avatar-mb:25}")
	private int maxAvatarMb;

	@Transactional
	public LoginResponse login(LoginRequest request) {
		String loginNorm = request.getLogin() != null ? request.getLogin().trim() : "";
		String senhaDigitada = request.getSenha() != null ? request.getSenha().trim() : "";

		LoginUsuario usuario = loginUsuarioRepository
				.findByLoginNormalized(loginNorm)
				.orElseThrow(() -> {
					log.warn("Login falhou: nenhum registo com login (após trim) igual a [{}]", loginNorm);
					return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
				});

		if (!isAtivo(usuario)) {
			log.warn("Login falhou: utilizador inativo, login [{}]", loginNorm);
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário inativo");
		}

		if (!senhaConfereComArmazenado(senhaDigitada, usuario.getSenhaHash())) {
			int hashLen = usuario.getSenhaHash() != null ? usuario.getSenhaHash().length() : -1;
			boolean bcrypt = isBcryptHash(usuario.getSenhaHash());
			log.warn(
					"Login falhou: senha incorreta para login [{}]. Hash no BD: tamanho={}, parece BCrypt={}",
					loginNorm,
					hashLen,
					bcrypt);
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
		}

		/* Migração: se ainda estiver em texto plano no BD, passa a gravar BCrypt */
		if (!isBcryptHash(usuario.getSenhaHash())) {
			usuario.setSenhaHash(passwordEncoder.encode(senhaDigitada));
		}

		String novoToken = gerarTokenSeguro();
		usuario.setToken(novoToken);
		loginUsuarioRepository.save(usuario);

		return LoginResponse.builder()
				.idLogin(usuario.getIdLogin())
				.nome(usuario.getNome())
				.email(usuario.getEmail())
				.token(novoToken)
				.imagem(usuario.getImagem())
				.perfil(perfilAcesso(usuario))
				.build();
	}

	/**
	 * Resolve o utilizador a partir do cabeçalho {@code Authorization: Bearer &lt;token&gt;}
	 * (token opaco gravado em {@code login_usuario.token}).
	 */
	@Transactional
	public LoginUsuario requireUsuarioPorBearer(String authorizationHeader) {
		String token = extrairBearerToken(authorizationHeader);
		return loginUsuarioRepository
				.findByToken(token)
				.filter(AuthService::isAtivo)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão inválida ou expirada"));
	}

	@Transactional(readOnly = true)
	public PerfilUsuarioResponse perfilDoToken(String authorizationHeader) {
		String token = extrairBearerToken(authorizationHeader);
		LoginUsuario usuario = loginUsuarioRepository
				.findByToken(token)
				.filter(AuthService::isAtivo)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão inválida ou expirada"));
		return toPerfil(usuario);
	}

	@Transactional
	public PerfilUsuarioResponse atualizarPerfil(String authorizationHeader, PerfilUsuarioUpdateRequest body) {
		LoginUsuario usuario = requireUsuarioPorBearer(authorizationHeader);
		if (body == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Corpo da requisição em falta");
		}
		if (StringUtils.hasText(body.getNome())) {
			usuario.setNome(body.getNome().trim());
		}
		if (StringUtils.hasText(body.getEmail())) {
			String emailNorm = body.getEmail().trim();
			if (loginUsuarioRepository.existsEmailForOther(emailNorm, usuario.getIdLogin())) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já está em uso");
			}
			usuario.setEmail(emailNorm);
		}
		if (body.getTelefone() != null) {
			usuario.setTelefone(body.getTelefone().isBlank() ? null : body.getTelefone().trim());
		}
		if (body.getEndereco() != null) {
			usuario.setEndereco(body.getEndereco().isBlank() ? null : body.getEndereco().trim());
		}
		if (body.getCidade() != null) {
			usuario.setCidade(body.getCidade().isBlank() ? null : body.getCidade().trim());
		}
		if (body.getEstado() != null) {
			String uf = body.getEstado().trim().toUpperCase(Locale.ROOT);
			usuario.setEstado(uf.isBlank() ? null : uf);
		}
		loginUsuarioRepository.save(usuario);
		return toPerfil(usuario);
	}

	/**
	 * Grava ficheiro de imagem em disco, atualiza {@link LoginUsuario#getImagem()} com URL pública
	 * {@code /api/files/avatars/&lt;ficheiro&gt;} e remove o avatar anterior se for gerido por esta API.
	 */
	@Transactional
	public PerfilUsuarioResponse atualizarImagemPerfil(String authorizationHeader, MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ficheiro em falta");
		}
		if (file.getSize() > maxAvatarBytes()) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, "Imagem demasiado grande (máx. " + maxAvatarMb + " MB)");
		}
		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, "Tipo de ficheiro não suportado (use JPEG, PNG, GIF ou WebP)");
		}

		LoginUsuario usuario = requireUsuarioPorBearer(authorizationHeader);
		deleteManagedAvatarFile(usuario.getImagem());

		Path dir = Paths.get(avatarsUploadDir).toAbsolutePath().normalize();
		try {
			Files.createDirectories(dir);
		} catch (IOException e) {
			log.error("Não foi possível criar pasta de avatares: {}", dir, e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao preparar armazenamento");
		}

		String ext = extensaoSegura(file, contentType);
		String filename = usuario.getIdLogin() + "-" + UUID.randomUUID() + ext;
		Path target = dir.resolve(filename);

		try (InputStream in = file.getInputStream()) {
			Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			log.error("Falha ao gravar avatar para {}", target, e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gravar imagem");
		}

		String publicPath = "/api/files/avatars/" + filename;
		usuario.setImagem(publicPath);
		usuario.setImagemUrl(publicPath);
		loginUsuarioRepository.save(usuario);
		return toPerfil(usuario);
	}

	private long maxAvatarBytes() {
		return Math.max(1, maxAvatarMb) * 1024L * 1024L;
	}

	private void deleteManagedAvatarFile(String imagemUrl) {
		if (!StringUtils.hasText(imagemUrl) || !imagemUrl.contains("/api/files/avatars/")) {
			return;
		}
		int idx = imagemUrl.lastIndexOf("/api/files/avatars/");
		String name = imagemUrl.substring(idx + "/api/files/avatars/".length());
		if (name.isBlank() || name.contains("..") || name.contains("/") || name.contains("\\")) {
			return;
		}
		Path dir = Paths.get(avatarsUploadDir).toAbsolutePath().normalize();
		Path old = dir.resolve(name).normalize();
		if (!old.startsWith(dir)) {
			return;
		}
		try {
			Files.deleteIfExists(old);
		} catch (IOException e) {
			log.warn("Não foi possível apagar avatar antigo: {}", old, e);
		}
	}

	private static String extensaoSegura(MultipartFile file, String contentType) {
		String original = file.getOriginalFilename();
		String lower = original != null ? original.toLowerCase(Locale.ROOT) : "";
		if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
			return ".jpg";
		}
		if (lower.endsWith(".png")) {
			return ".png";
		}
		if (lower.endsWith(".gif")) {
			return ".gif";
		}
		if (lower.endsWith(".webp")) {
			return ".webp";
		}
		if (MediaType.IMAGE_JPEG_VALUE.equalsIgnoreCase(contentType)) {
			return ".jpg";
		}
		if (MediaType.IMAGE_PNG_VALUE.equalsIgnoreCase(contentType)) {
			return ".png";
		}
		if (MediaType.IMAGE_GIF_VALUE.equalsIgnoreCase(contentType)) {
			return ".gif";
		}
		return ".webp";
	}

	private static String extrairBearerToken(String authorizationHeader) {
		if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token ausente");
		}
		String token = authorizationHeader.substring(7).trim();
		if (!StringUtils.hasText(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token ausente");
		}
		return token;
	}

	private static boolean isAtivo(LoginUsuario u) {
		if (u == null || u.getAtivo() == null) {
			return true;
		}
		return "S".equalsIgnoreCase(u.getAtivo().trim());
	}

	private static String perfilAcesso(LoginUsuario u) {
		String p = u.getPerfil();
		if (!StringUtils.hasText(p)) {
			return "USUARIO";
		}
		return p.trim().toUpperCase(Locale.ROOT);
	}

	private static String imagemPublica(LoginUsuario u) {
		if (StringUtils.hasText(u.getImagemUrl())) {
			return u.getImagemUrl().trim();
		}
		return u.getImagem();
	}

	private static PerfilUsuarioResponse toPerfil(LoginUsuario u) {
		return PerfilUsuarioResponse.builder()
				.idLogin(u.getIdLogin())
				.nome(u.getNome())
				.sobrenome(u.getSobrenome())
				.email(u.getEmail())
				.endereco(u.getEndereco())
				.cidade(u.getCidade())
				.estado(u.getEstado())
				.imagem(imagemPublica(u))
				.telefone(u.getTelefone())
				.ativo(u.getAtivo())
				.bio(u.getBio())
				.cargo(u.getCargo())
				.localizacao(u.getLocalizacao())
				.pais(u.getPais())
				.cidadeEstado(u.getCidadeEstado())
				.cep(u.getCep())
				.taxId(u.getTaxId())
				.urlFacebook(u.getUrlFacebook())
				.urlTwitter(u.getUrlTwitter())
				.urlLinkedin(u.getUrlLinkedin())
				.urlInstagram(u.getUrlInstagram())
				.perfil(perfilAcesso(u))
				.build();
	}

	/**
	 * Atualiza a senha (gravada como BCrypt em {@code senha_hash}). Exige a senha atual correta.
	 * Invalida o token anterior para forçar novo login.
	 */
	@Transactional
	public void alterarSenha(AlterarSenhaRequest request) {
		LoginUsuario usuario = loginUsuarioRepository
				.findByEmailNormalized(request.getEmail().trim())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

		if (!isAtivo(usuario)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário inativo");
		}

		if (!senhaConfereComArmazenado(request.getSenhaAtual(), usuario.getSenhaHash())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha atual incorreta");
		}

		if (mesmaSenhaQueArmazenada(request.getNovaSenha(), usuario.getSenhaHash())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A nova senha deve ser diferente da atual");
		}

		usuario.setSenhaHash(passwordEncoder.encode(request.getNovaSenha()));
		usuario.setToken(null);
		loginUsuarioRepository.save(usuario);
	}

	private static String gerarTokenSeguro() {
		byte[] bytes = new byte[48];
		SECURE_RANDOM.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	/** BCrypt típico: 60 chars, prefixo $2a$ / $2b$ / $2y$ */
	private static boolean isBcryptHash(String armazenado) {
		if (armazenado == null || armazenado.length() < 60) {
			return false;
		}
		return armazenado.startsWith("$2a$")
				|| armazenado.startsWith("$2b$")
				|| armazenado.startsWith("$2y$");
	}

	/**
	 * Aceita hash BCrypt ou, temporariamente, texto plano legado (para migração a partir de dados antigos).
	 * Em produção, mantenha apenas BCrypt no banco.
	 */
	private boolean senhaConfereComArmazenado(String senhaDigitada, String armazenado) {
		if (armazenado == null || senhaDigitada == null) {
			return false;
		}
		String digitada = senhaDigitada.trim();
		if (digitada.isEmpty()) {
			return false;
		}
		if (isBcryptHash(armazenado)) {
			return passwordEncoder.matches(digitada, armazenado);
		}
		/* legado texto plano: ignora espaços no fim do valor vindo do BD */
		return digitada.equals(armazenado.trim());
	}

	private boolean mesmaSenhaQueArmazenada(String novaSenhaPlano, String armazenado) {
		if (armazenado == null || novaSenhaPlano == null) {
			return false;
		}
		String nova = novaSenhaPlano.trim();
		if (isBcryptHash(armazenado)) {
			return passwordEncoder.matches(nova, armazenado);
		}
		return nova.equals(armazenado.trim());
	}
}
