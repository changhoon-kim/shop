package com.shop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.OrderDto;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
public class OrderServiceTest {
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	ItemRepository itemRepository;
	
	@Autowired
	MemberRepository memberRepository;
	
	public Item saveItem() {
		Item item = new Item();
		item.setItemNm("test item");
		item.setPrice(10000);
		item.setItemDetail("test item detail");
		item.setItemSellStatus(ItemSellStatus.SELL);
		item.setStockNumber(100);
		return itemRepository.save(item);
	}
	
	public Member saveMember() {
		Member member = new Member();
		member.setEmail("test@test.com");
		return memberRepository.save(member);
	}
	
	@Test
	@DisplayName("주문테스트")
	public void order() {
		Item item = saveItem();
		Member member = saveMember();
		
		OrderDto orderDto = new OrderDto();
		orderDto.setCount(10);
		orderDto.setItemId(item.getId());
		
		Long orderId = orderService.order(orderDto, member.getEmail());
		
		Order order = orderRepository.findById(orderId)
				.orElseThrow(EntityNotFoundException::new);
		
		List<OrderItem> orderItems = order.getOrderItems();
		
		int totalPrice = orderDto.getCount() * item.getPrice();
		
		assertEquals(totalPrice, order.getTotalPrice());
	}
}
