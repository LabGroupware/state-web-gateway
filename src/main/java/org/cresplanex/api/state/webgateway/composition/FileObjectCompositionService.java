package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.composition.attach.AttachRelationFileObject;
import org.cresplanex.api.state.webgateway.composition.helper.FileObjectCompositionHelper;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectDto;
import org.cresplanex.api.state.webgateway.hasher.FileObjectHasher;
import org.cresplanex.api.state.webgateway.proxy.query.FileObjectQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.FileObjectRetriever;
import org.cresplanex.api.state.webgateway.retriever.resolver.FIleObjectRetrieveResolver;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FileObjectCompositionService {

    private final FileObjectQueryProxy fileObjectQueryProxy;
    private final AttachRelationFileObject attachRelationFileObject;

    public FileObjectDto findFileObject(String operatorId, String fileObjectId, List<String> with) {
        FileObjectDto fileObject;
        FileObjectRetriever fileObjectRetriever = FIleObjectRetrieveResolver.resolve(
                with != null ? with.toArray(new String[0]) : new String[0]
        );
        int need = FileObjectCompositionHelper.calculateNeedQuery(List.of(fileObjectRetriever));
        RetrievedCacheContainer cache = new RetrievedCacheContainer();
        switch (need) {
            default:
                fileObject = fileObjectQueryProxy.findFileObject(
                        operatorId,
                        fileObjectId
                );
                cache.getCache().put(FileObjectHasher.hashFileObject(fileObjectId), fileObject.deepClone());
                break;
        }
        attachRelationFileObject.attach(
                operatorId,
                cache,
                fileObjectRetriever,
                fileObject
        );

        return fileObject;
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
            List<String> filterBucketIds,
            List<String> with
    ) {
        ListResponseDto.InternalData<FileObjectDto> fileObjects;
        FileObjectRetriever fileObjectRetriever = FIleObjectRetrieveResolver.resolve(
                with != null ? with.toArray(new String[0]) : new String[0]
        );
        int need = FileObjectCompositionHelper.calculateNeedQuery(List.of(fileObjectRetriever));
        RetrievedCacheContainer cache = new RetrievedCacheContainer();
        switch (need) {
            default:
                fileObjects = fileObjectQueryProxy.getFileObjects(
                        operatorId,
                        limit,
                        offset,
                        cursor,
                        pagination,
                        sortField,
                        sortOrder,
                        withCount,
                        hasBucketFilter,
                        filterBucketIds
                );
                for (FileObjectDto dto : fileObjects.getListData()) {
                    cache.getCache().put(FileObjectHasher.hashFileObject(dto.getFileObjectId()), dto.deepClone());
                }
                break;
        }
        attachRelationFileObject.attach(
                operatorId,
                cache,
                fileObjectRetriever,
                fileObjects.getListData()
        );

        return fileObjects;
    }
}
