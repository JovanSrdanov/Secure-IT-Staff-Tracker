package jass.security.service.interfaces;

import jass.security.dto.project.AddSwEngineerToProjectDto;
import jass.security.dto.project.SwEngineerProjectStatsDto;
import jass.security.exception.NotFoundException;
import jass.security.model.Project;

import java.util.List;
import java.util.UUID;

public interface IProjectService extends ICrudService<Project> {
      void AddSwEngineerToProject(AddSwEngineerToProjectDto dto, UUID projectId) throws NotFoundException;
      void DismissSwEngineerFromProject(UUID swEngineerId , UUID projectId) throws NotFoundException;
      List<SwEngineerProjectStatsDto> GetSwEngineersOnProject(UUID projectId);
}
