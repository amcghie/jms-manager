package uk.gov.dwp.jms.manager.core.configuration;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.Bus;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import uk.gov.dwp.jms.manager.core.client.DestinationStatisticsResource;
import uk.gov.dwp.jms.manager.core.client.FailedMessageResource;
import uk.gov.dwp.jms.manager.core.client.FailedMessageSearchResource;

import java.util.Arrays;

@Configuration
@Import({
        JacksonConfiguration.class,
        ResourceConfiguration.class
})
@ImportResource({"classpath:META-INF/cxf/cxf.xml"})
public class CxfBusConfiguration {

    @Bean
    public ServletRegistrationBean cxfServlet() {
        return new ServletRegistrationBean(new CXFServlet(), "/*");
    }

    @Bean(initMethod = "create")
    public JAXRSServerFactoryBean failedMessageResource(Bus bus,
                                                        JacksonJsonProvider jacksonJsonProvider,
                                                        FailedMessageResource failedMessageResource,
                                                        FailedMessageSearchResource failedMessageSearchResource,
                                                        DestinationStatisticsResource destinationStatisticsResource) {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setServiceBeans(Arrays.asList(
                failedMessageResource, failedMessageSearchResource, destinationStatisticsResource
        ));
        endpoint.setAddress("/core");
        endpoint.setProvider(jacksonJsonProvider);
        endpoint.setBus(bus);
        endpoint.setFeatures(Arrays.asList(loggingFeature()));
        return endpoint;
    }

    @Bean
    public LoggingFeature loggingFeature() {
        return new LoggingFeature();
    }


}
