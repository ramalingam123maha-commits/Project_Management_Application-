package com.projectflow.dto.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectflow.domain.ProjectStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProjectRequest(
        @NotBlank(message = "Project name is required")
        @Size(max = 150, message = "Project name must be at most 150 characters")
        String name,

        @Size(max = 5000, message = "Description must be at most 5000 characters")
        String description,

        ProjectStatus status,

        LocalDate startDate,

        LocalDate dueDate
) {
    @JsonIgnore
    @AssertTrue(message = "Due date must not be before start date")
    public boolean isDateRangeValid() {
        return startDate == null || dueDate == null || !dueDate.isBefore(startDate);
    }
}
