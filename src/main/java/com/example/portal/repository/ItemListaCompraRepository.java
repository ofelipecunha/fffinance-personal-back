package com.example.portal.repository;

import com.example.portal.entity.ItemListaCompra;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemListaCompraRepository extends JpaRepository<ItemListaCompra, Integer> {

	List<ItemListaCompra> findByLista_IdOrderByIdAsc(Integer listaId);

	@Query("SELECT COALESCE(SUM(i.quantidade * i.valorUnitario), 0) FROM ItemListaCompra i WHERE i.lista.id = :listaId")
	java.math.BigDecimal sumTotalByListaId(@Param("listaId") Integer listaId);
}
