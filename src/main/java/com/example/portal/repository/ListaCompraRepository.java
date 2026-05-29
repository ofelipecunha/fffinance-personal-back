package com.example.portal.repository;

import com.example.portal.entity.ListaCompra;
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
}
