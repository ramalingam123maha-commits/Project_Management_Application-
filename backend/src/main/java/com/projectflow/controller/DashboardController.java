package com.projectflow.controller;

import com.projectflow.dto.dashboard.DashboardResponse;
import com.projectflow.security.UserPrincipal;
import com.projectflow.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /** Returns statistics scoped to the caller's role (Admin, Project Manager or Member). */
    @GetMapping
    public ResponseEntity<DashboardResponse> get(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(dashboardService.getDashboard(principal));
    }
}
