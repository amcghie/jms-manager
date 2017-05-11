package uk.gov.dwp.jms.manager.core.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.dwp.jms.manager.core.JmsManagerApplication;
import uk.gov.dwp.jms.manager.core.client.Destination;
import uk.gov.dwp.jms.manager.core.client.FailedMessageResource;
import uk.gov.dwp.jms.manager.core.configuration.DaoProperties;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static uk.gov.dwp.jms.manager.core.client.FailedMessageBuilder.aFailedMessage;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JmsManagerApplication.class)
public class CreateFailedMessagesIntTest {

    @Autowired
    private FailedMessageResource failedMessageResource;

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private DaoProperties daoProperties;

    @Before
    public void setUp() {
        clearDownDb();
    }

//    @After
//    public void tearDown() {
//        clearDownDb();
//    }

    private void clearDownDb() {
        DB db = mongoClient.getDB(daoProperties.getDbName());
        for (String name : db.getCollectionNames()) {
            if (!name.startsWith("system.")) {
                db.getCollection(name).remove(new BasicDBObject());
            }
        }
    }

    private List<Destination> destinations = Arrays.asList(
            new Destination("internal", "uc.core.queue"),
            new Destination("internal", "uc.agent.core.queue"),
            new Destination("external", "uc.cis.queue"),
            new Destination("external", "uc.rte.queue")
    );

    @Test
    public void testCreate() throws Exception {
        for (int i=0; i<10; i++) {
            failedMessageResource.create(aFailedMessage()
                    .withDestination(destinations.get(i % 4))
                    .withContent("{ \"name\": \"foo\" }")
                    .withSentDateTime(ZonedDateTime.now(UTC).minusSeconds(2))
                    .withFailedDateTime(ZonedDateTime.now(UTC))
                    .build()
            );
        }
    }

}