package jass.security.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class ActivationLink {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private Date creationDate;
    @Column(nullable = false)
    private boolean used;
}
