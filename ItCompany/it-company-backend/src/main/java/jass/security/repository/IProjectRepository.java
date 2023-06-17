package jass.security.repository;

import jass.security.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IProjectRepository extends JpaRepository<Project, UUID> { }
