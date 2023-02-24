package com.example.users.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.users.config.ProductEventListener;
import com.example.users.entities.Category;
import com.example.users.entities.Product;
import com.example.users.repository.CategoryRepository;
import com.example.users.repository.ProductRepository;


@CrossOrigin (origins = "*")
@RestController
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private ProductEventListener pe;
    
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
    	
    	
        Product savedProduct = productRepository.save(product);
        envMessage("Product created: " + savedProduct.getName());
       // messagingTemplate.convertAndSend("/topic/products", savedProduct);
        pe.handleProductChangeEvent(savedProduct);
       
        
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @GetMapping("/getproducts")
    public ResponseEntity<List<Product>> getAllProducts() throws Exception{
    	Thread.sleep(1);
        List<Product> products = productRepository.findAll();
      //  messagingTemplate.convertAndSend("/topic/new", products);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    
    @GetMapping("/categories")
    
    public ResponseEntity<List<Category>> getAllcategories() {
        List<Category> categorys = categoryRepository.findAll();
        return new ResponseEntity<>(categorys, HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Product existingProduct = optionalProduct.get();
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());
        Product savedProduct = productRepository.save(existingProduct);
        envMessage("Product updated: " + savedProduct.getName());
        return new ResponseEntity<>(savedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Product product = optionalProduct.get();
        productRepository.delete(product);
        envMessage("Product deleted: " + product.getName());
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    private void envMessage(String message) {
        kafkaTemplate.send("my-topic", message);
    }
}
