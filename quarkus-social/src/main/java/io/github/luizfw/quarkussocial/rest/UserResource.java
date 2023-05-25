package io.github.luizfw.quarkussocial.rest;


import io.github.luizfw.quarkussocial.rest.dto.CreateUserRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @POST
    public Response createUser(final CreateUserRequest userRequest) {
        return Response.ok(userRequest).build();
    }
    @GET
    public Response listAll() {
        return Response.ok().build();
    }
}
