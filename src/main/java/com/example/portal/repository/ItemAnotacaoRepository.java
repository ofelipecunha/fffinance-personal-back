package com.example.portal.repository;

import com.example.portal.entity.ItemAnotacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemAnotacaoRepository extends JpaRepository<ItemAnotacao, Integer> {

	List<ItemAnotacao> findByAnotacao_IdOrderByIdAsc(Integer anotacaoId);

	@Query("SELECT COUNT(i) FROM ItemAnotacao i WHERE i.anotacao.id = :anotacaoId")
	long countByAnotacaoId(@Param("anotacaoId") Integer anotacaoId);
}
