package org.cresplanex.api.state.webgateway.proxy;

import build.buf.gen.job.v1.FindJobRequest;
import build.buf.gen.job.v1.FindJobResponse;
import build.buf.gen.job.v1.Job;
import build.buf.gen.job.v1.JobServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class JobServiceProxy {

    @GrpcClient("jobService")
    private JobServiceGrpc.JobServiceBlockingStub jobServiceBlockingStub;

    public Job findJob(String jobId) {
        FindJobRequest request = FindJobRequest.newBuilder().setJobId(jobId).build();
        FindJobResponse response = this.jobServiceBlockingStub.findJob(request);

        return response.getJob();
    }
}
