package org.cresplanex.api.state.webgateway.proxy.command;

import build.buf.gen.plan.v1.CreateTaskRequest;
import build.buf.gen.storage.v1.CreateFileObjectRequest;
import build.buf.gen.storage.v1.FileObject;
import build.buf.gen.storage.v1.StorageServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class StorageCommandServiceProxy {

    @GrpcClient("storageService")
    private StorageServiceGrpc.StorageServiceBlockingStub storageServiceBlockingStub;


    public String createFileObject(String operationId, FileObject fileObject) {
        return this.storageServiceBlockingStub.createFileObject(
                CreateFileObjectRequest.newBuilder()
                        .setOperatorId(operationId)
                        .setBucketId(fileObject.getBucketId())
                        .setName(fileObject.getName())
                        .setPath(fileObject.getPath())
                        .build()
        ).getJobId();
    }
}
