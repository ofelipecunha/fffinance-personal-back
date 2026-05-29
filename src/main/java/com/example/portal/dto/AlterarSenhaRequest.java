package com.example.portal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlterarSenhaRequest {

	@NotBlank(message = "E-mail é obrigatório")
	@Email(message = "E-mail inválido")
	private String email;

	@NotBlank(message = "Senha atual é obrigatória")
	private String senhaAtual;

	@NotBlank(message = "Nova senha é obrigatória")
	@Size(min = 6, max = 72, message = "Nova senha deve ter entre 6 e 72 caracteres")
	private String novaSenha;
}
