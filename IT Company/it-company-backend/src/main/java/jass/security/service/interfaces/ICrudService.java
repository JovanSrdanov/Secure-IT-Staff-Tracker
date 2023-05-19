package jass.security.service.interfaces;


import java.util.List;
import java.util.UUID;

public interface ICrudService<T> {
    List<T> findAll();
    T findById(UUID id);
    T save(T entity);
    void delete(UUID id);
}
