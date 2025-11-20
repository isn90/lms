package com.cloudx.azure.library_management_system.controller;

import com.cloudx.azure.library_management_system.entity.Books;
import com.cloudx.azure.library_management_system.model.Book;
import com.cloudx.azure.library_management_system.model.CartItem;
import com.cloudx.azure.library_management_system.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/books")
@Tag(name = "Library Management", description = "API for managing books, cart, and orders")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieve a list of all available books in the library")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of books")
    public ResponseEntity<List<Books>> getAllBooks() {
        return ResponseEntity.ok(libraryService.getAllBooks());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieve a specific book by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<Books> getBookById(
            @Parameter(description = "ID of the book to retrieve", example = "1", required = true)
            @PathVariable String id) {
        Optional<Books> book = libraryService.getBookById(id);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Add a new book", description = "Add a new book to the library catalog")
    @ApiResponse(responseCode = "200", description = "Book successfully added")
    public ResponseEntity<Books> addBook(
            @Parameter(description = "Book object to add", required = true)
            @RequestBody Books book) {
        return ResponseEntity.ok(libraryService.addBook(book));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a book", description = "Remove a book from the library catalog")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book successfully removed"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<Void> removeBook(
            @Parameter(description = "ID of the book to remove", example = "1", required = true)
            @PathVariable String id) {
        if (libraryService.removeBook(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/cart/{bookId}")
    @Operation(summary = "Add book to cart", description = "Add a book to the shopping cart with specified quantity")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book added to cart successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid quantity or book not available")
    })
    public ResponseEntity<CartItem> addToCart(
            @Parameter(description = "ID of the book to add to cart", example = "1", required = true)
            @PathVariable String bookId,

            @Parameter(description = "Quantity to add (default: 1)", example = "2")
            @RequestParam(defaultValue = "1") int quantity) {
        try {
            CartItem cartItem = libraryService.addToCart(bookId, quantity);
            return ResponseEntity.ok(cartItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/cart/{bookId}")
    @Operation(summary = "Remove from cart", description = "Remove a book from the shopping cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book removed from cart"),
            @ApiResponse(responseCode = "404", description = "Book not found in cart")
    })
    public ResponseEntity<Void> removeFromCart(
            @Parameter(description = "ID of the book to remove from cart", example = "1", required = true)
            @PathVariable String bookId) {
        if (libraryService.removeFromCart(bookId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/cart")
    @Operation(summary = "Get cart items", description = "Retrieve all items in the shopping cart")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved cart items")
    public ResponseEntity<List<CartItem>> getCart() {
        return ResponseEntity.ok(libraryService.getCartItems());
        //return ResponseEntity.notFound().build();
    }

    @PostMapping("/checkout")
    @Operation(summary = "Checkout cart", description = "Process checkout and create an order from cart items")
    @ApiResponse(responseCode = "200", description = "Checkout successful")
    public ResponseEntity<Map<String, CartItem>> checkout() {
        return ResponseEntity.ok(libraryService.checkout());
        //return ResponseEntity.notFound().build();
    }

    @PostMapping("/return")
    @Operation(summary = "Return books", description = "Return books to library and update inventory")
    @ApiResponse(responseCode = "200", description = "Books returned successfully")
    public ResponseEntity<Map<String, CartItem>> returnBooks(
            @Parameter(description = "Map of book IDs and quantities to return", example = "{\"1\": 2, \"2\": 1}", required = true)
            @RequestBody Map<String, Integer> returnItems) {
        //return ResponseEntity.ok(libraryService.returnBooks(returnItems));
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/cart/clear")
    @Operation(summary = "Clear cart", description = "Remove all items from the shopping cart")
    @ApiResponse(responseCode = "200", description = "Cart cleared successfully")
    public ResponseEntity<Void> clearCart() {
        libraryService.clearCart();
        return ResponseEntity.ok().build();
    }
}