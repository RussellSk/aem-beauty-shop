package com.exadel.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;


@Model(
        adaptables = SlingHttpServletRequest.class,
        resourceType = HeaderModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class HeaderModel {

    // points to AEM component definition in ui.apps
    static final String RESOURCE_TYPE = "exadel/components/content/header";

    @ValueMapValue
    @Default(values = "Beauty Shop Default")
    private String name; //maps variable to jcr property named "name"

    @ValueMapValue
    @Default(values = "Default description")
    private String description; //maps variable to jcr property named "description"

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
