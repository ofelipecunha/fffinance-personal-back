package com.example.portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import org.hibernate.annotations.Formula;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_lista")
@Getter
@Setter
@NoArgsConstructor
public class ItemListaCompra {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "lista_id", nullable = false)
	private ListaCompra lista;

	@Column(nullable = false, length = 150)
	private String nome;

	@Column(nullable = false)
	private Integer quantidade = 1;

	@Column(name = "valor_unitario", nullable = false, precision = 12, scale = 2)
	private BigDecimal valorUnitario = BigDecimal.ZERO;

	/** Total derivado (não precisa de coluna física no PostgreSQL). */
	@Formula("quantidade * valor_unitario")
	private BigDecimal total;
}
