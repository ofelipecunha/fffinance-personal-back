package com.example.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

	private Long idLogin;
	private String nome;
	private String email;
	private String token;
	/** URL ou caminho da foto de perfil (pode ser nulo). */
	private String imagem;

	/** ADMIN | USUARIO */
	private String perfil;
}
