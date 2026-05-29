package com.example.portal.controller;

import com.example.portal.dto.DocumentoResponse;
import com.example.portal.dto.DocumentoUpdateRequest;
import com.example.portal.service.DocumentoService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
public class DocumentoController {

	private final DocumentoService documentoService;

	@GetMapping
	public List<DocumentoResponse> listar(@RequestParam("pessoaId") Integer pessoaId) {
		return documentoService.listarPorPessoa(pessoaId);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public DocumentoResponse criar(
			@RequestParam String nome,
			@RequestParam(required = false) String descricao,
			@RequestParam Integer pessoaId,
			@RequestParam("arquivo") MultipartFile arquivo)
			throws IOException {
		return documentoService.criar(nome, descricao, pessoaId, arquivo);
	}

	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public DocumentoResponse atualizarJson(@PathVariable Integer id, @Valid @RequestBody DocumentoUpdateRequest body) {
		return documentoService.atualizarMetadados(id, body);
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public DocumentoResponse atualizarMultipart(
			@PathVariable Integer id,
			@RequestParam String nome,
			@RequestParam(required = false) String descricao,
			@RequestParam Integer pessoaId,
			@RequestParam("arquivo") MultipartFile arquivo)
			throws IOException {
		return documentoService.atualizarComArquivo(id, nome, descricao, pessoaId, arquivo);
	}

	@GetMapping("/{id}/download")
	public ResponseEntity<Resource> download(@PathVariable Integer id) throws IOException {
		Resource resource = documentoService.arquivoParaDownload(id);
		String fn = documentoService.nomeArquivoDownload(id);
		ContentDisposition disposition = ContentDisposition.attachment()
				.filename(fn, StandardCharsets.UTF_8)
				.build();
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(resource);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Integer id) {
		documentoService.excluir(id);
	}
}
