package com.exadel.core.services.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    private int id;
    private String brand;
    private String name;
    private String price;
    private String image_link;
    private String description;
    private double rating;
    private String category;
    private String product_type;
    private String created_at;
    private String updated_at;
    private String api_featured_image;
    private List<ProductColor> product_colors;
}