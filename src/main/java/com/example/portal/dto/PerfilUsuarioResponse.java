package com.example.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Dados do utilizador autenticado (perfil + cabeçalho). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilUsuarioResponse {

	private Long idLogin;
	private String nome;
	private String sobrenome;
	private String email;
	private String imagem;
	private String telefone;
	private String bio;
	private String cargo;
	private String localizacao;
	private String pais;
	private String cidadeEstado;
	private String cep;
	private String taxId;
	private String urlFacebook;
	private String urlTwitter;
	private String urlLinkedin;
	private String urlInstagram;
}
