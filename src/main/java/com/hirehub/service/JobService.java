package com.hirehub.service;

import com.hirehub.dto.JobRequest;
import com.hirehub.dto.JobResponse;
import com.hirehub.exception.ResourceNotFoundException;
import com.hirehub.exception.UnauthorizedActionException;
import com.hirehub.model.Job;
import com.hirehub.model.JobStatus;
import com.hirehub.model.User;
import com.hirehub.repository.JobRepository;
import com.hirehub.security.CurrentUserProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final CurrentUserProvider currentUserProvider;

    public JobService(JobRepository jobRepository, CurrentUserProvider currentUserProvider) {
        this.jobRepository = jobRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public JobResponse createJob(JobRequest request) {
        User employer = currentUserProvider.getCurrentUser();

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setStatus(JobStatus.OPEN);
        job.setPostedBy(employer);

        return JobResponse.fromEntity(jobRepository.save(job));
    }

    public JobResponse updateJob(Long jobId, JobRequest request) {
        Job job = getJobOrThrow(jobId);
        checkOwnership(job);

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());

        return JobResponse.fromEntity(jobRepository.save(job));
    }

    public void deleteJob(Long jobId) {
        Job job = getJobOrThrow(jobId);
        checkOwnership(job);
        jobRepository.delete(job);
    }

    public List<JobResponse> getAllOpenJobs() {
        return jobRepository.findByStatus(JobStatus.OPEN)
                .stream().map(JobResponse::fromEntity).toList();
    }

    public List<JobResponse> searchJobs(String keyword) {
        return jobRepository.searchOpenJobs(keyword)
                .stream().map(JobResponse::fromEntity).toList();
    }

    public JobResponse getJobById(Long jobId) {
        return JobResponse.fromEntity(getJobOrThrow(jobId));
    }

    public List<JobResponse> getMyJobs() {
        User employer = currentUserProvider.getCurrentUser();
        return jobRepository.findByPostedById(employer.getId())
                .stream().map(JobResponse::fromEntity).toList();
    }

    // Employer closes a job manually, e.g. once a hire is made
    public JobResponse closeJob(Long jobId) {
        Job job = getJobOrThrow(jobId);
        checkOwnership(job);
        job.setStatus(JobStatus.CLOSED);
        return JobResponse.fromEntity(jobRepository.save(job));
    }

    private Job getJobOrThrow(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
    }

    // Ownership check: this is the part @PreAuthorize alone can't do,
    // since it only checks role, not "is this YOUR job"
    private void checkOwnership(Job job) {
        User currentUser = currentUserProvider.getCurrentUser();
        if (!job.getPostedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You do not have permission to modify this job");
        }
    }
}
