package com.example.portal.repository;

import com.example.portal.entity.ListaCompra;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ListaCompraRepository extends JpaRepository<ListaCompra, Integer> {

	List<ListaCompra> findByTipo_IdOrderByDataCriacaoDesc(Integer tipoId);

	@Query("SELECT l FROM ListaCompra l JOIN FETCH l.tipo WHERE l.id = :id")
	Optional<ListaCompra> findByIdComTipo(@Param("id") Integer id);

	@Query("""
			SELECT l FROM ListaCompra l JOIN FETCH l.tipo
			WHERE l.tipo.codigo = 'MERCADO'
			  AND l.dataCompra >= :dataInicio
			  AND l.dataCompra < :dataFim
			  AND (:idLogin IS NULL OR l.idLogin = :idLogin)
			ORDER BY l.dataCompra DESC, l.id DESC
			""")
	List<ListaCompra> findMercadoPorPeriodo(
			@Param("dataInicio") LocalDate dataInicio,
			@Param("dataFim") LocalDate dataFim,
			@Param("idLogin") Long idLogin);
}
