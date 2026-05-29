package com.example.portal.repository;

import com.example.portal.entity.Lancamento;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Integer> {

	@Query("""
			SELECT l FROM Lancamento l
			JOIN FETCH l.categoria
			LEFT JOIN FETCH l.formaPagamentoRef
			ORDER BY l.dataLancamento DESC, l.id DESC
			""")
	List<Lancamento> findAllOrdered();

	@Query("""
			SELECT l FROM Lancamento l
			JOIN FETCH l.categoria
			LEFT JOIN FETCH l.formaPagamentoRef
			WHERE l.id = :id
			""")
	Optional<Lancamento> findDetalhe(@Param("id") Integer id);

	@Query("""
			SELECT l FROM Lancamento l
			JOIN FETCH l.categoria
			LEFT JOIN FETCH l.formaPagamentoRef
			WHERE (:dataInicio IS NULL OR l.dataLancamento >= :dataInicio)
			  AND (:dataFim IS NULL OR l.dataLancamento <= :dataFim)
			ORDER BY l.dataLancamento DESC, l.id DESC
			""")
	List<Lancamento> findAllOrderedFiltrado(
			@Param("dataInicio") LocalDate dataInicio,
			@Param("dataFim") LocalDate dataFim);
}
