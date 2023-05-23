package jass.security.service.interfaces;

import jass.security.dto.project.*;
import jass.security.exception.InvalidDateException;
import jass.security.exception.NotFoundException;
import jass.security.model.PrManagerProjectStats;
import jass.security.model.Project;

import java.util.List;
import java.util.UUID;

public interface IProjectService extends ICrudService<Project> {
      void AddSwEngineerToProject(AddSwEngineerToProjectDto dto, UUID projectId) throws NotFoundException;
      void AddPrManagerToProject(AddProjectMangerToProjectDto dto, UUID projectId) throws NotFoundException;
      void DismissSwEngineerFromProject(UUID swEngineerId , UUID projectId) throws NotFoundException;
      void DismissPrManagerFromProject(UUID prManagerId , UUID projectId) throws NotFoundException;
      List<SwEngineerProjectStatsDto> GetSwEngineersOnProject(UUID projectId);
      List<PrManagerProjectStatsDto> GetPrManagersOnProject(UUID projectId);
      List<PrManagerProjectStatsProjectDto> GetPrManagersProjects(UUID prMangerId);
      List<SwEngineerProjectStatsProjectDto> GetSwEngineersProjects(UUID swEngineerId);
      void ChangeSwEngineersJobDescription(UUID projectId, UUID swEngineerId, String newDescription) throws NotFoundException;
      Project update(UUID projectId, UpdateProjectDto dto) throws NotFoundException, InvalidDateException;
}
