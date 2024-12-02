package org.cresplanex.api.state.webgateway.proxy.query;

import build.buf.gen.cresplanex.nova.v1.SortOrder;
import build.buf.gen.storage.v1.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.cresplanex.api.state.common.constants.StorageServiceApplicationCode;
import org.cresplanex.api.state.webgateway.composition.CompositionUtils;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectDto;
import org.cresplanex.api.state.webgateway.exception.FileObjectNotFoundException;
import org.cresplanex.api.state.webgateway.mapper.CommonMapper;
import org.cresplanex.api.state.webgateway.mapper.FileObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileObjectQueryProxy {

    @GrpcClient("storageService")
    private StorageServiceGrpc.StorageServiceBlockingStub storageServiceBlockingStub;

    public FileObjectDto findFileObject(
            String operatorId,
            String fileObjectId
    ) {
        FindFileObjectResponse response;
        try {
            response = storageServiceBlockingStub.findFileObject(
                    FindFileObjectRequest.newBuilder()
                            .setOperatorId(operatorId)
                            .setFileObjectId(fileObjectId)
                            .build()
            );
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new FileObjectNotFoundException(
                        FileObjectNotFoundException.FindType.FILE_OBJECT_ID,
                        fileObjectId,
                        StorageServiceApplicationCode.FILE_OBJECT_NOT_FOUND
                );
            }
            throw e;
        }
        return FileObjectMapper.convert(response.getFileObject());
    }

    public ListResponseDto.InternalData<FileObjectDto> getFileObjects(
            String operatorId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount,
            boolean hasBucketFilter,
            List<String> filterBucketIds
    ) {
        GetFileObjectsResponse response = storageServiceBlockingStub.getFileObjects(
                GetFileObjectsRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setPagination(CompositionUtils.createPagination(limit, offset, cursor, pagination))
                        .setSort(createFileObjectSort(sortField, sortOrder))
                        .setWithCount(withCount)
                        .setFilterBucket(createFileObjectFilterBucket(hasBucketFilter, filterBucketIds))
                        .build()
        );
        return new ListResponseDto.InternalData<>(
                response.getFileObjectsList().stream()
                        .map(FileObjectMapper::convert)
                        .toList(),
                CommonMapper.countConvert(response.getCount())
        );
    }

    public ListResponseDto.InternalData<FileObjectDto> getPluralFileObjects(
            String operatorId,
            List<String> fileObjectIds,
            String sortField,
            String sortOrder
    ) {
        GetPluralFileObjectsResponse response = storageServiceBlockingStub.getPluralFileObjects(
                GetPluralFileObjectsRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .addAllFileObjectIds(fileObjectIds)
                        .setSort(createFileObjectSort(sortField, sortOrder))
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getFileObjectsList().stream()
                        .map(FileObjectMapper::convert)
                        .toList(),
                CommonMapper.invalidCountGenerate()
        );
    }

    public static FileObjectSort createFileObjectSort(
            String sortField,
            String sortOrder
    ) {
        FileObjectOrderField orderField = switch (sortField) {
            case "name" -> FileObjectOrderField.FILE_OBJECT_ORDER_FIELD_NAME;
            case "create" -> FileObjectOrderField.FILE_OBJECT_ORDER_FIELD_CREATE;
            default -> FileObjectOrderField.FILE_OBJECT_ORDER_FIELD_CREATE;
        };
        SortOrder order = switch (sortOrder) {
            case "asc" -> SortOrder.SORT_ORDER_ASC;
            case "desc" -> SortOrder.SORT_ORDER_DESC;
            default -> SortOrder.SORT_ORDER_ASC;
        };
        return FileObjectSort.newBuilder()
                .setOrderField(orderField)
                .setOrder(order)
                .build();
    }

    public static FileObjectFilterBucket createFileObjectFilterBucket(
            boolean hasBucketFilter,
            List<String> filterBucketIds
    ) {
        return FileObjectFilterBucket.newBuilder()
                .setHasValue(hasBucketFilter)
                .addAllBucketIds(filterBucketIds)
                .build();
    }
}
