package uk.gov.dwp.jms.manager.core.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class SearchRequest {

    private final Optional<String> brokerName;
    private final Optional<String> queueName;

    SearchRequest(@JsonProperty("brokerName") Optional<String> brokerName,
                  @JsonProperty("queueName") Optional<String> queueName) {
        this.brokerName = brokerName;
        this.queueName = queueName;
    }

    public Optional<String> getBrokerName() {
        return brokerName;
    }

    public Optional<String> getQueueName() {
        return queueName;
    }

    public static SearchRequestBuilder aSearchRequest() {
        return new SearchRequestBuilder()
                .withBrokerName(Optional.empty())
                .withQueueName(Optional.empty());
    }

    public static class SearchRequestBuilder {

        private Optional<String> brokerName;
        private Optional<String> queueName;

        public SearchRequestBuilder withBrokerName(Optional<String> brokerName) {
            this.brokerName = brokerName;
            return this;
        }

        public SearchRequestBuilder withQueueName(Optional<String> queueName) {
            this.queueName = queueName;
            return this;
        }

        public SearchRequest build() {
            return new SearchRequest(brokerName, queueName);
        }
    }
}
