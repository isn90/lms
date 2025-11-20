package com.cloudx.azure.library_management_system.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class Book {
    private String id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private int quantity;
    private double price;
    private LocalDate publishedDate;
}
