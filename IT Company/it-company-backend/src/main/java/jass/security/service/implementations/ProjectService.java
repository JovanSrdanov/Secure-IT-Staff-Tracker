package jass.security.service.implementations;

import jass.security.model.Project;
import jass.security.repository.IProjectRepository;
import jass.security.service.interfaces.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Primary
public class ProjectService implements IProjectService {

    @Autowired
    public ProjectService(IProjectRepository _projectRepository) {
        this._projectRepository = _projectRepository;
    }

    private final IProjectRepository _projectRepository;
    @Override
    public List<Project> findAll() {
        return _projectRepository.findAll();
    }

    @Override
    public Project findById(UUID id) {
        return null;
    }

    @Override
    public Project save(Project entity) {
        entity.setId(UUID.randomUUID());
        return _projectRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {

    }
}
