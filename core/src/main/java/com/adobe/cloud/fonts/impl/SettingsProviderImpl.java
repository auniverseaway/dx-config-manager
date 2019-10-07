package com.adobe.cloud.fonts.impl;

import java.util.Collections;
import java.util.Map;

import com.adobe.cloud.fonts.Settings;
import com.adobe.cloud.fonts.SettingsProvider;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SettingsProviderImpl implements SettingsProvider {

    private static String CONF_CONTAINER_BUCKET_NAME = "settings";

    public static String CLOUDCONFIG_PARENT = "cloudconfigs";

    static final String SERVICE_USER = "cloudconfig-scripttags";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private ConfigurationResourceResolver configResourceResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

@Override
public Settings getSettings(SlingHttpServletRequest request, String configName) {
    String configPath = CLOUDCONFIG_PARENT  + "/" + configName;
    Map<String, Object> serviceMap = Collections.<String, Object>singletonMap(ResourceResolverFactory.SUBSERVICE, SERVICE_USER);
    try (ResourceResolver configResolver = resolverFactory.getServiceResourceResolver(serviceMap)) {
        log.trace("Obtaining ResourceResolver with service user [{}]", SERVICE_USER);
        Resource environmentResource = getEnvironmentResource(configResolver, request, configPath);
        if (environmentResource != null) {
            return environmentResource.adaptTo(Settings.class);
        }
    } catch (LoginException e) {
        log.error("Unable to obtain ResourceResolver with service user [{}]", SERVICE_USER);
    }
    return null;
}

    private Resource getEnvironmentResource(ResourceResolver resolver, SlingHttpServletRequest request, String configPath) throws LoginException {
        PageManager pageMgr = resolver.adaptTo(PageManager.class);
        Page page = pageMgr != null ? pageMgr.getContainingPage(request.getResource()) : null;
        if (page != null && page.hasContent()) {
            log.trace("Resolving context-aware configuration for resource [{}]", page.getContentResource().getPath());
            Resource configResource = configResourceResolver.getResource(
                    page.getContentResource(),
                    CONF_CONTAINER_BUCKET_NAME,
                    configPath);
            if (configResource != null) {
                Page configPage = pageMgr.getContainingPage(configResource);
                if (configPage != null) {
                    return configPage.hasContent() ? configPage.getContentResource() : null;
                }
            } else {
                log.debug("No configuration found.");
            }
        } else {
            log.debug("Resource [{}] is not adaptable to Page or has no content", request != null ? request.getResource() : null);
        }
        return null;
    }
}
