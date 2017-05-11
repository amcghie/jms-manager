package uk.gov.dwp.jms.manager.core.client;

import javax.ws.rs.*;
import java.util.List;
import java.util.Set;

@Consumes("application/json")
@Produces("application/json")
@Path("/failed-message")
public interface FailedMessageResource {

    /**
     * @deprecated Use the {@link FailedMessageSearchResource}
     * @param failedMessgeId
     * @return
     */
    @Deprecated
    @GET
    @Path("/{failedMessageId}")
    FailedMessage getFailedMessage(@PathParam("failedMessageId") FailedMessageId failedMessgeId);

    @PUT
    @Path("/{failedMessageId}/add/{label}")
    void addLabel(@PathParam("failedMessageId") FailedMessageId failedMessageId, @PathParam("label") String label);

    @PUT
    @Path("/{failedMessageId}/add")
    void setLabels(@PathParam("failedMessageId") FailedMessageId failedMessageId, Set<String> labels);

    /**
     * @deprecated Use the {@link FailedMessageSearchResource}
     * @return
     */
    @Deprecated
    @GET
    @Path("/all")
    List<FailedMessage> getFailedMessages();

    @DELETE
    void delete(List<FailedMessageId> failedMessageIds);

    @POST
    void create(FailedMessage failedMessage);

    @POST
    @Path("/{failedMessageId}/reprocess")
    void reprocess(@PathParam("failedMessageId") FailedMessageId failedMessageId);
}
