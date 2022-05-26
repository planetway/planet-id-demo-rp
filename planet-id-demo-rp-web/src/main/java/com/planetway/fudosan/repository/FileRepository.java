package com.planetway.fudosan.repository;

import com.planetway.fudosan.domain.RpFile;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class FileRepository {
    private final Map<String, RpFile> files = new HashMap<>();

    public void save(RpFile rpFile) {
        files.put(rpFile.getFileId(), rpFile);
    }

    public Collection<RpFile> findAll() {
        return files.values();
    }

    public RpFile findById(String fileId) {
        return files.get(fileId);
    }
}
