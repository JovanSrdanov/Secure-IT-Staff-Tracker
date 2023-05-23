package jass.security.repository;

import jass.security.dto.swengineer.SkillDto;
import jass.security.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ISkillRepository extends JpaRepository<Skill, UUID> {
    @Query("select new jass.security.dto.swengineer.SkillDto(skill)" +
            "from Skill skill " +
            "where skill.swEngineer.id = :engineerId")
    List<SkillDto> findBySoftwareEngineerId(UUID engineerId);

    @Modifying
    @Query("DELETE from Skill skill " +
            "where skill.id = :skillId and skill.swEngineer.id = :swEngineerId")
    void deleteBySwEngineerId(@Param("skillId") UUID skillId,@Param("swEngineerId") UUID swEngineerId);
}
