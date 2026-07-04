package com.hirehub.service;

import com.hirehub.dto.ApplicationResponse;
import com.hirehub.exception.DuplicateResourceException;
import com.hirehub.exception.ResourceNotFoundException;
import com.hirehub.exception.UnauthorizedActionException;
import com.hirehub.model.Application;
import com.hirehub.model.ApplicationStatus;
import com.hirehub.model.Job;
import com.hirehub.model.JobStatus;
import com.hirehub.model.User;
import com.hirehub.repository.ApplicationRepository;
import com.hirehub.repository.JobRepository;
import com.hirehub.security.CurrentUserProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final CurrentUserProvider currentUserProvider;

    public ApplicationService(ApplicationRepository applicationRepository, JobRepository jobRepository,
                               CurrentUserProvider currentUserProvider) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public ApplicationResponse applyToJob(Long jobId) {
        User jobSeeker = currentUserProvider.getCurrentUser();

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (job.getStatus() == JobStatus.CLOSED) {
            throw new UnauthorizedActionException("This job is closed and no longer accepting applications");
        }

        if (applicationRepository.existsByJobIdAndApplicantId(jobId, jobSeeker.getId())) {
            throw new DuplicateResourceException("You have already applied to this job");
        }

        Application application = new Application();
        application.setJob(job);
        application.setApplicant(jobSeeker);
        application.setStatus(ApplicationStatus.APPLIED);

        return ApplicationResponse.fromEntity(applicationRepository.save(application));
    }

    public void withdrawApplication(Long applicationId) {
        Application application = getApplicationOrThrow(applicationId);
        User currentUser = currentUserProvider.getCurrentUser();

        if (!application.getApplicant().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You can only withdraw your own applications");
        }

        applicationRepository.delete(application);
    }

    public List<ApplicationResponse> getMyApplications() {
        User jobSeeker = currentUserProvider.getCurrentUser();
        return applicationRepository.findByApplicantId(jobSeeker.getId())
                .stream().map(ApplicationResponse::fromEntity).toList();
    }

    // Employer views everyone who applied to one of their jobs
    public List<ApplicationResponse> getApplicantsForJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        User currentUser = currentUserProvider.getCurrentUser();
        if (!job.getPostedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You do not have permission to view these applicants");
        }

        return applicationRepository.findByJobId(jobId)
                .stream().map(ApplicationResponse::fromEntity).toList();
    }

    public ApplicationResponse updateStatus(Long applicationId, ApplicationStatus newStatus) {
        Application application = getApplicationOrThrow(applicationId);
        User currentUser = currentUserProvider.getCurrentUser();

        // Only the employer who posted the job can change the status of applications to it
        if (!application.getJob().getPostedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You do not have permission to update this application");
        }

        application.setStatus(newStatus);
        Application saved = applicationRepository.save(application);

        // If the employer just hired someone, auto-close the job
        if (newStatus == ApplicationStatus.HIRED) {
            Job job = application.getJob();
            job.setStatus(JobStatus.CLOSED);
            jobRepository.save(job);
        }

        return ApplicationResponse.fromEntity(saved);
    }

    private Application getApplicationOrThrow(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
    }
}
