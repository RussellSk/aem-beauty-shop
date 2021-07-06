package com.exadel.core.services.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductColor {
    private String hex_value;
    private String colour_name;
}
