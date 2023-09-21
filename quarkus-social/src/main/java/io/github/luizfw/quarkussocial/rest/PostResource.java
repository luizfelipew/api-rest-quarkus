package io.github.luizfw.quarkussocial.rest;

import io.github.luizfw.quarkussocial.domain.model.Post;
import io.github.luizfw.quarkussocial.domain.model.User;
import io.github.luizfw.quarkussocial.domain.repository.FollowerRepository;
import io.github.luizfw.quarkussocial.domain.repository.PostRepository;
import io.github.luizfw.quarkussocial.domain.repository.UserRepository;
import io.github.luizfw.quarkussocial.rest.dto.CreatePostRequest;
import io.github.luizfw.quarkussocial.rest.dto.PostResponse;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.val;

import java.util.Objects;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private FollowerRepository followerRepository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository postRepository, FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId);
        if (Objects.isNull(user)) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);

        postRepository.persist(post);

        return Response
                .status(Response.Status.CREATED)
                .build();
    }

    @GET
    public Response listPosts(@PathParam("userId") Long userId,
                              @HeaderParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);
        if (Objects.isNull(user)) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        if (Objects.isNull(followerId)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("You forgot the header followerId")
                    .build();
        }

        val follower = userRepository.findById(followerId);

        if (Objects.isNull(follower)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("FollowerId doesn't exist")
                    .build();
        }

        val follows = followerRepository.follows(follower, user);
        if (!follows) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("You can't see these posts")
                    .build();
        }


        var query = postRepository.find(
                "user", Sort.by("dateTime", Sort.Direction.Descending), user);

        var list = query.list();

        var postResponseList = list.stream()
                .map(PostResponse::fromEntity)
                .toList();

        return Response
                .ok(postResponseList)
                .build();
    }
}
