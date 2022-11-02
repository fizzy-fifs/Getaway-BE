package com.example.holidayplanner.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

import java.util.List;

public interface ControllerInterface<T> {

    ResponseEntity create(T entity, Errors errors) throws JsonProcessingException;

    List<T> getAll();

    String update(String id, T newInfo);

    String delete( String id);
}
