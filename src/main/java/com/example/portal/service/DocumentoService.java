package com.example.portal.service;

import com.example.portal.dto.DocumentoResponse;
import com.example.portal.dto.DocumentoUpdateRequest;
import com.example.portal.entity.Documento;
import com.example.portal.entity.Pessoa;
import com.example.portal.repository.DocumentoRepository;
import com.example.portal.repository.PessoaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentoService {

	private static final Set<String> EXT_OK = Set.of("pdf", "jpg", "jpeg", "png");

	private final DocumentoRepository documentoRepository;
	private final PessoaRepository pessoaRepository;

	@Value("${app.documents.upload-dir:uploads/documentos}")
	private String uploadDir;

	@Transactional(readOnly = true)
	public List<DocumentoResponse> listarPorPessoa(Integer pessoaId) {
		if (!pessoaRepository.existsById(pessoaId)) {
			throw new EntityNotFoundException("Pessoa não encontrada: " + pessoaId);
		}
		return documentoRepository.findByPessoa_IdOrderByDataUploadDesc(pessoaId).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public Resource arquivoParaDownload(Integer id) throws IOException {
		Documento d = documentoRepository
				.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Documento não encontrado: " + id));
		Path path = Paths.get(d.getCaminhoArquivo()).toAbsolutePath().normalize();
		if (!Files.isRegularFile(path)) {
			throw new EntityNotFoundException("Ficheiro em disco não encontrado");
		}
		return new UrlResource(path.toUri());
	}

	@Transactional(readOnly = true)
	public String nomeArquivoDownload(Integer id) {
		return documentoRepository
				.findById(id)
				.map(Documento::getNomeArquivo)
				.orElse("documento");
	}

	@Transactional
	public DocumentoResponse criar(String nome, String descricao, Integer pessoaId, MultipartFile arquivo) throws IOException {
		Objects.requireNonNull(arquivo, "arquivo");
		if (arquivo.isEmpty()) {
			throw new IllegalArgumentException("Arquivo vazio");
		}
		validarExtensao(arquivo.getOriginalFilename());
		Pessoa pessoa = pessoaRepository
				.findById(pessoaId)
				.orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada: " + pessoaId));

		Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
		Files.createDirectories(dir);
		String original = arquivo.getOriginalFilename() != null ? arquivo.getOriginalFilename() : "ficheiro";
		String ext = extensao(original);
		String stored = UUID.randomUUID() + "." + ext;
		Path target = dir.resolve(stored);
		Files.copy(arquivo.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

		Documento d = new Documento();
		d.setNome(nome);
		d.setDescricao(descricao);
		d.setNomeArquivo(original);
		d.setTipoArquivo(ext);
		d.setTamanho(arquivo.getSize());
		d.setCaminhoArquivo(target.toString());
		d.setPessoa(pessoa);
		d.setDataUpload(Instant.now());
		documentoRepository.save(d);
		return toResponse(d);
	}

	@Transactional
	public DocumentoResponse atualizarComArquivo(Integer id, String nome, String descricao, Integer pessoaId, MultipartFile arquivo)
			throws IOException {
		Documento d = documentoRepository
				.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Documento não encontrado: " + id));
		Pessoa pessoa = pessoaRepository
				.findById(pessoaId)
				.orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada: " + pessoaId));

		apagarFicheiroDisco(d.getCaminhoArquivo());

		validarExtensao(arquivo.getOriginalFilename());
		Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
		Files.createDirectories(dir);
		String original = arquivo.getOriginalFilename() != null ? arquivo.getOriginalFilename() : d.getNomeArquivo();
		String ext = extensao(original);
		String stored = UUID.randomUUID() + "." + ext;
		Path target = dir.resolve(stored);
		Files.copy(arquivo.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

		d.setNome(nome);
		d.setDescricao(descricao);
		d.setNomeArquivo(original);
		d.setTipoArquivo(ext);
		d.setTamanho(arquivo.getSize());
		d.setCaminhoArquivo(target.toString());
		d.setPessoa(pessoa);
		documentoRepository.save(d);
		return toResponse(d);
	}

	@Transactional
	public DocumentoResponse atualizarMetadados(Integer id, DocumentoUpdateRequest req) {
		Documento d = documentoRepository
				.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Documento não encontrado: " + id));
		Pessoa pessoa = pessoaRepository
				.findById(req.pessoaId())
				.orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada: " + req.pessoaId()));
		d.setNome(req.nome());
		d.setDescricao(req.descricao());
		d.setPessoa(pessoa);
		documentoRepository.save(d);
		return toResponse(d);
	}

	@Transactional
	public void excluir(Integer id) {
		Documento d = documentoRepository
				.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Documento não encontrado: " + id));
		apagarFicheiroDisco(d.getCaminhoArquivo());
		documentoRepository.delete(d);
	}

	private void apagarFicheiroDisco(String caminho) {
		if (caminho == null) {
			return;
		}
		try {
			Path p = Paths.get(caminho);
			Files.deleteIfExists(p);
		} catch (IOException ignored) {
			// log em produção
		}
	}

	private void validarExtensao(String filename) {
		if (filename == null || !filename.contains(".")) {
			throw new IllegalArgumentException("Nome de ficheiro inválido");
		}
		String ext = extensao(filename);
		if (!EXT_OK.contains(ext)) {
			throw new IllegalArgumentException("Tipo não permitido. Use: pdf, jpg, jpeg, png");
		}
	}

	private static String extensao(String filename) {
		int i = filename.lastIndexOf('.');
		return i < 0 ? "" : filename.substring(i + 1).toLowerCase();
	}

	private DocumentoResponse toResponse(Documento d) {
		return new DocumentoResponse(
				d.getId(),
				d.getNome(),
				d.getDescricao(),
				d.getNomeArquivo(),
				d.getTipoArquivo(),
				d.getTamanho(),
				null,
				d.getPessoa().getId(),
				d.getDataUpload());
	}
}
