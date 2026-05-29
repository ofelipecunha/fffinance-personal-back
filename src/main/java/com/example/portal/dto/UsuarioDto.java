package com.example.portal.dto;

import java.time.Instant;
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
public class UsuarioDto {

	private Integer idUsuario;
	private String nome;
	private String login;
	private String email;
	private String telefone;
	private String perfil;
	private String ativo;
	private Instant dataCadastro;
}
