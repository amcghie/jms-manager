package uk.gov.dwp.jms.manager.web.configuration;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import uk.gov.dwp.jms.manager.core.client.DestinationStatisticsResource;
import uk.gov.dwp.jms.manager.core.client.FailedMessageResource;
import uk.gov.dwp.jms.manager.web.common.jackson.PageMessageBodyWriter;
import uk.gov.dwp.jms.manager.web.common.mustache.MustachePageRenderer;
import uk.gov.dwp.jms.manager.web.search.FailedMessageListController;
import uk.gov.dwp.jms.manager.web.search.FailedMessagesJsonSerializer;
import uk.gov.dwp.jms.manager.web.summary.DestinationStatisticsController;
import uk.gov.dwp.jms.manager.web.summary.DestinationStatisticsJsonSerializer;

import javax.servlet.ServletException;
import java.util.ArrayList;

@Configuration
@Import({
        JacksonConfiguration.class,
        JmsManagerCoreClientConfiguration.class,
        MustacheConfiguration.class
})
@ImportResource({ "classpath:META-INF/cxf/cxf.xml" })
public class CxfBusConfiguration {

    @Bean
    public ServletRegistrationBean cxfServlet() throws ServletException {
        ServletRegistrationBean cxfServlet = new ServletRegistrationBean(new CXFServlet(), "/*");
        cxfServlet.addInitParameter("static-resources-list", "/static/.+");
        cxfServlet.addInitParameter("static-cache-control", "max-age=86400");
        return cxfServlet;
    }

    @Bean
    public Server rsServer(Bus bus,
                           FailedMessageResource failedMessageResource,
                           DestinationStatisticsResource destinationStatisticsResource,
                           JacksonConfiguration jacksonConfiguration,
                           MustachePageRenderer mustachePageRenderer) {
        bus.getInInterceptors().add(new LoggingInInterceptor());
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setAddress("/web");
        endpoint.setServiceBeans(new ArrayList<Object>() {{
            add(new FailedMessageListController(failedMessageResource, failedMessageSearchResource, new FailedMessagesJsonSerializer()));
            add(new DestinationStatisticsController(destinationStatisticsResource, new DestinationStatisticsJsonSerializer(jacksonConfiguration.objectMapper())));
        }});
        endpoint.setProvider(jacksonConfiguration.jacksonJsonProvider());
        endpoint.setProvider(new PageMessageBodyWriter(mustachePageRenderer));
        endpoint.setBus(bus);
        return endpoint.create();
    }
}
