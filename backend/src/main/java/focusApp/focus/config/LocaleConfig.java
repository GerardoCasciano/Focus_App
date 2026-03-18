package focusApp.focus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

//Scelta dinamica delle lingue
@Configuration
public class LocaleConfig {
    @Bean
    public LocaleResolver localeResolver(){
        AcceptHeaderLocaleResolver slr = new AcceptHeaderLocaleResolver();
        slr.setDefaultLocale(Locale.ITALIAN);
        //lingue supportate
        slr.setSupportedLocales(Arrays.asList(
                Locale.ITALIAN,
                Locale.ENGLISH,
                Locale.FRENCH,
                new Locale("es"),
                Locale.CHINESE,
                Locale.JAPANESE
        ));
        return slr;
    }
    @Bean
    public ResourceBundleMessageSource messageSource(){
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("message");
        source.setDefaultEncoding("UTF-8");
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }
}
