package uk.gov.dwp.jms.manager.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.jms.manager.core.client.DestinationStatisticsResource;
import uk.gov.dwp.jms.manager.core.client.FailedMessageResource;
import uk.gov.dwp.jms.manager.core.client.FailedMessageSearchResource;
import uk.gov.dwp.jms.manager.core.dao.DestinationStatisticsDao;
import uk.gov.dwp.jms.manager.core.dao.FailedMessageDao;
import uk.gov.dwp.jms.manager.core.dao.FailedMessageLabelsDao;
import uk.gov.dwp.jms.manager.core.service.DestinationStatisticsResourceImpl;
import uk.gov.dwp.jms.manager.core.service.FailedMessageResourceImpl;
import uk.gov.dwp.jms.manager.core.service.FailedMessageSearchResourceImpl;

@Configuration
@Import({ DaoConfig.class })
public class ResourceConfiguration {

    @Bean
    public FailedMessageSearchResource failedMessageSearchResource(FailedMessageDao failedMessageDao,
                                                                   FailedMessageLabelsDao failedMessageLabelsDao) {

        return new FailedMessageSearchResourceImpl(failedMessageDao, failedMessageLabelsDao);
    }

    @Bean
    public DestinationStatisticsResource destinationStatisticsResource(DestinationStatisticsDao destinationStatisticsDao) {
        return new DestinationStatisticsResourceImpl(destinationStatisticsDao);
    }

    @Bean
    public FailedMessageResource failedMessageResource(FailedMessageDao failedMessageDao,
                                                       FailedMessageLabelsDao failedMessageLabelsDao,
                                                       DestinationStatisticsDao destinationStatisticsDao) {
        return new FailedMessageResourceImpl(failedMessageDao, failedMessageLabelsDao, destinationStatisticsDao);
    }
}
