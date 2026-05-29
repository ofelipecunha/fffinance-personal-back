package com.example.portal.repository;

import com.example.portal.entity.FormaPagamento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, Integer> {

	@Query("SELECT f FROM FormaPagamento f ORDER BY LOWER(f.nome)")
	List<FormaPagamento> findAllOrderedByNomeIgnoreCase();
}
