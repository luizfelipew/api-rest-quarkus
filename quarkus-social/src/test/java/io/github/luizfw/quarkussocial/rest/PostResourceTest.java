package io.github.luizfw.quarkussocial.rest;

import io.github.luizfw.quarkussocial.domain.model.User;
import io.github.luizfw.quarkussocial.domain.repository.UserRepository;
import io.github.luizfw.quarkussocial.rest.dto.CreatePostRequest;
import io.github.luizfw.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;

    Long userId;

    @BeforeEach
    @Transactional
    void setUp() {
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");

        userRepository.persist(user);
        userId = user.getId();
    }

    @Test
    @DisplayName("should create a post for a user")
    void createPostTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("some text");

        var jsonRequest = JsonbBuilder
                .create()
                .toJson(postRequest);

        var userId = 1;

        given()
                .contentType(ContentType.JSON)
                .body(jsonRequest)
                .pathParam("userId", userId)
                .when()
                .post()
                .then()
                .statusCode(201);


    }
}