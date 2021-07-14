package com.exadel.core.models;

import com.exadel.core.services.LikesService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;


@Model(
        adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class LikesModel {

    @Inject
    private LikesService likesService;

    @ValueMapValue
    private int productId;

    public int getProductId() {
        return productId;
    }

    public int getLikes() {
        return likesService.getLikesCount(String.valueOf(productId));
    }

    public int getDislikes() {
        return likesService.getDislikesCount(String.valueOf(productId));
    }
}
