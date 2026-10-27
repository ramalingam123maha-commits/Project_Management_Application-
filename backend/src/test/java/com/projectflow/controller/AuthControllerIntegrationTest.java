package com.projectflow.controller;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIntegrationTest extends IntegrationTestSupport {

    @Test
    void registerLoginAndMe() throws Exception {
        String email = uniqueEmail("ann");
        String body = "{\"name\":\"Ann\",\"email\":\"" + email.toUpperCase() + "\",\"password\":\"password1\"}";

        call(post("/api/auth/register"), null, body)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", not(emptyString())))
                .andExpect(jsonPath("$.user.role").value("MEMBER"))
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist());

        String token = json(call(post("/api/auth/login"), null,
                "{\"email\":\"" + email + "\",\"password\":\"password1\"}")
                .andExpect(status().isOk())).get("token").asText();

        call(get("/api/auth/me"), token, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void duplicateEmailReturns409WithErrorBody() throws Exception {
        String email = uniqueEmail("dup");
        String body = "{\"name\":\"Dup\",\"email\":\"" + email + "\",\"password\":\"password1\"}";
        call(post("/api/auth/register"), null, body).andExpect(status().isCreated());

        call(post("/api/auth/register"), null, body)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message", not(emptyString())));
    }

    @Test
    void invalidRegistrationReturnsFieldErrors() throws Exception {
        call(post("/api/auth/register"), null, "{\"name\":\"\",\"email\":\"not-an-email\",\"password\":\"short\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    void malformedJsonReturns400() throws Exception {
        call(post("/api/auth/login"), null, "{bad json").andExpect(status().isBadRequest());
    }

    @Test
    void wrongPasswordReturns401() throws Exception {
        TestUser u = registerUser("Bob");
        call(post("/api/auth/login"), null, "{\"email\":\"" + u.email() + "\",\"password\":\"wrongpass1\"}")
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointsRequireValidToken() throws Exception {
        call(get("/api/auth/me"), null, null).andExpect(status().isUnauthorized());
        call(get("/api/auth/me"), "garbage", null).andExpect(status().isUnauthorized());
        call(get("/api/projects"), null, null).andExpect(status().isUnauthorized());
        call(get("/api/dashboard"), null, null).andExpect(status().isUnauthorized());
    }
}
