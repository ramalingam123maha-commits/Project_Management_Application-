package com.projectflow.controller;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerIntegrationTest extends IntegrationTestSupport {

    @Test
    void memberCannotCreateProjectButManagerCan() throws Exception {
        TestUser member = registerUser("Mem");
        TestUser pm = registerManager("Pm");

        call(post("/api/projects"), member.token(), "{\"name\":\"X\"}").andExpect(status().isForbidden());
        call(post("/api/projects"), pm.token(), "{\"name\":\"  Alpha  \"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alpha"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.memberCount").value(1));
    }

    @Test
    void validationRejectsBadProject() throws Exception {
        TestUser pm = registerManager("Pm");
        call(post("/api/projects"), pm.token(), "{\"name\":\"\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
        call(post("/api/projects"), pm.token(),
                "{\"name\":\"X\",\"startDate\":\"2026-05-10\",\"dueDate\":\"2026-05-01\"}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void outsidersCannotSeeProject() throws Exception {
        TestUser pm = registerManager("Pm");
        TestUser outsider = registerUser("Out");
        long id = createProject(pm.token(), "Secret");

        call(get("/api/projects/" + id), outsider.token(), null).andExpect(status().isNotFound());
        call(get("/api/projects"), outsider.token(), null).andExpect(jsonPath("$", hasSize(0)));
        call(get("/api/projects/" + id), pm.token(), null).andExpect(status().isOk());
        call(get("/api/projects/" + id), adminToken(), null).andExpect(status().isOk());
    }

    @Test
    void onlyOwningManagerOrAdminCanManage() throws Exception {
        TestUser owner = registerManager("Owner");
        TestUser otherPm = registerManager("Other");
        TestUser member = registerUser("Mem");
        long id = createProject(owner.token(), "P");
        call(post("/api/projects/" + id + "/members"), owner.token(), "{\"email\":\"" + member.email() + "\"}")
                .andExpect(status().isCreated());

        String update = "{\"name\":\"Renamed\"}";
        call(put("/api/projects/" + id), member.token(), update).andExpect(status().isForbidden());
        call(put("/api/projects/" + id), otherPm.token(), update).andExpect(status().isNotFound());
        call(put("/api/projects/" + id), owner.token(), update)
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Renamed"));
        call(put("/api/projects/" + id), adminToken(), update).andExpect(status().isOk());

        call(delete("/api/projects/" + id), member.token(), null).andExpect(status().isForbidden());
        call(delete("/api/projects/" + id), otherPm.token(), null).andExpect(status().isNotFound());
        call(delete("/api/projects/" + id), owner.token(), null).andExpect(status().isNoContent());
        call(get("/api/projects/" + id), owner.token(), null).andExpect(status().isNotFound());
    }

    @Test
    void memberManagement() throws Exception {
        TestUser owner = registerManager("Owner");
        TestUser member = registerUser("Mem");
        long id = createProject(owner.token(), "P");
        String add = "{\"email\":\"" + member.email() + "\"}";

        call(post("/api/projects/" + id + "/members"), owner.token(), add).andExpect(status().isCreated());
        call(post("/api/projects/" + id + "/members"), owner.token(), add).andExpect(status().isConflict());
        call(post("/api/projects/" + id + "/members"), owner.token(), "{\"email\":\"nobody@x.com\"}")
                .andExpect(status().isNotFound());
        call(post("/api/projects/" + id + "/members"), member.token(), add).andExpect(status().isForbidden());
        call(get("/api/projects/" + id + "/members"), member.token(), null)
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));

        call(delete("/api/projects/" + id + "/members/" + owner.id()), owner.token(), null)
                .andExpect(status().isConflict());
        call(delete("/api/projects/" + id + "/members/" + member.id()), owner.token(), null)
                .andExpect(status().isNoContent());
        call(delete("/api/projects/" + id + "/members/" + member.id()), owner.token(), null)
                .andExpect(status().isNotFound());
    }
}
