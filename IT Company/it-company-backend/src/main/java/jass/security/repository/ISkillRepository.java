package jass.security.repository;

import jass.security.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ISkillRepository extends JpaRepository<Skill, UUID> { }
