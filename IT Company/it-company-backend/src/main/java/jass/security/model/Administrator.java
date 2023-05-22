package jass.security.model;


import jakarta.persistence.Entity;
import lombok.*;

import java.util.UUID;


@Entity
public class Administrator extends Employee {
    public Administrator(){}
    public Administrator(UUID adminId, String name, String surname, Address address, String phoneNumber, String profession) {
        super(adminId, name, surname, address, phoneNumber, profession);
    }
}
