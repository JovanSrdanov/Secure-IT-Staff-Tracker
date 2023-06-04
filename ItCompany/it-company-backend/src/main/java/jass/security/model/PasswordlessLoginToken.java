package jass.security.model;

import jakarta.persistence.Access;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PasswordlessLoginToken {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private int duration;    //Minutes
    @Column(nullable = false)
    private boolean used;

    public boolean isExpired(){
       LocalDateTime currentTime = LocalDateTime.now();
       return currentTime.isAfter(createdAt.plusMinutes(duration));
    }
}
