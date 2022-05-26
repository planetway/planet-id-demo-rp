package com.planetway.fudosan.repository;

import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Repository
public class UsersFilesRepository {

    private MultiValueMap<String, String> database = new LinkedMultiValueMap<>();

    public void add(String key, String value) {
        database.add(key, value);
    }

    public List<String> get(String key) {
        return database.get(key);
    }

    public MultiValueMap<String, String> database() {
        return database;
    }

}
