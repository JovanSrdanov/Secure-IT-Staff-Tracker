package jass.security.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class Cv {
    @Id
    private UUID id;
    @Column(nullable = false)
    private byte[] secretKey;
    @Column(nullable = false)
    private byte[] aesInitVector;
}
