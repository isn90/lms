package com.cloudx.azure.library_management_system.service;

import com.cloudx.azure.library_management_system.entity.Books;
import com.cloudx.azure.library_management_system.model.Book;
import com.cloudx.azure.library_management_system.model.CartItem;
import com.cloudx.azure.library_management_system.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class LibraryService {

    private final BookRepository bookRepository;

    private Map<String, CartItem> cart = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // Optional: Load initial data if table is empty
        if (bookRepository.count() == 0) {
            loadInitialData();
        }
    }

    private void loadInitialData() {
        List<Books> initialBooks = Arrays.asList(
                new Books("1", "The Great Gatsby", "F. Scott Fitzgerald",
                        "978-0-7432-7356-5", "Fiction", 10, BigDecimal.valueOf(12.99), java.time.LocalDate.of(1925, 4, 10)),
                new Books("2", "To Kill a Mockingbird", "Harper Lee",
                        "978-0-06-112008-4", "Fiction", 8, BigDecimal.valueOf(14.99), java.time.LocalDate.of(1960, 7, 11)),
                new Books("3", "1984", "George Orwell",
                        "978-0-452-28423-4", "Dystopian", 12, BigDecimal.valueOf(10.99), java.time.LocalDate.of(1949, 6, 8))
        );
        bookRepository.saveAll(initialBooks);
    }

    public List<Books> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Books> getBookById(String id) {
        return bookRepository.findById(id);
    }

    public Books addBook(Books book) {
        if (book.getId() == null) {
            book.setId(UUID.randomUUID().toString());
        }
        book.setCreatedDate(java.time.LocalDateTime.now());
        book.setUpdatedDate(java.time.LocalDateTime.now());
        return bookRepository.save(book);
    }

    public boolean removeBook(String id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Keep your existing cart methods (they can stay in-memory for now)
    @Cacheable(value = "cart", key = "#bookId")
    public CartItem addToCart(String bookId, int quantity) {
        Books book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient quantity available");
        }

        CartItem cartItem = cart.get(bookId);
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem(bookId, book.getTitle(), quantity, book.getPrice());
            cart.put(bookId, cartItem);
        }

        return cartItem;
    }

    @CacheEvict(value = "cart", key = "#bookId")
    public boolean removeFromCart(String bookId) {
        return cart.remove(bookId) != null;
    }

    @Cacheable("cartItems")
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cart.values());
    }

    @CacheEvict(value = "cart", allEntries = true)
    public void clearCart() {
        cart.clear();
    }

    public Map<String, CartItem> checkout() {
        // For now, just return the cart and clear it
        // In a real application, you'd process payment and update inventory
        Map<String, CartItem> order = new HashMap<>(cart);
        cart.clear();
        return order;
    }

    /*public Map<String, CartItem> returnBooks(Map<String, Integer> returnItems) {
        Map<String, CartItem> returnedItems = new HashMap<>();

        for (Map.Entry<String, Integer> entry : returnItems.entrySet()) {
            String bookId = entry.getKey();
            int quantity = entry.getValue();

            Book book = books.get(bookId);
            if (book != null) {
                book.setQuantity(book.getQuantity() + quantity);
                returnedItems.put(bookId, new CartItem(bookId, book.getTitle(), quantity, book.getPrice()));
            }
        }

        return returnedItems;
    }*/
}
