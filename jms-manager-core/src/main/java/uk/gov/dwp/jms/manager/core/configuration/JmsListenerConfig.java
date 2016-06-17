package uk.gov.dwp.jms.manager.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import uk.gov.dwp.jms.manager.core.client.FailedMessageResource;
import uk.gov.dwp.jms.manager.core.jms.FailedMessageListener;
import uk.gov.dwp.jms.manager.core.jms.JmsMessagePropertyExtractor;
import uk.gov.dwp.jms.manager.core.jms.MessageTextExtractor;
import uk.gov.dwp.jms.manager.core.jms.activemq.ActiveMQDestinationExtractor;
import uk.gov.dwp.jms.manager.core.jms.activemq.ActiveMQFailedMessageFactory;

import javax.jms.ConnectionFactory;

@Configuration
@Import({ResourceConfiguration.class})
public class JmsListenerConfig {

    @Bean
    public FailedMessageListener failedMessageListener(FailedMessageResource failedMessageResource) {
        return new FailedMessageListener(
                new ActiveMQFailedMessageFactory(
                        new MessageTextExtractor(),
                        new ActiveMQDestinationExtractor("broker.name"),
                        new JmsMessagePropertyExtractor()),
                failedMessageResource);
    }

    @Bean
    public DefaultMessageListenerContainer dlqMessageListenerContainer(ConnectionFactory connectionFactory, JmsListenerProperties jmsListenerProperties, FailedMessageListener failedMessageListener) {
        DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(connectionFactory);
        defaultMessageListenerContainer.setDestinationName(jmsListenerProperties.getQueueName());
        defaultMessageListenerContainer.setupMessageListener(failedMessageListener);
        return defaultMessageListenerContainer;
    }
}
