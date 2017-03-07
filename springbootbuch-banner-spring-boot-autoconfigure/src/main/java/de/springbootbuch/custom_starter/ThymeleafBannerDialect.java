package de.springbootbuch.custom_starter;

import java.util.HashSet;
import java.util.Set;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import static org.thymeleaf.templatemode.TemplateMode.HTML;

class ThymeleafBannerDialect implements IProcessorDialect {

    private final BannerSupplier bannerSupplier;

    public ThymeleafBannerDialect(final BannerSupplier bannerSupplier) {
        this.bannerSupplier = bannerSupplier;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public String getPrefix() {
        return "banner";
    }

    @Override
    public int getDialectProcessorPrecedence() {
        return 1000;
    }

    @Override
    public Set<IProcessor> getProcessors(final String actualPrefix) {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new AbstractElementTagProcessor(HTML, actualPrefix, "enableansibanners", true, null, false, 10) {
            @Override
            protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
                final IModelFactory mf = context.getModelFactory();
                final IModel model = mf.createModel();
                model.add(mf.setAttribute(mf.createOpenElementTag("script", "type", "text/javascript"), "src", "/js/ansi_up.js"));
                model.add(mf.createCloseElementTag("script"));
                model.add(mf.createOpenElementTag("script", "type", "text/javascript"));
                model.add(mf.createText("\n"
                        + "document.addEventListener('DOMContentLoaded', function(event) {\n"
                        + "   var banner = document.getElementsByClassName('banner');\n"
                        + "   for (var i = 0; i < banner.length; ++i) {\n"
                        + "      banner[i].innerHTML = ansi_up.ansi_to_html(banner[i].innerHTML);\n"
                        + "   }\n"
                        + "});\n"));
                model.add(mf.createCloseElementTag("script"));
                structureHandler.replaceWith(model, false);
            }
        });
        
        processors.add(new AbstractElementTagProcessor(HTML, actualPrefix, "show", true, null, false, 10) {
            @Override
            protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
                final IModel model = bannerSupplier.get(context);
                structureHandler.replaceWith(model, false);                
            }
        });
        
        return processors;
    }
}
