package com.exadel.core.models;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Model(
        adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class TagsModel {

    private static final String TAG_PATH = "/content/cq:tags/exadel/brands/";

    /**
     * Amount of tags to display
     */
    @ValueMapValue
    @Default(intValues = 20)
    private int amount;

    @SlingObject
    private ResourceResolver resourceResolver;

    private List<String> tags;

    @PostConstruct
    protected void init() {
        tags = new ArrayList<>();
        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        Tag brandNamespace = tagManager.resolve(TAG_PATH);
        Iterator<Tag> brandTag = brandNamespace.listChildren();
        int tagsCount = 0;
        while (brandTag.hasNext()) {
            if (tagsCount++ >= getAmount()) break;
            Tag currentTag = brandTag.next();
            String tagName = currentTag.getName();
            long count = currentTag.getCount();
            tags.add(String.format("%s (%d)", tagName, count));
        }
    }

    public List<String> getTags() {
        return tags;
    }

    public int getAmount() {
        return amount;
    }
}
