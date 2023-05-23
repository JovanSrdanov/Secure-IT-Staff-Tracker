package jass.security.model;

import jakarta.persistence.*;
import jass.security.dto.project.UpdateProjectDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class Project {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private DateRange duration;

    public void update(UpdateProjectDto dto) {
        setName(dto.getName());
        this.duration.setEndDate(dto.getEndDate());
    }
}