// Student Number: ATE/7495/14

package com.shopwave.shopwavestarter.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }

}