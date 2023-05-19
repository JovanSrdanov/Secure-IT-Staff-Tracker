package jass.security.service.implementations;

import jass.security.model.SoftwareEngineer;
import jass.security.repository.ISoftwareEngineerRepository;
import jass.security.service.interfaces.ISoftwareEngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Primary
public class SoftwareEngineerService implements ISoftwareEngineerService {

    private final ISoftwareEngineerRepository softwareEngineerRepository;

    @Autowired
    public SoftwareEngineerService(ISoftwareEngineerRepository softwareEngineerRepository) {
        this.softwareEngineerRepository = softwareEngineerRepository;
    }

    @Override
    public List<SoftwareEngineer> findAll() {
        return null;
    }

    @Override
    public SoftwareEngineer findById(UUID id) {
        return null;
    }

    @Override
    public SoftwareEngineer save(SoftwareEngineer entity) {
        return softwareEngineerRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        softwareEngineerRepository.deleteById(id);
    }
}
