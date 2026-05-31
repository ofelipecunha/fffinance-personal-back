package com.example.portal.repository;

import com.example.portal.entity.TipoLista;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoListaRepository extends JpaRepository<TipoLista, Integer> {

	@Query("SELECT t FROM TipoLista t WHERE t.ativo = TRUE ORDER BY t.ordem ASC, t.nome ASC")
	List<TipoLista> findAllAtivosOrdenados();

	@Query(value = """
			SELECT
			  t.id AS id,
			  t.codigo AS codigo,
			  t.nome AS nome,
			  t.icone AS icone,
			  t.cor AS cor,
			  t.ordem AS ordem,
			  (SELECT COUNT(*) FROM lista l WHERE l.tipo_id = t.id) AS qtd_listas,
			  (SELECT COALESCE(SUM(i.quantidade * i.valor_unitario), 0)
			     FROM item_lista i
			     INNER JOIN lista l2 ON l2.id = i.lista_id
			    WHERE l2.tipo_id = t.id) AS valor_total
			FROM tipo_lista t
			WHERE t.ativo = TRUE
			ORDER BY t.ordem ASC, t.nome ASC
			""", nativeQuery = true)
	List<Object[]> findCardResumoRows();

	Optional<TipoLista> findByCodigo(String codigo);
}
