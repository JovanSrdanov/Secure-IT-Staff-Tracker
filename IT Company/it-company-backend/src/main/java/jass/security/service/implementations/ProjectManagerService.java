package jass.security.service.implementations;

import jass.security.model.ProjectManager;
import jass.security.repository.IProjectManagerRepository;
import jass.security.service.interfaces.IProjectManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Primary
public class ProjectManagerService implements IProjectManagerService {

    private final IProjectManagerRepository projectManagerRepository;

    @Autowired
    public ProjectManagerService(IProjectManagerRepository projectManagerRepository) {
        this.projectManagerRepository = projectManagerRepository;
    }

    @Override
    public List<ProjectManager> findAll() {
        return null;
    }

    @Override
    public ProjectManager findById(UUID id) {
        return null;
    }

    @Override
    public ProjectManager save(ProjectManager entity) {
        return projectManagerRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        projectManagerRepository.deleteById(id);
    }
}
