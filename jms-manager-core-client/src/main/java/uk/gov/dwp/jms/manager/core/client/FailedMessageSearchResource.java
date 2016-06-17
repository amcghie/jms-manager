package uk.gov.dwp.jms.manager.core.client;

import java.util.List;

public interface FailedMessageSearchResource {

    FailedMessage getFailedMessage(FailedMessageId failedMessageId);

    List<FailedMessage> findMessages(SearchRequest searchRequest);
}
