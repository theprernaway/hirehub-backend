package com.hirehub.dto;

import com.hirehub.model.Application;
import com.hirehub.model.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String applicantName;
    private ApplicationStatus status;
    private LocalDateTime appliedDate;

    public static ApplicationResponse fromEntity(Application application) {
        ApplicationResponse res = new ApplicationResponse();
        res.setId(application.getId());
        res.setJobId(application.getJob().getId());
        res.setJobTitle(application.getJob().getTitle());
        res.setApplicantName(application.getApplicant().getName());
        res.setStatus(application.getStatus());
        res.setAppliedDate(application.getAppliedDate());
        return res;
    }
}
