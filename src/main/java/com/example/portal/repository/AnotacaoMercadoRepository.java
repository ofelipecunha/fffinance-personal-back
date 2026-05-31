package com.example.portal.repository;

import com.example.portal.entity.AnotacaoMercado;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnotacaoMercadoRepository extends JpaRepository<AnotacaoMercado, Integer> {

	@Query("""
			SELECT a FROM AnotacaoMercado a
			WHERE a.convertida = FALSE
			  AND (:idLogin IS NULL OR a.idLogin = :idLogin)
			ORDER BY a.dataCriacao DESC
			""")
	List<AnotacaoMercado> findAbertas(@Param("idLogin") Long idLogin);

	Optional<AnotacaoMercado> findFirstByIdLoginAndConvertidaFalseOrderByDataCriacaoDesc(Long idLogin);

	@Query("SELECT a FROM AnotacaoMercado a WHERE a.id = :id AND a.convertida = FALSE")
	Optional<AnotacaoMercado> findAbertaById(@Param("id") Integer id);
}
