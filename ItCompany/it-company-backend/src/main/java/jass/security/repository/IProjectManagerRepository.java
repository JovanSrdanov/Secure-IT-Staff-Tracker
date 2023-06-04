package jass.security.repository;

import jass.security.model.ProjectManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IProjectManagerRepository extends JpaRepository<ProjectManager, UUID> {
    @Query("select engStats.softwareEngineer.id " +
            "from  PrManagerProjectStats pmStats " +
            "left join SwEngineerProjectStats engStats " +
            "on pmStats.project.id = engStats.project.id " +
            "where pmStats.projectManager.id = :prManagerId " +
            "and engStats.softwareEngineer.id = :engineerId")
    List<UUID> getSubordinateIds(UUID prManagerId, UUID engineerId);
}
