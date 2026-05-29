package com.example.portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "login_usuario")
@Getter
@Setter
@NoArgsConstructor
public class LoginUsuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_login")
	private Long idLogin;

	@Column(nullable = false, length = 150)
	private String nome;

	@Column(nullable = false, unique = true, length = 150)
	private String email;

	@Column(length = 150)
	private String login;

	@Column(nullable = false, length = 30)
	private String perfil = "USUARIO";

	@Column(name = "senha_hash", nullable = false, length = 255)
	private String senhaHash;

	@Column(nullable = false, length = 1)
	private String ativo = "S";

	@Column(length = 500)
	private String token;

	@Column(name = "data_criacao", nullable = false, insertable = false, updatable = false)
	private Instant dataCriacao;

	@Column(length = 2048)
	private String imagem;

	@Column(name = "imagem_url", length = 500)
	private String imagemUrl;

	@Column(length = 255)
	private String endereco;

	@Column(length = 100)
	private String cidade;

	@Column(length = 2)
	private String estado;

	@Column(length = 150)
	private String sobrenome;

	@Column(length = 20)
	private String telefone;

	@Column(length = 500)
	private String bio;

	@Column(length = 150)
	private String cargo;

	@Column(length = 255)
	private String localizacao;

	@Column(length = 120)
	private String pais;

	@Column(name = "cidade_estado", length = 255)
	private String cidadeEstado;

	@Column(length = 30)
	private String cep;

	@Column(name = "tax_id", length = 80)
	private String taxId;

	@Column(name = "url_facebook", length = 500)
	private String urlFacebook;

	@Column(name = "url_twitter", length = 500)
	private String urlTwitter;

	@Column(name = "url_linkedin", length = 500)
	private String urlLinkedin;

	@Column(name = "url_instagram", length = 500)
	private String urlInstagram;
}
