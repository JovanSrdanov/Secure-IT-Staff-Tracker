package jass.security.repository;


import jass.security.dto.project.SwEngineerProjectStatsDto;
import jass.security.dto.project.SwEngineerProjectStatsProjectDto;
import jass.security.model.SoftwareEngineer;
import jass.security.model.SwEngineerProjectStats;
import jass.security.model.SwEngineerProjectStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISwEngineerProjectStatsRepository extends JpaRepository<SwEngineerProjectStats, UUID> {
    Optional<SwEngineerProjectStats> findById(SwEngineerProjectStatsId id);

    @Query("select new jass.security.dto.project.SwEngineerProjectStatsDto(engineer, stats.jobDescription, stats.workingPeriod) " +
            "from SoftwareEngineer engineer left join " +
            "SwEngineerProjectStats stats " +
            "on engineer.id = stats.id.swEngineerId " +
            "where stats.id.projectId = :projectId")
    List<SwEngineerProjectStatsDto> GetSwEngineersOnProject(UUID projectId);
    @Query("select new jass.security.dto.project.SwEngineerProjectStatsProjectDto(project, stats.jobDescription, stats.workingPeriod) " +
            "from Project project left join SwEngineerProjectStats stats " +
            "on project.id = stats.id.projectId " +
            "where stats.id.swEngineerId = :swEngineerId")
    List<SwEngineerProjectStatsProjectDto> GetSwEngineersProjects(UUID swEngineerId);
}
