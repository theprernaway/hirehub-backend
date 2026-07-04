package com.hirehub.repository;

import com.hirehub.model.Job;
import com.hirehub.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatus(JobStatus status);

    List<Job> findByPostedById(Long employerId);

    // Simple search by title OR location, case-insensitive, only among OPEN jobs
    @Query("SELECT j FROM Job j WHERE j.status = com.hirehub.model.JobStatus.OPEN " +
           "AND (LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Job> searchOpenJobs(@Param("keyword") String keyword);
}
