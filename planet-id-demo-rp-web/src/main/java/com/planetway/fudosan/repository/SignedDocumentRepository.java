package com.planetway.fudosan.repository;

import com.planetway.fudosan.domain.SignedDocumentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SignedDocumentRepository extends CrudRepository<SignedDocumentEntity, Long> {
    List<SignedDocumentEntity> getAllByUserId(Long userId);
    SignedDocumentEntity findByUserIdAndUuid(Long userId, String uuid);
}
