package jass.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@MappedSuperclass
public class Employee {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Address address;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String profession; //zvanje
}




















