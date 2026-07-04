package com.hirehub.controller;

import com.hirehub.dto.ApplicationResponse;
import com.hirehub.dto.ApplicationStatusUpdateRequest;
import com.hirehub.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // JobSeeker only: apply to a job
    @PostMapping("/{jobId}")
    @PreAuthorize("hasRole('JOBSEEKER')")
    public ResponseEntity<ApplicationResponse> applyToJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.applyToJob(jobId));
    }

    // JobSeeker only: withdraw own application
    @DeleteMapping("/{applicationId}")
    @PreAuthorize("hasRole('JOBSEEKER')")
    public ResponseEntity<Void> withdrawApplication(@PathVariable Long applicationId) {
        applicationService.withdrawApplication(applicationId);
        return ResponseEntity.noContent().build();
    }

    // JobSeeker only: view all of their own applications
    @GetMapping("/my")
    @PreAuthorize("hasRole('JOBSEEKER')")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications() {
        return ResponseEntity.ok(applicationService.getMyApplications());
    }

    // Employer only: view applicants for one of their jobs
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<List<ApplicationResponse>> getApplicantsForJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getApplicantsForJob(jobId));
    }

    // Employer only: update an applicant's status (SHORTLISTED / REJECTED / HIRED)
    @PutMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody ApplicationStatusUpdateRequest request) {
        return ResponseEntity.ok(applicationService.updateStatus(applicationId, request.getStatus()));
    }
}
