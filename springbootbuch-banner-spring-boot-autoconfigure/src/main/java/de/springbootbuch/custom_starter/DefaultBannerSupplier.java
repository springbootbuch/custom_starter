package de.springbootbuch.custom_starter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;

public class DefaultBannerSupplier implements BannerSupplier {

    private final String banner;

    public DefaultBannerSupplier(final Environment environment, final Banner banner) {
        final String bannerCharset = environment.getProperty("banner.charset", "UTF-8");
        try (
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final PrintStream printStream = new PrintStream(out, true, bannerCharset)
        ) {
            banner.printBanner(environment, DefaultBannerSupplier.class, printStream);
            this.banner = new String(out.toByteArray(), Charset.forName(bannerCharset));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IModel get(ITemplateContext context) {
        final IModelFactory modelFactory = context.getModelFactory();
        final IModel model = modelFactory.createModel();
        model.add(modelFactory.createOpenElementTag("pre", "class", "banner"));
        model.add(modelFactory.createText(banner));
        return model;
    }
}