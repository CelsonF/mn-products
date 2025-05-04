package com.celsonf.controller;

import com.celsonf.InMemoryStore;
import com.celsonf.model.Product;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.exceptions.HttpStatusException;

@Controller("/admin/products")
public class AdminProductsController {

    private final InMemoryStore store;

    public AdminProductsController(InMemoryStore store) {
        this.store = store;
    }


    @Status(HttpStatus.CREATED)
    @Post(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public Product addNewProduct(@Body Product product) {
        if(store.getProducts().containsKey(product.id())) {
            throw new HttpStatusException(
                    HttpStatus.CONFLICT,
                    "Product with id "+ product.id() +" already exists"
            );
        }

        return store.addProduct(product);
    }


}
