package com.example.portal.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Limites de multipart aplicados ao {@link jakarta.servlet.ServletContext} / DispatcherServlet.
 * Garante que o upload de avatar não fica preso ao default (~1 MB) se algo falhar no binding das
 * propriedades, e que {@code maxRequestSize} excede {@code maxFileSize} (o pedido multipart inclui
 * boundaries e metadados — com ambos iguais, ficheiros grandes falham com {@code MaxUploadSizeExceededException}).
 */
@Configuration
public class MultipartLimitsConfig {

	private static final long MAX_FILE_BYTES = 32L * 1024 * 1024;
	private static final long MAX_REQUEST_BYTES = 40L * 1024 * 1024;

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		return new MultipartConfigElement("", MAX_FILE_BYTES, MAX_REQUEST_BYTES, 0);
	}
}
