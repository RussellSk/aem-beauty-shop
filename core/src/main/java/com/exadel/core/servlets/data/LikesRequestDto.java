package com.exadel.core.servlets.data;

import lombok.Data;

@Data
public class LikesRequestDto {
    private String product;
    private boolean like_type;
}
