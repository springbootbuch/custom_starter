package de.springbootbuch.custom_starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import org.springframework.cache.Cache;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;

class JoshsBannerSupplier implements BannerSupplier {

    private final ObjectMapper objectMapper;
    
    private final Cache cache;
    
    JoshsBannerSupplier(ObjectMapper objectMapper, Cache cache) {
        this.objectMapper = objectMapper;
        this.cache = cache;        
    }
    
    @Override
    public IModel get(ITemplateContext context) {
        final String html = Optional.ofNullable(cache.get("quote", String.class))
                .orElseGet(this::getQuote);
        
        return context.getModelFactory().parse(context.getTemplateData(), html);
    }

    String getQuote() {
       final String url
               = "https://publish.twitter.com/oembed?url="
               + "https://twitter.com/starbuxman/status/702215432946593792";
       String quote = "<blockquote>n/a</blockquote";       
       try {           
           quote = (String) objectMapper.readValue(new URL(url), Map.class).get("html");
       } catch (IOException ex) {
       }
       return quote;
    }
}
