package org.serratec.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.serratec.ecommerce.dto.ItemPedidoDto;
import org.serratec.ecommerce.model.ItemPedido;
import org.serratec.ecommerce.model.Jogo;
import org.serratec.ecommerce.model.Pedido;
import org.serratec.ecommerce.repository.ItemPedidoRepository;
import org.serratec.ecommerce.repository.JogoRepository;
import org.serratec.ecommerce.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemPedidoService {

	@Autowired
	private ItemPedidoRepository itemPedidoRepository;

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private JogoRepository jogoRepository;

	public List<ItemPedidoDto> listarTodos() {
		return itemPedidoRepository.findAll().stream().map(ItemPedidoDto::toDTO).toList();
	}

	public Optional<ItemPedidoDto> obterPorId(Long id) {
		if (!itemPedidoRepository.existsById(id)) {
			return Optional.empty();
		}
		return Optional.of(ItemPedidoDto.toDTO(itemPedidoRepository.findById(id).get()));
	}

	public ItemPedidoDto salvarItemPedido(ItemPedidoDto dto) {
		ItemPedido itemPedidoEntity = dto.toEntity();
		
		Jogo jogo = jogoRepository.findById(dto.jogoId()).orElseThrow(() -> new RuntimeException("Jogo não encontrado"));
	    Pedido pedido = pedidoRepository.findById(dto.pedidoId()).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
	    
	    ItemPedido itemPedido = null;
		itemPedido.setJogo(jogo);
	    itemPedido.setPedido(pedido);
	    
	    itemPedido.setValorBruto(itemPedido.getPrecoUnitario() * itemPedido.getQuantidade());
	    itemPedido.setValorLiquido(itemPedido.getValorBruto() * (1 - (itemPedido.getPercentualDesconto() / 100)));
	    
	    itemPedidoRepository.save(itemPedido);
	    
	    return new ItemPedidoDto(itemPedido.getId(), pedido.getId(), jogo.getId(), itemPedido.getPrecoUnitario(), itemPedido.getQuantidade(), 
	            itemPedido.getPercentualDesconto(), itemPedido.getValorBruto(), 
	            itemPedido.getValorLiquido());
	}

	public boolean apagarItemPedido(Long id) {
		if (!itemPedidoRepository.existsById(id)) {
			return false;
		}
		itemPedidoRepository.deleteById(id);
		return true;
	}

	public Optional<ItemPedidoDto> alterarItemPedido(Long id, ItemPedidoDto dto) {
		if (!itemPedidoRepository.existsById(id)) {
			return Optional.empty();
		}
		ItemPedido itemPedidoEntity = dto.toEntity();
		itemPedidoEntity.setId(id);
		itemPedidoEntity.setPedido(pedidoRepository.findById(dto.pedidoId())
				.orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado.")));
		itemPedidoEntity.setJogo(jogoRepository.findById(dto.jogoId())
				.orElseThrow(() -> new IllegalArgumentException("Jogo não encontrado.")));
		itemPedidoEntity = itemPedidoRepository.save(itemPedidoEntity);
		return Optional.of(ItemPedidoDto.toDTO(itemPedidoEntity));
	}

	public List<ItemPedidoDto> buscarPorPedido(Long pedidoId) {
		List<ItemPedido> itensPedido = itemPedidoRepository.findByPedidoId(pedidoId);
		return itensPedido.stream().map(ItemPedidoDto::toDTO).toList();
	}

	public List<ItemPedidoDto> buscarPorJogo(Long jogoId) {
		List<ItemPedido> itensPedido = itemPedidoRepository.findByJogoId(jogoId);
		return itensPedido.stream().map(ItemPedidoDto::toDTO).toList();
	}
}