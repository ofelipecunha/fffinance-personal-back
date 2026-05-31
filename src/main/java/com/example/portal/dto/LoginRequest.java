package com.example.portal.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

	@NotBlank(message = "Login é obrigatório")
	@Size(max = 150, message = "Login deve ter no máximo 150 caracteres")
	@JsonAlias({ "email" })
	private String login;

	@NotBlank(message = "Senha é obrigatória")
	@JsonAlias({ "password" })
	private String senha;

	/** Compatível com clientes antigos que enviam {@code email} no JSON. */
	public LoginRequest(String loginOuEmail, String senha) {
		this.login = loginOuEmail;
		this.senha = senha;
	}
}
