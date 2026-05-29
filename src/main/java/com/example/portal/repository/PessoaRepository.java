package com.example.portal.repository;

import com.example.portal.entity.Pessoa;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {

	List<Pessoa> findByAtivoTrueOrderByNomeAsc();
}
