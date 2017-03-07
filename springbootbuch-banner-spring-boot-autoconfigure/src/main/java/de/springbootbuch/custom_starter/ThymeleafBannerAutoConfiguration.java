package de.springbootbuch.custom_starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition.ConfigurationPhase;
import org.springframework.core.env.Environment;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Configuration
@EnableConfigurationProperties(ThymeleafBannerProperties.class)
@ConditionalOnClass({SpringTemplateEngine.class, ThymeleafAutoConfiguration.class})
@AutoConfigureAfter(CacheAutoConfiguration.class)
@AutoConfigureBefore(ThymeleafAutoConfiguration.class)
class ThymeleafBannerAutoConfiguration {

    static class OnNoBannerButFun extends AllNestedConditions {

        public OnNoBannerButFun() {
            // This controlls wether beans are added or not.
            // If you would want to prevent configuration classes,
            // use ConfigurationPhase.PARSE_CONFIGURATION
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        // See https://github.com/spring-projects/spring-boot/issues/2541
        @ConditionalOnProperty(name = "spring.main.banner-mode", havingValue = "off")
        static class OnBannerTurnedOff {
        }

        @ConditionalOnClass(ObjectMapper.class)
        static class OnObjectMapperAvailable {            
        }
        
        @ConditionalOnBean(CacheManager.class)
        static class OnCacheManagerAvailable {
        }             

        @ConditionalOnProperty("thymeleaf-banner.cacheName")
        static class OnCacheNameSpecified {
        }
    }

    @Bean
    @ConditionalOnClass(ObjectMapper.class)    
    @Conditional(OnNoBannerButFun.class)
    public BannerSupplier joshsBannerSupplier(
            ObjectMapper objectMapper, 
            CacheManager cacheManager, 
            ThymeleafBannerProperties bannerProperties
    ) {
        return new JoshsBannerSupplier(objectMapper, cacheManager.getCache(bannerProperties.getCacheName()));
    }

    @Bean
    @ConditionalOnMissingBean(BannerSupplier.class)
    @ConditionalOnProperty(name = "spring.main.banner-mode", havingValue = "off")
    public BannerSupplier emptyBannerSupplier() {
        return context -> context.getModelFactory().createModel();
    }

    @Bean
    @ConditionalOnMissingBean(BannerSupplier.class)
    @ConditionalOnBean(Banner.class)
    public BannerSupplier defaultBannerSupplier(final Environment environment, final Banner banner) {
        return new DefaultBannerSupplier(environment, banner);
    }

    @Bean
    public ThymeleafBannerDialect webBannerDialect(final BannerSupplier bannerSupplier) {
        return new ThymeleafBannerDialect(bannerSupplier);
    }
}
