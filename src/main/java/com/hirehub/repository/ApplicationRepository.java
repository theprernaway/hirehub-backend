package com.hirehub.repository;

import com.hirehub.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByApplicantId(Long applicantId);

    List<Application> findByJobId(Long jobId);

    boolean existsByJobIdAndApplicantId(Long jobId, Long applicantId);
}
