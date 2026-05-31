package com.example.portal.repository;

import com.example.portal.entity.Categoria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

	@Query("SELECT c FROM Categoria c ORDER BY LOWER(c.nome)")
	List<Categoria> findAllOrderedByNomeIgnoreCase();

	Optional<Categoria> findFirstByNomeIgnoreCaseAndTipoIgnoreCase(String nome, String tipo);
}
