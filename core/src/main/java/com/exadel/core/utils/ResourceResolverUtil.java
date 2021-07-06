package com.exadel.core.utils;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import java.util.HashMap;
import java.util.Map;

public final class ResourceResolverUtil {
    private static final String SUB_SERVICE = "exadelbeautyshopuser";
    private ResourceResolverUtil(){}

    public static ResourceResolver getResourceResolver(ResourceResolverFactory resourceResolver)
            throws LoginException {
        Map<String, Object> authenticationInfo = new HashMap<>();
        authenticationInfo.put(ResourceResolverFactory.SUBSERVICE, SUB_SERVICE);
        return resourceResolver.getServiceResourceResolver(authenticationInfo);
    }

    public static void closeResourceResolver(ResourceResolver resourceResolver) {
        if (resourceResolver != null && resourceResolver.isLive()) {
            resourceResolver.close();
        }
    }
}
