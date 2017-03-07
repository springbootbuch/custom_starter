package de.springbootbuch.custom_starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thymeleaf-banner")
public class ThymeleafBannerProperties {
    /**
     * The name of the cache that should be used by
     * {@link JoshsBannerSupplier}.
     */
    private String cacheName;

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }
}
