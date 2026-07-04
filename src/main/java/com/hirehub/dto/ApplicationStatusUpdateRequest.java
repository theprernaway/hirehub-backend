package com.hirehub.dto;

import com.hirehub.model.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private ApplicationStatus status; // SHORTLISTED, REJECTED, or HIRED
}
