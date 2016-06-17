package uk.gov.dwp.jms.manager.core.service;

import org.junit.Test;
import uk.gov.dwp.jms.manager.core.client.Destination;
import uk.gov.dwp.jms.manager.core.client.FailedMessage;
import uk.gov.dwp.jms.manager.core.client.FailedMessageBuilder;
import uk.gov.dwp.jms.manager.core.client.FailedMessageId;
import uk.gov.dwp.jms.manager.core.dao.DestinationStatisticsDao;
import uk.gov.dwp.jms.manager.core.dao.FailedMessageDao;
import uk.gov.dwp.jms.manager.core.dao.FailedMessageLabelsDao;

import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static uk.gov.dwp.jms.manager.core.domain.FailedMessageMatcher.aFailedMessage;

public class FailedMessageResourceImplTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();
    private static final FailedMessageId ANOTHER_FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();

    private final FailedMessageDao failedMessageDao = mock(FailedMessageDao.class);
    private final FailedMessageLabelsDao failedMessageLabelsDao = mock(FailedMessageLabelsDao.class);
    private final DestinationStatisticsDao destinationStatisticsDao = mock(DestinationStatisticsDao.class);

    private final FailedMessageResourceImpl underTest = new FailedMessageResourceImpl(failedMessageDao, failedMessageLabelsDao, destinationStatisticsDao);
    private final Destination destination = mock(Destination.class);
    private final FailedMessage failedMessage = FailedMessageBuilder.aFailedMessage().withFailedMessageId(FAILED_MESSAGE_ID).withDestination(destination).build();

    @Test
    public void testCreate() throws Exception {
        underTest.create(failedMessage);

        verify(failedMessageDao).insert(argThat(aFailedMessage().withFailedMessageId(equalTo(FAILED_MESSAGE_ID))));
        verify(destinationStatisticsDao).addFailed(destination);
    }

    @Test
    public void testFindById() throws Exception {
        when(failedMessageDao.findById(FAILED_MESSAGE_ID)).thenReturn(failedMessage);
        when(failedMessageLabelsDao.findLabelsById(FAILED_MESSAGE_ID)).thenReturn(treeSet("A Label"));

        FailedMessage failedMessage = underTest.getFailedMessage(FAILED_MESSAGE_ID);
        assertThat(failedMessage, is(aFailedMessage()
                .withFailedMessageId(equalTo(FAILED_MESSAGE_ID))
                .withLabels(contains("A Label"))
        ));

        verify(failedMessageDao).findById(FAILED_MESSAGE_ID);
        verify(failedMessageLabelsDao).findLabelsById(FAILED_MESSAGE_ID);
    }

    private TreeSet<String> treeSet(String...labels) {
        return Arrays.stream(labels).collect(TreeSet::new, TreeSet::add, TreeSet::addAll);
    }

    @Test
    public void testFindAllFailedMessages() throws Exception {
        when(failedMessageDao.find()).thenReturn(Collections.singletonList(failedMessage));
        when(failedMessageLabelsDao.findLabelsById(FAILED_MESSAGE_ID)).thenReturn(treeSet("A Label"));

        assertThat(underTest.getFailedMessages(), contains(aFailedMessage().withFailedMessageId(equalTo(FAILED_MESSAGE_ID)).withLabels(contains("A Label"))));

        verify(failedMessageDao).find();
        verify(failedMessageLabelsDao).findLabelsById(FAILED_MESSAGE_ID);
    }

    @Test
    public void testReprocess() throws Exception {
        when(failedMessageDao.findById(FAILED_MESSAGE_ID)).thenReturn(failedMessage);
        underTest.reprocess(FAILED_MESSAGE_ID);

        verify(failedMessageDao).delete(FAILED_MESSAGE_ID);
        verify(failedMessageLabelsDao).remove(FAILED_MESSAGE_ID);
        verify(destinationStatisticsDao).reprocess(destination);
    }

    @Test
    public void deleteFailedMessages() {

        underTest.delete(Arrays.asList(FAILED_MESSAGE_ID, ANOTHER_FAILED_MESSAGE_ID));

        verify(failedMessageDao).delete(FAILED_MESSAGE_ID);
        verify(failedMessageLabelsDao).remove(FAILED_MESSAGE_ID);
        verify(failedMessageDao).delete(ANOTHER_FAILED_MESSAGE_ID);
        verify(failedMessageLabelsDao).remove(ANOTHER_FAILED_MESSAGE_ID);
    }
}