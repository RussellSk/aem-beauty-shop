package com.exadel.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Model(
        adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ProductModel {

    private static final short DESCRIPTION_MAX_WIDTH = 200;
    private static final short NAME_MAX_WIDTH = 100;

    @ValueMapValue
    @Default(values = "Product Name")
    private String name;

    @ValueMapValue
    @Default(values = "Default Brand")
    private String brand;

    @ValueMapValue
    @Default(values = "Default Type")
    private String type;

    @ValueMapValue
    @Default(doubleValues = 0.0)
    private double rating;

    @ValueMapValue
    private String createdAt;

    @ValueMapValue
    private String category;

    @ValueMapValue
    @Default(values = "Default Price")
    private String price;

    @ValueMapValue
    @Default(values = "/")
    private String image;

    @ValueMapValue
    @Default(values = "Default description")
    private String description;

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private ResourceResolver resourceResolver;

    private String pagePath;

    @PostConstruct
    protected void init() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        pagePath = Optional.ofNullable(pageManager)
                .map(pm -> pm.getContainingPage(currentResource))
                .map(p -> p.getPath() + ".html").orElse("");
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public double getRating() {
        return rating;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getCategory() {
        return category;
    }

    public String getPagePath() {
        return pagePath;
    }

    /**
     * Returns truncated name text
     */
    public String getTruncatedName() {
        return StringUtils.abbreviate(getName(), NAME_MAX_WIDTH);
    }

    /**
     * Returns truncated description text
     */
    public String getTruncatedDescription() {
        return StringUtils.abbreviate(getDescription(), DESCRIPTION_MAX_WIDTH);
    }
}
