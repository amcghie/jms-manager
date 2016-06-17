package uk.gov.dwp.jms.manager.core.service;

import uk.gov.dwp.jms.manager.core.client.FailedMessage;
import uk.gov.dwp.jms.manager.core.client.FailedMessageBuilder;
import uk.gov.dwp.jms.manager.core.client.FailedMessageId;
import uk.gov.dwp.jms.manager.core.client.FailedMessageResource;
import uk.gov.dwp.jms.manager.core.dao.DestinationStatisticsDao;
import uk.gov.dwp.jms.manager.core.dao.FailedMessageDao;
import uk.gov.dwp.jms.manager.core.dao.FailedMessageLabelsDao;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public class FailedMessageResourceImpl implements FailedMessageResource {

    private final FailedMessageDao failedMessageDao;
    private final DestinationStatisticsDao destinationStatisticsDao;
    private final FailedMessageLabelsDao failedMessageLabelsDao;

    public FailedMessageResourceImpl(FailedMessageDao failedMessageDao, FailedMessageLabelsDao failedMessageLabelsDao, DestinationStatisticsDao destinationStatisticsDao) {
        this.failedMessageDao = failedMessageDao;
        this.destinationStatisticsDao = destinationStatisticsDao;
        this.failedMessageLabelsDao = failedMessageLabelsDao;
    }

    @Override
    public void create(FailedMessage failedMessage) {
        failedMessageDao.insert(failedMessage);
        destinationStatisticsDao.addFailed(failedMessage.getDestination());
    }

    @Override
    public void reprocess(FailedMessageId failedMessageId) {
        FailedMessage failedMessage = failedMessageDao.findById(failedMessageId);
        failedMessageLabelsDao.remove(failedMessageId);
        failedMessageDao.delete(failedMessageId);
        destinationStatisticsDao.reprocess(failedMessage.getDestination());
    }

    @Override
    public FailedMessage getFailedMessage(FailedMessageId failedMessageId) {
        return ofNullable(failedMessageDao.findById(failedMessageId))
                .map(this::getLabels)
                .orElse(null);
    }

    @Override
    public void addLabel(FailedMessageId failedMessageId, String label) {
        failedMessageLabelsDao.addLabel(failedMessageId, label);
    }

    @Override
    public void setLabels(FailedMessageId failedMessageId, Set<String> labels) {
        failedMessageLabelsDao.setLabels(failedMessageId, labels);
    }

    @Override
    public List<FailedMessage> getFailedMessages() {
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

    @Override
    public void delete(List<FailedMessageId> failedMessageIds) {
        failedMessageIds.forEach(failedMessageId -> {
            failedMessageDao.delete(failedMessageId);
            failedMessageLabelsDao.remove(failedMessageId);
        });
    }
}
