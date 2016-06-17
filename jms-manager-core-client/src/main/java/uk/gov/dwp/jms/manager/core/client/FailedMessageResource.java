package uk.gov.dwp.jms.manager.core.client;

import javax.ws.rs.*;
import java.util.List;
import java.util.Set;

@Consumes("application/json")
@Produces("application/json")
@Path("/failed-message")
public interface FailedMessageResource {

    @GET
    @Path("/{failedMessageId}")
    FailedMessage getFailedMessage(@PathParam("failedMessageId") FailedMessageId failedMessgeId);

    @PUT
    @Path("/{failedMessageId}/add/{label}")
    void addLabel(@PathParam("failedMessageId") FailedMessageId failedMessageId, @PathParam("label") String label);

    @PUT
    @Path("/{failedMessageId}/add")
    void setLabels(@PathParam("failedMessageId") FailedMessageId failedMessageId, Set<String> labels);

    @GET
    @Path("/all")
    List<FailedMessage> getFailedMessages();

    @DELETE
    void delete(List<FailedMessageId> failedMessageIds);

    @POST
    @Path("/{failedMessageId}")
    void create(@PathParam("failedMessageId") FailedMessage failedMessage);

    @POST
    @Path("/{failedMessageId}/reprocess")
    void reprocess(@PathParam("failedMessageId") FailedMessageId failedMessageId);
}
