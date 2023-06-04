package jass.security.service.implementations;

import jass.security.dto.project.*;
import jass.security.exception.InvalidDateException;
import jass.security.exception.NotFoundException;
import jass.security.model.*;
import jass.security.repository.*;
import jass.security.service.interfaces.IProjectService;
import jass.security.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final IProjectManagerRepository _projectManagerRepository;
    private final ISwEngineerProjectStatsRepository _swEngineerProjectStatsRepository;
    private final IPrManagerProjectStatsRepository _prManagerProjectStatsRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    public ProjectService(IProjectRepository projectRepository, ISwEngineerRepository swEngineerRepository, IProjectManagerRepository projectManagerRepository, ISwEngineerProjectStatsRepository swEngineerProjectStatsRepository, IPrManagerProjectStatsRepository prManagerProjectStatsRepository) {
        _projectRepository = projectRepository;
        _swEngineerRepository = swEngineerRepository;
        _projectManagerRepository = projectManagerRepository;
        _swEngineerProjectStatsRepository = swEngineerProjectStatsRepository;
        _prManagerProjectStatsRepository = prManagerProjectStatsRepository;
    }

    @Override
    public List<Project> findAll() {
        return _projectRepository.findAll();
    }

    @Override
    public Project findById(UUID id) throws NotFoundException {
        var project = _projectRepository.findById(id);
        if (project.isEmpty()) {
            throw new NotFoundException("engineer not found");
        }
        return project.get();
    }

    @Override
    public Project save(Project entity) {
        entity.setId(UUID.randomUUID());
        logger.info("Project with an ID: " + entity.getId() + ", successfully created");

        return _projectRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {

    }

    public void AddSwEngineerToProject(AddSwEngineerToProjectDto dto, UUID projectId) throws NotFoundException {

        Optional<SoftwareEngineer> swEngineer = _swEngineerRepository.findById(dto.getSwEngineerId());
        if (swEngineer.isEmpty()) {
            throw new NotFoundException("Software engineer not found");
        }

        Optional<Project> project = _projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new NotFoundException("Project not found");
        }


        SwEngineerProjectStatsId statsId = new SwEngineerProjectStatsId(dto.getSwEngineerId(), projectId);
        DateRange workingPeriod = new DateRange(new Date(), null);
        SwEngineerProjectStats stats = new SwEngineerProjectStats(statsId, dto.getJobDescription(), workingPeriod, swEngineer.get(), project.get());
        _swEngineerProjectStatsRepository.save(stats);
    }

    @Override
    public void AddPrManagerToProject(AddProjectMangerToProjectDto dto, UUID projectId) throws NotFoundException {

        Optional<ProjectManager> projectManager = _projectManagerRepository.findById(dto.getPrManagerId());
        if (projectManager.isEmpty()) {
            throw new NotFoundException("Project manager not found");
        }

        Optional<Project> project = _projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new NotFoundException("Project not found");
        }


        PrManagerProjectStatsId statsId = new PrManagerProjectStatsId(dto.getPrManagerId(), projectId);
        DateRange workingPeriod = new DateRange(new Date(), null);
        PrManagerProjectStats stats = new PrManagerProjectStats(statsId, workingPeriod, projectManager.get(), project.get());
        _prManagerProjectStatsRepository.save(stats);
    }


    public void DismissSwEngineerFromProject(UUID swEngineerId, UUID projectId) throws NotFoundException {
        SwEngineerProjectStatsId statsId = new SwEngineerProjectStatsId(swEngineerId, projectId);
        var result = _swEngineerProjectStatsRepository.findById(statsId);
        if (result.isEmpty()) {
            throw new NotFoundException("Software engineer project stats not found");
        }

        var updatingStats = result.get();

        updatingStats.getWorkingPeriod().setEndDate(new Date());
        _swEngineerProjectStatsRepository.save(updatingStats);
    }

    @Override
    public void DismissPrManagerFromProject(UUID prManagerId, UUID projectId) throws NotFoundException {
        PrManagerProjectStatsId statsId = new PrManagerProjectStatsId(prManagerId, projectId);
        var result = _prManagerProjectStatsRepository.findById(statsId);
        if (result.isEmpty()) {
            throw new NotFoundException("Project manager project stats not found");
        }
        var updatingStats = result.get();
        updatingStats.getWorkingPeriod().setEndDate(new Date());
        _prManagerProjectStatsRepository.save(updatingStats);
    }

    public List<SwEngineerProjectStatsDto> GetSwEngineersOnProject(UUID projectId) {
        return _swEngineerProjectStatsRepository.GetSwEngineersOnProject(projectId);
    }

    @Override
    public List<PrManagerProjectStatsDto> GetPrManagersOnProject(UUID projectId) {
        return _prManagerProjectStatsRepository.GetPrManagersOnProject(projectId);
    }

    @Override
    public List<PrManagerProjectStatsProjectDto> GetPrManagersProjects(UUID prMangerId) {
        return _prManagerProjectStatsRepository.GetPrManagersProjects(prMangerId);
    }

    @Override
    public List<SwEngineerProjectStatsProjectDto> GetSwEngineersProjects(UUID swEngineerId) {
        return _swEngineerProjectStatsRepository.GetSwEngineersProjects(swEngineerId);
    }

    @Override
    public void ChangeSwEngineersJobDescription(UUID projectId, UUID swEngineerId, String newJobDescription) throws NotFoundException {
        SwEngineerProjectStatsId statsId = new SwEngineerProjectStatsId(swEngineerId, projectId);
        var stats = _swEngineerProjectStatsRepository.findById(statsId);

        if (stats.isEmpty()) {
            throw new NotFoundException("Software engineer project stats not found");
        }

        var newStats = stats.get();
        newStats.setJobDescription(newJobDescription);
        _swEngineerProjectStatsRepository.save(newStats);
    }

    @Override
    public Project update(UUID projectId, UpdateProjectDto dto) throws NotFoundException, InvalidDateException {
        var oldProject = findById(projectId);
        if (!DateUtils.isEndDateAfterStartDate(oldProject.getDuration().getStartDate(), dto.getEndDate())) {
            throw new InvalidDateException("Project end date must be after it's start date");
        }
        oldProject.update(dto);

        return _projectRepository.save(oldProject);
    }
}
