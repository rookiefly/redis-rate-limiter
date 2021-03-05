package com.rookiefly.ratelimiter.server.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RateLimiterForm {

    @NotBlank
    private String resourceName;
    @NotBlank
    private String appName;
    @NotNull
    private Integer maxPermits;
    @NotNull
    private Integer rate;
}
