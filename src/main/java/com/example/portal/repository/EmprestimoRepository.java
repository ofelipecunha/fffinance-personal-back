package com.example.portal.repository;

import com.example.portal.entity.Emprestimo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Integer> {

	@Query(
			"""
			SELECT e FROM Emprestimo e
			WHERE e.idLogin = :idLogin
			AND (:descricao IS NULL OR :descricao = '' OR LOWER(e.descricao) LIKE LOWER(CONCAT('%', :descricao, '%')))
			ORDER BY e.dataCriacao DESC, e.id DESC
			""")
	List<Emprestimo> listarPorUsuario(
			@Param("idLogin") Long idLogin, @Param("descricao") String descricao);

	Optional<Emprestimo> findByIdAndIdLogin(Integer id, Long idLogin);
}
