package de.springbootbuch.custom_starter;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;

@FunctionalInterface
interface BannerSupplier {

    IModel get(ITemplateContext context);
}
