package uk.gov.dwp.jms.manager.web.search;

import uk.gov.dwp.jms.manager.core.client.*;
import uk.gov.dwp.jms.manager.web.w2ui.BaseW2UIRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.apache.cxf.common.util.StringUtils.split;
import static uk.gov.dwp.jms.manager.core.client.FailedMessageId.fromString;
import static uk.gov.dwp.jms.manager.core.client.SearchRequest.aSearchRequest;

@Path("/failed-messages")
@Produces(TEXT_HTML)
public class FailedMessageListController {

    private final FailedMessageResource failedMessageResource;
    private final FailedMessageSearchResource failedMessageSearchResource;
    private FailedMessagesJsonSerializer failedMessagesJsonSerializer;

    public FailedMessageListController(FailedMessageResource failedMessageResource, FailedMessageSearchResource failedMessageSearchResource, FailedMessagesJsonSerializer failedMessagesJsonSerializer) {
        this.failedMessageResource = failedMessageResource;
        this.failedMessageSearchResource = failedMessageSearchResource;
        this.failedMessagesJsonSerializer = failedMessagesJsonSerializer;
    }

    @GET
    public FailedMessageListPage getFailedMessages() {
        return getFailedMessageListPage(aSearchRequest());
    }

    private FailedMessageListPage getFailedMessageListPage(SearchRequest.SearchRequestBuilder searchRequestBuilder) {
        List<FailedMessage> failedMessages = failedMessageSearchResource.findMessages(searchRequestBuilder.build());
        return new FailedMessageListPage(failedMessages, failedMessagesJsonSerializer);
    }

    @GET
    @Path("/{brokerName}")
    public FailedMessageListPage getFailedMessages(@PathParam("brokerName") String brokerName) {
        return getFailedMessageListPage(aSearchRequest().withBrokerName(of(brokerName)));
    }

    @GET
    @Path("/{brokerName}/{destination}")
    public FailedMessageListPage getFailedMessages(@PathParam("brokerName") String brokerName, @PathParam("destination") String destinationName) {
        return getFailedMessageListPage(aSearchRequest().withBrokerName(of(brokerName)).withQueueName(of(destinationName)));
    }

    @POST
    @Path("/delete")
    @Consumes("application/json")
    public String deleteFailedMessages(BaseW2UIRequest request) {
        failedMessageResource.delete(toFailedMessageIds(request));
        return "{ 'status': 'success' }";
    }

    private List<FailedMessageId> toFailedMessageIds(BaseW2UIRequest request) {
        return request.getSelected().stream().map(FailedMessageId::fromString).collect(toList());
    }

    @POST
    @Path("/labels")
    @Consumes("application/json")
    public String updateLabelsOnFailedMessages(LabelRequest request) {
        request.getChanges()
                .stream()
                .forEach(change -> failedMessageResource.setLabels(fromString(change.getRecid()), extractLabels(change)));
        return "{ 'status': 'success' }";
    }

    private Set<String> extractLabels(LabelRequest.Change change) {
        return Stream.of(split(change.getLabels(), ","))
                .map(String::trim)
                .collect(toSet());
    }

    @POST
    @Path("/resend")
    @Consumes(MediaType.APPLICATION_JSON)
    public String resendFailedMessages(BaseW2UIRequest request) {
        // Resend
        return  "{ 'status': 'success' }";
    }

    @POST
    @Path("/data")
    @Consumes(MediaType.APPLICATION_JSON)
    public String getData(BaseW2UIRequest request) {
        List<FailedMessage> failedMessages = failedMessageResource.getFailedMessages();
        return getJson(failedMessages);
    }

    private String getJson(List<FailedMessage> failedMessages) {
        return new StringBuilder("{ ")
                .append("'status': 'success'")
                .append(", 'total': ").append(failedMessages.size())
                .append(", 'records': ").append(failedMessagesJsonSerializer.asJson(failedMessages))
                .append(" }")
                .toString();
    }
}
