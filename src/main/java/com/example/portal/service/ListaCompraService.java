package com.example.portal.service;

import com.example.portal.dto.ItemListaCompraResponse;
import com.example.portal.dto.ItemListaCompraUpdateRequest;
import com.example.portal.dto.ItemListaLinhaRequest;
import com.example.portal.dto.ListaCompraCreateRequest;
import com.example.portal.dto.ListaCompraDetalheResponse;
import com.example.portal.dto.ListaCompraResumoResponse;
import com.example.portal.dto.ListaCompraUpdateRequest;
import com.example.portal.entity.ItemListaCompra;
import com.example.portal.entity.ListaCompra;
import com.example.portal.entity.TipoLista;
import com.example.portal.repository.ItemListaCompraRepository;
import com.example.portal.repository.ListaCompraRepository;
import com.example.portal.repository.TipoListaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ListaCompraService {

	private final ListaCompraRepository listaRepo;
	private final ItemListaCompraRepository itemRepo;
	private final TipoListaRepository tipoRepo;

	public List<ListaCompraResumoResponse> listarPorTipo(Integer tipoId) {
		if (!tipoRepo.existsById(tipoId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de lista não encontrado");
		}
		return listaRepo.findByTipo_IdOrderByDataCriacaoDesc(tipoId).stream()
				.map(l -> new ListaCompraResumoResponse(
						l.getId(),
						l.getNome(),
						l.getDataCriacao(),
						itemRepo.sumTotalByListaId(l.getId())
				))
				.toList();
	}

	public ListaCompraDetalheResponse buscar(Integer id) {
		ListaCompra l = listaRepo.findByIdComTipo(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista não encontrada"));
		return montarDetalhe(l);
	}

	@Transactional
	public ListaCompraDetalheResponse criar(ListaCompraCreateRequest dto) {
		TipoLista tipo = tipoRepo.findById(dto.tipoId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de lista não encontrado"));
		ListaCompra lista = new ListaCompra();
		lista.setTipo(tipo);
		lista.setNome(dto.nome().trim());
		lista.setDataCriacao(Instant.now());
		lista = listaRepo.save(lista);
		if (dto.itens() != null && !dto.itens().isEmpty()) {
			for (ItemListaLinhaRequest linha : dto.itens()) {
				salvarNovoItem(lista, linha);
			}
		}
		listaRepo.flush();
		itemRepo.flush();
		return buscar(lista.getId());
	}

	@Transactional
	public ListaCompraDetalheResponse atualizarLista(Integer id, ListaCompraUpdateRequest dto) {
		ListaCompra l = listaRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista não encontrada"));
		l.setNome(dto.nome().trim());
		listaRepo.save(l);
		return buscar(id);
	}

	@Transactional
	public void excluirLista(Integer id) {
		if (!listaRepo.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista não encontrada");
		}
		listaRepo.deleteById(id);
	}

	@Transactional
	public ItemListaCompraResponse adicionarItem(Integer listaId, ItemListaLinhaRequest dto) {
		ListaCompra lista = listaRepo.findById(listaId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista não encontrada"));
		ItemListaCompra it = salvarNovoItem(lista, dto);
		itemRepo.flush();
		return recarregarItemResponse(it.getId());
	}

	@Transactional
	public ItemListaCompraResponse atualizarItem(Integer listaId, Integer itemId, ItemListaCompraUpdateRequest dto) {
		ItemListaCompra it = itemRepo.findById(itemId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));
		if (!it.getLista().getId().equals(listaId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item não pertence a esta lista");
		}
		it.setNome(dto.nome().trim());
		it.setQuantidade(dto.quantidade());
		it.setValorUnitario(normalizarMoeda(dto.valorUnitario()));
		itemRepo.save(it);
		itemRepo.flush();
		return recarregarItemResponse(itemId);
	}

	@Transactional
	public void excluirItem(Integer listaId, Integer itemId) {
		ItemListaCompra it = itemRepo.findById(itemId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));
		if (!it.getLista().getId().equals(listaId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item não pertence a esta lista");
		}
		itemRepo.delete(it);
	}

	private ItemListaCompra salvarNovoItem(ListaCompra lista, ItemListaLinhaRequest dto) {
		ItemListaCompra it = new ItemListaCompra();
		it.setLista(lista);
		it.setNome(dto.nome().trim());
		it.setQuantidade(dto.quantidade());
		it.setValorUnitario(normalizarMoeda(dto.valorUnitario()));
		return itemRepo.save(it);
	}

	private ItemListaCompraResponse recarregarItemResponse(Integer itemId) {
		ItemListaCompra it = itemRepo.findById(itemId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));
		return paraItemResponse(it);
	}

	private ListaCompraDetalheResponse montarDetalhe(ListaCompra l) {
		List<ItemListaCompra> itens = itemRepo.findByLista_IdOrderByIdAsc(l.getId());
		List<ItemListaCompraResponse> linhas = itens.stream().map(this::paraItemResponse).toList();
		TipoLista t = l.getTipo();
		return new ListaCompraDetalheResponse(
				l.getId(),
				t.getId(),
				t.getCodigo(),
				t.getNome(),
				l.getNome(),
				l.getDataCriacao(),
				itemRepo.sumTotalByListaId(l.getId()),
				linhas
		);
	}

	private ItemListaCompraResponse paraItemResponse(ItemListaCompra i) {
		return new ItemListaCompraResponse(
				i.getId(),
				i.getNome(),
				i.getQuantidade(),
				i.getValorUnitario(),
				i.getTotal() != null ? i.getTotal().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO
		);
	}

	private static BigDecimal normalizarMoeda(BigDecimal v) {
		if (v == null) {
			return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		}
		return v.setScale(2, RoundingMode.HALF_UP);
	}
}
