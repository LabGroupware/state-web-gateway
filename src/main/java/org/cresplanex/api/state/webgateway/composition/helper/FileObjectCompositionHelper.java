package org.cresplanex.api.state.webgateway.composition.helper;

import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectDto;
import org.cresplanex.api.state.webgateway.hasher.FileObjectHasher;
import org.cresplanex.api.state.webgateway.proxy.query.FileObjectQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.FileObjectRetriever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileObjectCompositionHelper {

    public static int calculateNeedQuery(List<FileObjectRetriever> retrievers) {
        int needQuery = 0;
//        for (FileObjectRetriever retriever : retrievers) {
//        }
        return needQuery;
    }

    public static Map<String, FileObjectDto> createFileObjectDtoMap(
            FileObjectQueryProxy fileObjectQueryProxy,
            RetrievedCacheContainer cache,
            String operatorId,
            List<String> fileObjectIds,
            List<FileObjectRetriever> retrievers
    ) {
        int need = calculateNeedQuery(retrievers);
        List<String> needRetrieveAttachedFileObjectIds = new ArrayList<>();
        Map<String, FileObjectDto> fileObjectDtoMap = new HashMap<>();
        List<FileObjectDto> fileObject;

        switch (need) {
            default:
                for (String fileObjectId : fileObjectIds) {
                    if (cache.getCache().containsKey(FileObjectHasher.hashFileObject(fileObjectId))) {
                        fileObjectDtoMap.put(fileObjectId, ((FileObjectDto) cache.getCache().get(FileObjectHasher.hashFileObject(fileObjectId))).deepClone());
                        break;
                    } else {
                        needRetrieveAttachedFileObjectIds.add(fileObjectId);
                    }
                }

                if (!needRetrieveAttachedFileObjectIds.isEmpty()) {
                    fileObject = fileObjectQueryProxy.getPluralFileObjects(
                            operatorId,
                            needRetrieveAttachedFileObjectIds,
                            null,
                            null
                    ).getListData();
                    for (FileObjectDto dto : fileObject) {
                        fileObjectDtoMap.put(dto.getFileObjectId(), dto);
                        cache.getCache().put(FileObjectHasher.hashFileObject(dto.getFileObjectId()), dto.deepClone());
                    }
                }
                break;
        }

        return fileObjectDtoMap;
    }
}
