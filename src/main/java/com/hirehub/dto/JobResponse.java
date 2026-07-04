package com.hirehub.dto;

import com.hirehub.model.Job;
import com.hirehub.model.JobStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private Double salary;
    private JobStatus status;
    private String postedByName;
    private LocalDateTime createdAt;

    // Converts an entity into a response DTO so we never expose the entity directly
    public static JobResponse fromEntity(Job job) {
        JobResponse res = new JobResponse();
        res.setId(job.getId());
        res.setTitle(job.getTitle());
        res.setDescription(job.getDescription());
        res.setLocation(job.getLocation());
        res.setSalary(job.getSalary());
        res.setStatus(job.getStatus());
        res.setPostedByName(job.getPostedBy().getName());
        res.setCreatedAt(job.getCreatedAt());
        return res;
    }
}
