package com.example.portal.repository;

import com.example.portal.entity.LoginUsuario;
import java.util.List;
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

	@Query(
			"""
			SELECT u FROM LoginUsuario u
			WHERE (:nome IS NULL OR :nome = '' OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
			ORDER BY u.nome
			""")
	List<LoginUsuario> listarPorNome(@Param("nome") String nome);

	@Query(
			"SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM LoginUsuario u "
					+ "WHERE LOWER(TRIM(u.login)) = LOWER(TRIM(:login)) AND u.idLogin <> :id")
	boolean existsLoginForOther(@Param("login") String login, @Param("id") Long id);

	@Query(
			"SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM LoginUsuario u "
					+ "WHERE LOWER(TRIM(u.email)) = LOWER(TRIM(:email)) AND u.idLogin <> :id")
	boolean existsEmailForOther(@Param("email") String email, @Param("id") Long id);

	@Query(
			"SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM LoginUsuario u "
					+ "WHERE LOWER(TRIM(COALESCE(u.login, u.email))) = LOWER(TRIM(:login))")
	boolean existsByLoginNormalized(@Param("login") String login);

	@Query(
			"SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM LoginUsuario u "
					+ "WHERE LOWER(TRIM(u.email)) = LOWER(TRIM(:email))")
	boolean existsByEmailNormalized(@Param("email") String email);
}
