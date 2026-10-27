package com.projectflow.controller;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends IntegrationTestSupport {

    @Test
    void onlyAdminCanListUsers() throws Exception {
        TestUser member = registerUser("Mem");
        call(get("/api/users"), member.token(), null).andExpect(status().isForbidden());
        call(get("/api/users"), adminToken(), null).andExpect(status().isOk());
    }

    @Test
    void adminPromotesUserAndRoleAppliesImmediately() throws Exception {
        TestUser user = registerUser("Prom");
        call(patch("/api/users/" + user.id() + "/role"), adminToken(), "{\"role\":\"PROJECT_MANAGER\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("PROJECT_MANAGER"));

        call(get("/api/auth/me"), user.token(), null).andExpect(jsonPath("$.role").value("PROJECT_MANAGER"));
    }

    @Test
    void memberCannotChangeRoles() throws Exception {
        TestUser a = registerUser("A");
        TestUser b = registerUser("B");
        call(patch("/api/users/" + b.id() + "/role"), a.token(), "{\"role\":\"ADMIN\"}")
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidAndMissingTargetsAreRejected() throws Exception {
        String admin = adminToken();
        TestUser u = registerUser("C");
        call(patch("/api/users/" + u.id() + "/role"), admin, "{\"role\":\"SUPERUSER\"}").andExpect(status().isBadRequest());
        call(patch("/api/users/" + u.id() + "/role"), admin, "{}").andExpect(status().isBadRequest());
        call(patch("/api/users/999999/role"), admin, "{\"role\":\"MEMBER\"}").andExpect(status().isNotFound());
    }
}
