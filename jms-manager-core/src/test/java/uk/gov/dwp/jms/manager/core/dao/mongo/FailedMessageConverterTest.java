package uk.gov.dwp.jms.manager.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.jms.manager.core.client.Destination;
import uk.gov.dwp.jms.manager.core.client.FailedMessageBuilder;
import uk.gov.dwp.jms.manager.core.client.FailedMessageId;
import uk.gov.dwp.jms.manager.core.configuration.DaoConfig;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jms.manager.core.client.FailedMessageId.newFailedMessageId;
import static uk.gov.dwp.jms.manager.core.dao.mongo.DBObjectMatcher.hasField;
import static uk.gov.dwp.jms.manager.core.dao.mongo.FailedMessageConverter.*;
import static uk.gov.dwp.jms.manager.core.domain.DestinationMatcher.aDestination;
import static uk.gov.dwp.jms.manager.core.domain.FailedMessageMatcher.aFailedMessage;

public class FailedMessageConverterTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = newFailedMessageId();
    private static final String FAILED_MESSAGE_ID_AS_STRING = FAILED_MESSAGE_ID.getId().toString();
    private static final Map<String, Object> SOME_PROPERTIES = new HashMap<String, Object>() {{
        put("propertyName", "propertyValue");
    }};
    private static final Destination SOME_DESTINATION = new Destination("broker", "queue.name");
    private static final BasicDBObject DESTINATION_DB_OBJECT = new BasicDBObject();
    private static final ZonedDateTime SENT_AT = ZonedDateTime.now(ZoneOffset.ofHours(1));
    private static final ZonedDateTime FAILED_AT = ZonedDateTime.now();

    private final ObjectConverter<Destination, DBObject> destinationDBObjectConverter = mock(ObjectConverter.class);
    private final ObjectConverter<Map<String, Object>, String> propertiesConverter = mock(ObjectConverter.class);

    private final FailedMessageConverter underTest = new DaoConfig().failedMessageConverter(destinationDBObjectConverter, propertiesConverter);

    private FailedMessageBuilder failedMessageBuilder;

    @Before
    public void setUp() {
        failedMessageBuilder = FailedMessageBuilder.aFailedMessage()
                .withFailedMessageId(FAILED_MESSAGE_ID)
                .withDestination(SOME_DESTINATION)
                .withContent("Hello")
                .withSentDateTime(SENT_AT)
                .withFailedDateTime(FAILED_AT)
                .withProperties(SOME_PROPERTIES);
    }

    @Test
    public void createId() {
        assertThat(underTest.createId(FAILED_MESSAGE_ID), equalTo(new BasicDBObject("_id", FAILED_MESSAGE_ID_AS_STRING)));
    }

    @Test
    public void mapNullDBObjectToFailedMessage() {
        assertThat(underTest.convertToObject(null), is(nullValue()));
    }

    @Test
    public void convertFailedMessage() {
        primePropertiesConverter(SOME_PROPERTIES, "{ \"propertyName\": \"propertyValue\" }");
        primeDestinationConverter(SOME_DESTINATION, DESTINATION_DB_OBJECT);

        DBObject dbObject = underTest.convertFromObject(failedMessageBuilder.build());
        assertThat(dbObject, allOf(
                hasField("_id", FAILED_MESSAGE_ID_AS_STRING),
                hasField(CONTENT, "Hello"),
                hasField(DESTINATION, DESTINATION_DB_OBJECT),
                hasField(PROPERTIES, "{ \"propertyName\": \"propertyValue\" }")
        ));

        assertThat(underTest.convertToObject(dbObject), is(aFailedMessage()
                .withFailedMessageId(equalTo(FAILED_MESSAGE_ID))
                .withContent(equalTo("Hello"))
                .withDestination(aDestination().withBrokerName("broker").withName("queue.name"))
                .withSentAt(SENT_AT)
                .withFailedAt(FAILED_AT)
                .withProperties(equalTo(SOME_PROPERTIES))
        ));
    }

    private void primeDestinationConverter(Destination destination, BasicDBObject destinationDbObject) {
        when(destinationDBObjectConverter.convertFromObject(destination)).thenReturn(destinationDbObject);
        when(destinationDBObjectConverter.convertToObject(destinationDbObject)).thenReturn(destination);
    }

    private void primePropertiesConverter(Map<String, Object> properties, String propertiesAsJson) {
        when(propertiesConverter.convertFromObject(properties)).thenReturn(propertiesAsJson);
        when(propertiesConverter.convertToObject(propertiesAsJson)).thenReturn(properties);
    }
}