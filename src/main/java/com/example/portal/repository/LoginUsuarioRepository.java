package com.example.portal.repository;

import com.example.portal.entity.LoginUsuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Camada de persistência (padrão DAO) para {@link LoginUsuario}.
 */
@Repository
public interface LoginUsuarioRepository extends JpaRepository<LoginUsuario, Long> {

	/**
	 * Ignora maiúsculas e espaços à volta do e-mail (evita falha silenciosa se o INSERT tiver espaços no VARCHAR).
	 */
	@Query("SELECT u FROM LoginUsuario u WHERE LOWER(TRIM(u.email)) = LOWER(TRIM(:email))")
	Optional<LoginUsuario> findByEmailNormalized(@Param("email") String email);

	Optional<LoginUsuario> findByToken(String token);
}
