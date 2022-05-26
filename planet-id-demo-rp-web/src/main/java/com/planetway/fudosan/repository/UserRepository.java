package com.planetway.fudosan.repository;

import com.planetway.fudosan.domain.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity getByEmail(String email);
    UserEntity findByPlanetId(String planetId);
}
