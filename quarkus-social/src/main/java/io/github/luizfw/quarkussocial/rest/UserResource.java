package io.github.luizfw.quarkussocial.rest;


import io.github.luizfw.quarkussocial.rest.dto.CreateUserRequest;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/users")
public class UserResource {

    @POST
    public Response createUser(final CreateUserRequest userRequest) {
        return Response.ok().build();
    }
}
