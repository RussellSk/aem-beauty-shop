package com.exadel.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = SlingHttpServletRequest.class,
        resourceType = FooterModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class BannerModel {

    // points to AEM component definition in ui.apps
    static final String RESOURCE_TYPE = "exadel/components/content/banner";

    @ValueMapValue
    @Default(values = "Beauty Shop Banner Default")
    private String name; //maps variable to jcr property named "name"

    @ValueMapValue
    @Default(values = "Default description")
    private String description; //maps variable to jcr property named "description"

    @ValueMapValue
    @Default(values = "/")
    private String primaryUrl;

    @ValueMapValue
    @Default(values = "/")
    private String secondaryUrl;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrimaryUrl() {
        return primaryUrl;
    }

    public String getSecondaryUrl() {
        return secondaryUrl;
    }
}
