package com.celsonf.admin.product;

import com.celsonf.model.Product;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record UpdatedProductRequest(String name, Product.Type type ) {
}
