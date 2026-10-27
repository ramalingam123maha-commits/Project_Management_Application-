package com.projectflow.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "project_members")
public class ProjectMember {

    @EmbeddedId
    private ProjectMemberId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    public ProjectMember() {
    }

    public ProjectMember(Project project, User user) {
        this.project = project;
        this.user = user;
        this.id = new ProjectMemberId(project != null ? project.getId() : null, user != null ? user.getId() : null);
    }

    @PrePersist
    protected void onCreate() {
        if (this.joinedAt == null) {
            this.joinedAt = Instant.now();
        }
        if (this.id == null && project != null && user != null) {
            this.id = new ProjectMemberId(project.getId(), user.getId());
        }
    }

    public ProjectMemberId getId() {
        return id;
    }

    public void setId(ProjectMemberId id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
        if (this.id != null && project != null) {
            this.id.setProjectId(project.getId());
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (this.id != null && user != null) {
            this.id.setUserId(user.getId());
        }
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectMember that = (ProjectMember) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Embeddable
    public static class ProjectMemberId implements Serializable {

        @Column(name = "project_id")
        private Long projectId;

        @Column(name = "user_id")
        private Long userId;

        public ProjectMemberId() {
        }

        public ProjectMemberId(Long projectId, Long userId) {
            this.projectId = projectId;
            this.userId = userId;
        }

        public Long getProjectId() {
            return projectId;
        }

        public void setProjectId(Long projectId) {
            this.projectId = projectId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProjectMemberId that = (ProjectMemberId) o;
            return Objects.equals(projectId, that.projectId) && Objects.equals(userId, that.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(projectId, userId);
        }
    }
}
