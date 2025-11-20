package com.cloudx.azure.library_management_system.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CartItem {
    private String bookId;
    private String title;
    private int quantity;
    private BigDecimal price;

    public double getTotalPrice() {
        return quantity * price.doubleValue();
    }
}
