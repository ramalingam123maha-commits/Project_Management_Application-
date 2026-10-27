package com.projectflow.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/** Shared helpers: real security chain, H2 database, unique emails per test so tests stay independent. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class IntegrationTestSupport {

    @Autowired protected MockMvc mvc;
    protected final ObjectMapper om = new ObjectMapper();

    protected ResultActions call(MockHttpServletRequestBuilder req, String token, String json) throws Exception {
        if (token != null) {
            req.header("Authorization", "Bearer " + token);
        }
        if (json != null) {
            req.contentType(MediaType.APPLICATION_JSON).content(json);
        }
        return mvc.perform(req);
    }

    protected String uniqueEmail(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8) + "@x.com";
    }

    protected JsonNode json(ResultActions r) throws Exception {
        return om.readTree(r.andReturn().getResponse().getContentAsString());
    }

    /** Registers a Member and returns {token, id, email}. */
    protected TestUser registerUser(String name) throws Exception {
        String email = uniqueEmail(name.toLowerCase());
        JsonNode body = json(call(post("/api/auth/register"), null,
                "{\"name\":\"" + name + "\",\"email\":\"" + email + "\",\"password\":\"password1\"}"));
        return new TestUser(body.get("token").asText(), body.get("user").get("id").asLong(), email);
    }

    protected String adminToken() throws Exception {
        return json(call(post("/api/auth/login"), null,
                "{\"email\":\"admin@projectflow.local\",\"password\":\"Admin@123456\"}")).get("token").asText();
    }

    /** Registers a user and promotes them to PROJECT_MANAGER; the token keeps working because roles are read from the DB. */
    protected TestUser registerManager(String name) throws Exception {
        TestUser u = registerUser(name);
        call(patch("/api/users/" + u.id() + "/role"), adminToken(), "{\"role\":\"PROJECT_MANAGER\"}");
        return u;
    }

    protected long createProject(String token, String name) throws Exception {
        return json(call(post("/api/projects"), token, "{\"name\":\"" + name + "\"}")).get("id").asLong();
    }

    protected long createTask(String token, long projectId, String title, Long assigneeId) throws Exception {
        String assignee = assigneeId == null ? "" : ",\"assigneeId\":" + assigneeId;
        return json(call(post("/api/projects/" + projectId + "/tasks"), token,
                "{\"title\":\"" + title + "\"" + assignee + "}")).get("id").asLong();
    }

    protected record TestUser(String token, long id, String email) {
    }
}
