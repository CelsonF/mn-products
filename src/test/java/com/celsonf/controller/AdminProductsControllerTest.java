package com.celsonf.controller;

import com.celsonf.InMemoryStore;
import com.celsonf.model.Product;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class AdminProductsControllerTest {

    @Inject
    @Client("/admin/products")
    HttpClient client;

    @Inject
    InMemoryStore store;


    @Test
    void addNewProductCanBeAddedUsingTheAdminPostEndpoint() {
        var productToAdd = new Product(1234, "test product", Product.Type.OTHER);

        store.getProducts().remove(productToAdd.id());
        assertNull(store.getProducts().get(productToAdd.id()));

        var response = client.toBlocking().exchange(
                HttpRequest.POST("/",productToAdd),
                Product.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(productToAdd.id(), response.getBody().get().id());
        assertEquals(productToAdd.name(), response.getBody().get().name());
        assertEquals(Product.Type.OTHER, response.getBody().get().type());
    }

//    @Test
//    void addingProductTwiceResultsInConflict() {
//        var productToAdd = new Product(1234, "test product", Product.Type.OTHER);
//    }

}