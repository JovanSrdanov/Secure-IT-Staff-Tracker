package jass.security.service.implementations;

import jass.security.dto.project.AddSwEngineerToProjectDto;
import jass.security.dto.project.SwEngineerProjectStatsDto;
import jass.security.exception.NotFoundException;
import jass.security.model.*;
import jass.security.repository.IProjectRepository;
import jass.security.repository.ISwEngineerProjectStatsRepository;
import jass.security.repository.ISwEngineerRepository;
import jass.security.service.interfaces.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
public class ProjectService implements IProjectService {

    private final IProjectRepository _projectRepository;
    private final ISwEngineerRepository _swEngineerRepository;
    private final ISwEngineerProjectStatsRepository _swEngineerProjectStatsRepository;
    @Autowired
    public ProjectService(IProjectRepository projectRepository, ISwEngineerRepository swEngineerRepository, ISwEngineerProjectStatsRepository swEngineerProjectStatsRepository) {
        _projectRepository = projectRepository;
        _swEngineerRepository = swEngineerRepository;
        _swEngineerProjectStatsRepository = swEngineerProjectStatsRepository;
    }

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

  public void AddSwEngineerToProject(AddSwEngineerToProjectDto dto, UUID projectId) throws NotFoundException {

      Optional<SoftwareEngineer> swEngineer = _swEngineerRepository.findById(dto.getSwEngineerId());
      if(swEngineer.isEmpty()){
          throw new NotFoundException("Software engineer not found");
      }

      Optional<Project> project = _projectRepository.findById(projectId);
      if(project.isEmpty()){
          throw new NotFoundException("Project not found");
      }


      SwEngineerProjectStatsId statsId = new SwEngineerProjectStatsId(dto.getSwEngineerId(), projectId);
      DateRange workingPeriod = new DateRange(new Date(), null);
      SwEngineerProjectStats stats = new SwEngineerProjectStats(statsId, dto.getJobDescription(), workingPeriod, swEngineer.get(), project.get());
      _swEngineerProjectStatsRepository.save(stats);
  }


    public void DismissSwEngineerFromProject(UUID swEngineerId , UUID projectId) throws NotFoundException{
        SwEngineerProjectStatsId statsId = new SwEngineerProjectStatsId(swEngineerId,projectId);
        var result = _swEngineerProjectStatsRepository.findById(statsId);
        if(result.isEmpty()){
            throw  new NotFoundException("Software engineer project stats not found");
        }

        var updatingStats = result.get();

        updatingStats.getWorkingPeriod().setEndDate(new Date());
        _swEngineerProjectStatsRepository.save(updatingStats);
    }

    public List<SwEngineerProjectStatsDto> GetSwEngineersOnProject(UUID projectId){
        return _swEngineerProjectStatsRepository.GetSwEngineersOnProject(projectId);
    }
}
