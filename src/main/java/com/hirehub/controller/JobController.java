package com.hirehub.controller;

import com.hirehub.dto.JobRequest;
import com.hirehub.dto.JobResponse;
import com.hirehub.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // Public: anyone can browse open jobs
    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllOpenJobs() {
        return ResponseEntity.ok(jobService.getAllOpenJobs());
    }

    // Public: search by title or location, e.g. /api/jobs/search?keyword=backend
    @GetMapping("/search")
    public ResponseEntity<List<JobResponse>> searchJobs(@RequestParam String keyword) {
        return ResponseEntity.ok(jobService.searchJobs(keyword));
    }

    // Public: view one job's details
    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.getJobById(jobId));
    }

    // Employer only: post a new job
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.createJob(request));
    }

    // Employer only: edit own job (ownership checked in service layer)
    @PutMapping("/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long jobId, @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.updateJob(jobId, request));
    }

    // Employer only: delete own job
    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        jobService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }

    // Employer only: close a job manually
    @PatchMapping("/{jobId}/close")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponse> closeJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.closeJob(jobId));
    }

    // Employer only: see all jobs they've posted
    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<List<JobResponse>> getMyJobs() {
        return ResponseEntity.ok(jobService.getMyJobs());
    }
}
