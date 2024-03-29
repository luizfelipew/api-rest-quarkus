package io.github.luizfw.quarkussocial.rest;

import io.github.luizfw.quarkussocial.domain.model.Follower;
import io.github.luizfw.quarkussocial.domain.model.Post;
import io.github.luizfw.quarkussocial.domain.model.User;
import io.github.luizfw.quarkussocial.domain.repository.FollowerRepository;
import io.github.luizfw.quarkussocial.domain.repository.PostRepository;
import io.github.luizfw.quarkussocial.domain.repository.UserRepository;
import io.github.luizfw.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;

    @Inject
    PostRepository postRepository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    void setUp() {
        // usuário padrão dos testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();
        // criada a postagem para esse usuário
        Post post = new Post();
        post.setText("Hello");
        post.setUser(user);
        postRepository.persist(post);

        // usuário que não segue ninguem
        var userNotFollower = new User();
        userNotFollower.setAge(33);
        userNotFollower.setName("Cicrano");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        // usuário seguidor
        var userFollower = new User();
        userFollower.setAge(31);
        userFollower.setName("Seguidor");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);

    }

    @Test
    @DisplayName("should create a post for a user")
    void createPostTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("some text");

        var jsonRequest = JsonbBuilder
                .create()
                .toJson(postRequest);

        given()
                .contentType(ContentType.JSON)
                .body(jsonRequest)
                .pathParam("userId", userId)
                .when()
                .post()
                .then()
                .statusCode(201);


    }

    @Test
    @DisplayName("should return 404 when trying to make a post for an inexistent user")
    void postForAnInexistentUserTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("some text");

        var jsonRequest = JsonbBuilder
                .create()
                .toJson(postRequest);

        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(jsonRequest)
                .pathParam("userId", inexistentUserId)
                .when()
                .post()
                .then()
                .statusCode(404);


    }

    @Test
    @DisplayName("should return 404 when user doesn't exist")
    void listPostUserNotFoundTest() {
        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(404);

    }

    @Test
    @DisplayName("should return 400 when followerId header is not present")
    void listPostFollowerHeaderNotSendTest() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @DisplayName("should return 400 when follower doesn't exist")
    void listPostFollowerNotFoundTest() {
        var inexistentFollowerId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .header("followerId", inexistentFollowerId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("FollowerId doesn't exist"));
    }

    @Test
    @DisplayName("should return 403 when follower isn't a follower")
    void listPostNotFollowerTest() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
                .when()
                .get()
                .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts"));
    }

    @Test
    @DisplayName("should return posts")
    void listPostsTest() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }
}