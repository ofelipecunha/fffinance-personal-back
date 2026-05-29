package com.example.portal.repository;

import com.example.portal.entity.Documento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoRepository extends JpaRepository<Documento, Integer> {

	List<Documento> findByPessoa_IdOrderByDataUploadDesc(Integer pessoaId);
}
