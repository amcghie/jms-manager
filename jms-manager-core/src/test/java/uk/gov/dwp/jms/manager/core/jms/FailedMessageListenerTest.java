package uk.gov.dwp.jms.manager.core.jms;

import org.junit.Test;
import uk.gov.dwp.jms.manager.core.client.FailedMessage;
import uk.gov.dwp.jms.manager.core.client.FailedMessageResource;

import javax.jms.JMSException;
import javax.jms.Message;

import static org.mockito.Mockito.*;

public class FailedMessageListenerTest {

    private final FailedMessageFactory failedMessageFactory = mock(FailedMessageFactory.class);
    private final FailedMessageResource failedMessageResource = mock(FailedMessageResource.class);

    private final Message message = mock(Message.class);
    private final FailedMessage failedMessage = mock(FailedMessage.class);

    private final FailedMessageListener underTest = new FailedMessageListener(failedMessageFactory, failedMessageResource);

    @Test
    public void processMessageSuccessfully() throws Exception {
        when(failedMessageFactory.createFailedMessage(message)).thenReturn(failedMessage);

        underTest.onMessage(message);

        verify(failedMessageResource).create(failedMessage);
    }

    @Test
    public void exceptionIsThrownRetrievingTheJMSMessageId() throws JMSException {
        when(message.getJMSMessageID()).thenThrow(JMSException.class);

        underTest.onMessage(message);

        verifyZeroInteractions(failedMessageResource);
    }
}