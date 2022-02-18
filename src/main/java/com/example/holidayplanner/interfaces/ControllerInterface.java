package com.example.holidayplanner.interfaces;

import org.springframework.validation.Errors;

import java.util.List;

public interface ControllerInterface<T> {

    String create(T entity, Errors errors);

    List<T> getAll();

    String update(String id, T newInfo);

    String delete( String id);
}