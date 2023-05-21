package jass.security.service.interfaces;


import jass.security.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

public interface ICrudService<T> {
    List<T> findAll();
    T findById(UUID id) throws NotFoundException;
    T save(T entity);
    void delete(UUID id);
}
