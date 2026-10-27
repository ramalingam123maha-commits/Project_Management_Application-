package com.projectflow.controller;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerIntegrationTest extends IntegrationTestSupport {

    private long projectId;
    private TestUser pm;
    private TestUser member;
    private TestUser outsider;

    private void setUpProject() throws Exception {
        pm = registerManager("Pm");
        member = registerUser("Mem");
        outsider = registerUser("Out");
        projectId = createProject(pm.token(), "Alpha");
        call(post("/api/projects/" + projectId + "/members"), pm.token(), "{\"email\":\"" + member.email() + "\"}")
                .andExpect(status().isCreated());
    }

    @Test
    void onlyOwningManagerCreatesTasks() throws Exception {
        setUpProject();
        String body = "{\"title\":\"  Write docs  \",\"priority\":\"HIGH\"}";

        call(post("/api/projects/" + projectId + "/tasks"), member.token(), body).andExpect(status().isForbidden());
        call(post("/api/projects/" + projectId + "/tasks"), outsider.token(), body).andExpect(status().isNotFound());
        call(post("/api/projects/" + projectId + "/tasks"), pm.token(), body)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Write docs"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void validationAndAssigneeRules() throws Exception {
        setUpProject();
        String url = "/api/projects/" + projectId + "/tasks";
        call(post(url), pm.token(), "{\"title\":\"\"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.title").exists());
        call(post(url), pm.token(), "{\"title\":\"T\",\"status\":\"BOGUS\"}").andExpect(status().isBadRequest());
        call(post(url), pm.token(), "{\"title\":\"T\",\"assigneeId\":" + outsider.id() + "}")
                .andExpect(status().isConflict());
        call(post(url), pm.token(), "{\"title\":\"T\",\"assigneeId\":999999}").andExpect(status().isNotFound());
        call(post(url), pm.token(), "{\"title\":\"T\",\"assigneeId\":" + member.id() + "}")
                .andExpect(status().isCreated()).andExpect(jsonPath("$.assignee.id").value(member.id()));
    }

    @Test
    void statusChangeRules() throws Exception {
        setUpProject();
        long assigned = createTask(pm.token(), projectId, "Mine", member.id());
        long unassigned = createTask(pm.token(), projectId, "Nobody", null);
        String inProgress = "{\"status\":\"IN_PROGRESS\"}";

        call(patch("/api/tasks/" + assigned + "/status"), member.token(), inProgress)
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        call(patch("/api/tasks/" + unassigned + "/status"), member.token(), inProgress).andExpect(status().isForbidden());
        call(patch("/api/tasks/" + unassigned + "/status"), pm.token(), "{\"status\":\"DONE\"}")
                .andExpect(status().isOk());
        call(patch("/api/tasks/" + assigned + "/status"), pm.token(), "{\"status\":\"NOPE\"}")
                .andExpect(status().isBadRequest());
        call(patch("/api/tasks/" + assigned + "/status"), outsider.token(), inProgress).andExpect(status().isNotFound());
    }

    @Test
    void editAssignAndDeletePermissions() throws Exception {
        setUpProject();
        long id = createTask(pm.token(), projectId, "T", null);

        call(put("/api/tasks/" + id), member.token(), "{\"title\":\"X\"}").andExpect(status().isForbidden());
        call(patch("/api/tasks/" + id + "/assignee?assigneeId=" + member.id()), member.token(), null)
                .andExpect(status().isForbidden());
        call(patch("/api/tasks/" + id + "/assignee?assigneeId=" + member.id()), pm.token(), null)
                .andExpect(status().isOk()).andExpect(jsonPath("$.assignee.id").value(member.id()));
        call(patch("/api/tasks/" + id + "/assignee"), pm.token(), null)
                .andExpect(status().isOk()).andExpect(jsonPath("$.assignee").doesNotExist());
        call(put("/api/tasks/" + id), pm.token(), "{\"title\":\"Renamed\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Renamed"));

        call(delete("/api/tasks/" + id), member.token(), null).andExpect(status().isForbidden());
        call(delete("/api/tasks/" + id), pm.token(), null).andExpect(status().isNoContent());
        call(get("/api/tasks/" + id), pm.token(), null).andExpect(status().isNotFound());
    }

    @Test
    void visibilityAndMyTasks() throws Exception {
        setUpProject();
        long id = createTask(pm.token(), projectId, "Mine", member.id());

        call(get("/api/tasks/" + id), outsider.token(), null).andExpect(status().isNotFound());
        call(get("/api/projects/" + projectId + "/tasks"), outsider.token(), null).andExpect(status().isNotFound());
        call(get("/api/projects/" + projectId + "/tasks"), member.token(), null)
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        call(get("/api/projects/" + projectId + "/tasks?status=DONE"), member.token(), null)
                .andExpect(jsonPath("$", hasSize(0)));
        call(get("/api/tasks/my"), member.token(), null)
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        call(get("/api/tasks/my"), outsider.token(), null).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void commentsFlow() throws Exception {
        setUpProject();
        long id = createTask(pm.token(), projectId, "T", null);
        String url = "/api/tasks/" + id + "/comments";

        call(post(url), member.token(), "{\"content\":\"hello\"}")
                .andExpect(status().isCreated()).andExpect(jsonPath("$.content").value("hello"));
        call(post(url), member.token(), "{\"content\":\"  \"}").andExpect(status().isBadRequest());
        call(post(url), outsider.token(), "{\"content\":\"hi\"}").andExpect(status().isNotFound());
        call(get(url), pm.token(), null).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));

        long commentId = json(call(get(url), pm.token(), null)).get(0).get("id").asLong();
        TestUser other = registerUser("Other");
        call(post("/api/projects/" + projectId + "/members"), pm.token(), "{\"email\":\"" + other.email() + "\"}");
        call(delete(url + "/" + commentId), other.token(), null).andExpect(status().isForbidden());
        call(delete(url + "/" + commentId), member.token(), null).andExpect(status().isNoContent());
    }
}
