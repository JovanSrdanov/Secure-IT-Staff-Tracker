package jass.security.repository;

import jass.security.model.Cv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ICvRepository extends JpaRepository<Cv, UUID> { }
