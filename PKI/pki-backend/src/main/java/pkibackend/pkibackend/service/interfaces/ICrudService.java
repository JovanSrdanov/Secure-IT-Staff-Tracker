package pkibackend.pkibackend.service.interfaces;

import pkibackend.pkibackend.exceptions.BadRequestException;

import java.util.UUID;

public interface ICrudService<T> {
    Iterable<T> findAll();

    T findById(UUID id);

    T save(T entity) throws BadRequestException;

    void deleteById(UUID id);
}
