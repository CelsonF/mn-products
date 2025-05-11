package com.celsonf.product;

import com.celsonf.InMemoryStore;
import com.celsonf.admin.product.UpdatedProductRequest;
import com.celsonf.model.Product;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
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

    @Test
    void addingProductTwiceResultsInConflict() {
        var productToAdd = new Product(1234, "test product", Product.Type.OTHER);

        store.getProducts().remove(productToAdd.id());
        assertNull(store.getProducts().get(productToAdd.id()));

        var response = client.toBlocking().exchange(
          HttpRequest.POST("/", productToAdd),
          Product.class
        );
        assertEquals(HttpStatus.CREATED,response.getStatus());

        var expectedConflict = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(HttpRequest.POST("/",productToAdd))
        );

        assertEquals(HttpStatus.CONFLICT, expectedConflict.getStatus());


    }

    @Test
    void productUpdatedUsingAdminPutEndpoint() {
        var productToUpdate = new Product(1234, "old value", Product.Type.OTHER);

        store.getProducts().put(productToUpdate.id(),productToUpdate);
        assertEquals(productToUpdate, store.getProducts().get(productToUpdate.id()));

        var updateRequest = new UpdatedProductRequest("new-value", Product.Type.TEA);

        var response = client.toBlocking().exchange(
                HttpRequest.PUT("/"+ productToUpdate.id(),updateRequest),
                Product.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        var productFromStore = store.getProducts().get(productToUpdate.id());
        assertEquals(updateRequest.name(),productFromStore.name());
        assertEquals(Product.Type.TEA, productFromStore.type());
    }

    @Test
    void nonExistingProductWillBeAddedWhenUsingAdminPutEndpoint() {
        var productId = 999;

        store.getProducts().remove(productId);
        assertNull(store.getProducts().get(productId));

        var updateRequest = new UpdatedProductRequest("new-value", Product.Type.TEA);

        var response = client.toBlocking().exchange(
                HttpRequest.PUT("/"+productId, updateRequest),
                Product.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        var productFromStore = store.getProducts().get(productId);
        assertEquals(updateRequest.name(),productFromStore.name());
        assertEquals(Product.Type.TEA, productFromStore.type());

    }
}