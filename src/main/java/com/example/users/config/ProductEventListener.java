package com.example.users.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.users.entities.Product;
import com.example.users.repository.ProductRepository;

@Component
public class ProductEventListener {

	@Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ProductRepository productRepository;
    
    @EventListener
    public void handleProductChangeEvent(Product event) {
        
            messagingTemplate.convertAndSend("/topic/new",event);
        
    }
    
	/*
	 * @EventListener public void handleProductChangeEvent(Product event) {
	 * 
	 * messagingTemplate.convertAndSend("/topic/new", getAllProducts());
	 * 
	 * }
	 */
    
	/*
	 * @EventListener public void handleProductChangeEvent(Product event) {
	 * messagingTemplate.convertAndSend("/topic/new", getAllProducts()); }
	 */

    private List<Product> getAllProducts() {
        return productRepository.findAll();
    }

}