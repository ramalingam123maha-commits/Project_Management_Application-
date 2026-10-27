package com.projectflow.controller;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DashboardControllerIntegrationTest extends IntegrationTestSupport {

    @Test
    void eachRoleSeesItsOwnScope() throws Exception {
        TestUser pm = registerManager("Pm");
        TestUser otherPm = registerManager("Other");
        TestUser member = registerUser("Mem");
        long project = createProject(pm.token(), "A");
        long otherProject = createProject(otherPm.token(), "B");
        call(post("/api/projects/" + project + "/members"), pm.token(), "{\"email\":\"" + member.email() + "\"}");
        createTask(pm.token(), project, "t1", member.id());
        createTask(pm.token(), project, "t2", null);
        createTask(otherPm.token(), otherProject, "t3", null);

        call(get("/api/dashboard"), pm.token(), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scope").value("PROJECT_MANAGER"))
                .andExpect(jsonPath("$.totalProjects").value(1))
                .andExpect(jsonPath("$.totalTasks").value(2));

        call(get("/api/dashboard"), member.token(), null)
                .andExpect(jsonPath("$.scope").value("MEMBER"))
                .andExpect(jsonPath("$.totalProjects").value(1))
                .andExpect(jsonPath("$.totalTasks").value(1));

        call(get("/api/dashboard"), adminToken(), null)
                .andExpect(jsonPath("$.scope").value("ADMIN"))
                .andExpect(jsonPath("$.totalTasks").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
    }
}
