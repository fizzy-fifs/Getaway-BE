package com.example.holidayplanner.interfaces;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ServiceInterface<T> {
    ResponseEntity create(T entity) throws JsonProcessingException;

    List<T> getAll();

    String update(String entityId, T newEntityInfo);

    String delete(String entityId);
}
