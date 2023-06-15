package io.github.luizfw.quarkussocial.rest;


import io.github.luizfw.quarkussocial.domain.model.User;
import io.github.luizfw.quarkussocial.domain.repository.UserRepository;
import io.github.luizfw.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Objects;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserRepository userRepository;

    public UserResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @POST
    @Transactional
    public Response createUser(final CreateUserRequest userRequest) {
        User user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        userRepository.persist(user);

        return Response.ok(user).build();
    }

    @GET
    public Response listAll() {
        PanacheQuery<User> query = userRepository.findAll();
        return Response
                .ok(query.list())
                .build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        User user = userRepository.findById(id);

        if (Objects.nonNull(user)) {
            userRepository.delete(user);
            return Response
                    .ok().build();
        }
        return Response
                .status(Response.Status.NOT_FOUND)
                .build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData) {
        User user = userRepository.findById(id);

        if (Objects.nonNull(user)) {
            user.setName(userData.getName());
            user.setAge(userData.getAge());
            return Response
                    .ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
