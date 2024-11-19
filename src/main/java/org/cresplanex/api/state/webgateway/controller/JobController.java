package org.cresplanex.api.state.webgateway.controller;

import build.buf.gen.job.v1.Job;
import lombok.AllArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.job.JobResponseDto;
import org.cresplanex.api.state.webgateway.mapper.JobMapper;
import org.cresplanex.api.state.webgateway.proxy.query.JobQueryServiceProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
@AllArgsConstructor
public class JobController {

    private final JobQueryServiceProxy jobQueryServiceProxy;

    @RequestMapping(value = "/{jobId}", method = RequestMethod.GET)
    public ResponseEntity<JobResponseDto> findJob(
                @PathVariable String jobId
    ) {
        Job job = jobQueryServiceProxy.findJob(jobId);
        return ResponseEntity.ok(JobMapper.convert(job));
    }
}
