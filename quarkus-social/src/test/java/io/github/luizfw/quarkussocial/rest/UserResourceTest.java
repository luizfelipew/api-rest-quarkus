package io.github.luizfw.quarkussocial.rest;

import io.github.luizfw.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.json.bind.JsonbBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserResourceTest {

    @Test
    @DisplayName("should create a user successfully")
    void createUserTest() {
        var user = new CreateUserRequest();
        user.setName("Fulano");
        user.setAge(30);

        var jsonRequest = JsonbBuilder
                .create()
                .toJson(user);

        var response = given()
                .contentType(ContentType.JSON)
                .body(jsonRequest)
                .when()
                .post("/users")
                .then()
                .extract()
                .response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }
}