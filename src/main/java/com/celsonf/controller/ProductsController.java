package com.celsonf.controller;

import com.celsonf.InMemoryStore;
import com.celsonf.model.Product;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@Controller("/products")
public class ProductsController {

    private final InMemoryStore store;

    public ProductsController(InMemoryStore store) {
        this.store = store;
    }

    @Get
    public List<Product> listAllProducts() {
        return new ArrayList<>(store.getProducts().values());
    }

}
