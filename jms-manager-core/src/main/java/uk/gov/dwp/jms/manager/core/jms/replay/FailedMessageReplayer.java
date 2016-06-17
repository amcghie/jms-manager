package uk.gov.dwp.jms.manager.core.jms.replay;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import uk.gov.dwp.jms.manager.core.client.FailedMessage;
import uk.gov.dwp.jms.manager.core.client.FailedMessageResource;

import java.util.function.Function;

public class FailedMessageReplayer {

    private final JmsTemplate jmsTemplate;
    private final FailedMessageResource failedMessageResource;
    private final Function<FailedMessage, MessageCreator> failedMessageCreatorFactory;

    public FailedMessageReplayer(JmsTemplate jmsTemplate,
                                 FailedMessageResource failedMessageResource,
                                 Function<FailedMessage, MessageCreator> failedMessageCreatorFactory) {
        this.jmsTemplate = jmsTemplate;
        this.failedMessageResource = failedMessageResource;
        this.failedMessageCreatorFactory = failedMessageCreatorFactory;
    }

    public void replay(FailedMessage failedMessage, String queueName) {
        jmsTemplate.send(queueName, failedMessageCreatorFactory.apply(failedMessage));
        failedMessageResource.reprocess(failedMessage.getFailedMessageId());
    }
}
