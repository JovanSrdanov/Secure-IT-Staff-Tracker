package jass.security.repository;

import jass.security.model.ProjectManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IProjectManagerRepository extends JpaRepository<ProjectManager, UUID> {
}
