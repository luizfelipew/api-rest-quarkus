package io.github.luizfw.quarkussocial.rest;

import io.github.luizfw.quarkussocial.domain.model.Follower;
import io.github.luizfw.quarkussocial.domain.repository.FollowerRepository;
import io.github.luizfw.quarkussocial.domain.repository.UserRepository;
import io.github.luizfw.quarkussocial.rest.dto.FollowerRequest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.val;

import java.util.Objects;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private FollowerRepository followerRepository;
    private UserRepository userRepository;
    @PathParam("userId")
    private String userId;

    @Inject
    public FollowerResource(FollowerRepository followerRepository, UserRepository userRepository) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }


    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request) {

        if (userId.equals(request.getFollowerId())) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("You can't follow yourself")
                    .build();
        }

        val user = userRepository.findById(userId);
        if (Objects.isNull(user)){
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        val follower = userRepository.findById(request.getFollowerId());

        boolean follows = followerRepository.follows(follower, user);

        if (!follows) {
            val entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);

            followerRepository.persist(entity);
        }



        return Response
                .status(Response.Status.NO_CONTENT)
                .build();
    }
}

