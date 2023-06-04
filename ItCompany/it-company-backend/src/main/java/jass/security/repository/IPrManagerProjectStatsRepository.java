package jass.security.repository;

import jass.security.dto.project.PrManagerProjectStatsDto;
import jass.security.dto.project.PrManagerProjectStatsProjectDto;
import jass.security.model.PrManagerProjectStats;
import jass.security.model.PrManagerProjectStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPrManagerProjectStatsRepository extends JpaRepository<PrManagerProjectStats, UUID> {
    Optional<PrManagerProjectStats> findById(PrManagerProjectStatsId id);
    @Query("select new jass.security.dto.project.PrManagerProjectStatsDto(manager, stats.workingPeriod) " +
            "from ProjectManager manager left join " +
            "PrManagerProjectStats stats " +
            "on manager.id = stats.id.prManagerId " +
            "where stats.id.projectId = :projectId")
    List<PrManagerProjectStatsDto> GetPrManagersOnProject(UUID projectId);
    @Query("select new jass.security.dto.project.PrManagerProjectStatsProjectDto(project,stats.workingPeriod) " +
            "from Project project left join PrManagerProjectStats stats " +
            "on project.id = stats.id.projectId " +
            "where stats.id.prManagerId = :prManagerId")
    List<PrManagerProjectStatsProjectDto> GetPrManagersProjects(UUID prManagerId);
}
