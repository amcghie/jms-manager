package uk.gov.dwp.jms.manager.web.configuration;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.jms.manager.web.common.mustache.MustachePageRenderer;
import uk.gov.dwp.jms.manager.web.common.security.SecurityContext;

@Configuration
public class MustacheConfiguration {

    @Bean
    public MustacheFactory mustacheFactory() {
        return new DefaultMustacheFactory("mustache/");
    }

    @Bean
    public MustachePageRenderer mustacheRender(SecurityContext securityContext) {
        return new MustachePageRenderer(mustacheFactory(), securityContext);
    }
}
