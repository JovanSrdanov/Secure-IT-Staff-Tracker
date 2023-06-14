package pkibackend.pkibackend.service.interfaces;

import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.exceptions.NotFoundException;

import java.util.UUID;

public interface ICrudService<T> {
    Iterable<T> findAll();

    T findById(UUID id) throws NotFoundException;

    T save(T entity) throws BadRequestException;

    void deleteById(UUID id);
}
