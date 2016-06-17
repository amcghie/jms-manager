package uk.gov.dwp.jms.manager.core.service;

import uk.gov.dwp.jms.manager.core.client.*;
import uk.gov.dwp.jms.manager.core.dao.FailedMessageDao;
import uk.gov.dwp.jms.manager.core.dao.FailedMessageLabelsDao;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public class FailedMessageSearchResourceImpl implements FailedMessageSearchResource {

    private final FailedMessageDao failedMessageDao;
    private final FailedMessageLabelsDao failedMessageLabelsDao;

    public FailedMessageSearchResourceImpl(FailedMessageDao failedMessageDao,
                                           FailedMessageLabelsDao failedMessageLabelsDao) {
        this.failedMessageDao = failedMessageDao;
        this.failedMessageLabelsDao = failedMessageLabelsDao;
    }

    @Override
    public FailedMessage getFailedMessage(FailedMessageId failedMessageId) {
        return ofNullable(failedMessageDao.findById(failedMessageId))
                .map(this::getLabels)
                .orElse(null);
    }

    @Override
    public List<FailedMessage> findMessages(SearchRequest searchRequest) {
        return failedMessageDao.find()
                .parallelStream()
                .map(this::getLabels)
                .collect(Collectors.toList());
    }

    private FailedMessage getLabels(FailedMessage failedMessage) {
        return FailedMessageBuilder.clone(failedMessage)
                .withLabels(failedMessageLabelsDao.findLabelsById(failedMessage.getFailedMessageId()))
                .build();
    }
}
