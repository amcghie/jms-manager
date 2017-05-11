package uk.gov.dwp.jms.manager.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.dwp.jms.manager.core.configuration.DaoConfig;
import uk.gov.dwp.jms.manager.core.configuration.DaoProperties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {
                DaoConfig.class,
                MongoAutoConfiguration.class,
                MongoDataAutoConfiguration.class},
        initializers = ConfigFileApplicationContextInitializer.class)
public abstract class AbstractMongoDaoTest {

    @Autowired
    protected MongoClient mongoClient;
    @Autowired
    protected DaoProperties daoProperties;
    protected DBCollection collection;

    @Before
    public void setUp() {
        collection = mongoClient.getDB(daoProperties.getDbName()).getCollection(getCollectionName());
        collection.remove(new BasicDBObject());
    }

    @After
    public void tearDown() {
        collection.remove(new BasicDBObject());
    }

    protected abstract String getCollectionName();

}
