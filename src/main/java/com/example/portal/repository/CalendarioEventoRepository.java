package com.example.portal.repository;

import com.example.portal.entity.CalendarioEvento;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarioEventoRepository extends JpaRepository<CalendarioEvento, Integer> {

	List<CalendarioEvento> findByDataEventoBetweenOrderByDataEventoAscIdAsc(LocalDate inicio, LocalDate fim);
}
