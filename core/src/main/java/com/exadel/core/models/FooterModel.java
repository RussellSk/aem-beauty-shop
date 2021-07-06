package com.exadel.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.time.LocalDate;

@Model(
        adaptables = SlingHttpServletRequest.class,
        resourceType = FooterModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class FooterModel {

    // points to AEM component definition in ui.apps
    static final String RESOURCE_TYPE = "exadel/components/content/footer";

    @ValueMapValue
    @Default(values = "Beauty Shop Default")
    private String companyName;

    @ValueMapValue
    @Default(values = "Description Default")
    private String description;

    @ValueMapValue
    @Default(values = "Exadel")
    private String copyRight;

    public String getCompanyName() {
        return companyName;
    }

    public String getDescription() {
        return description;
    }

    public String getCopyRight() {
        return copyRight + " © " + LocalDate.now().getYear();
    }
}
