package jass.security.dto.swengineer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jass.security.dto.AddressDto;
import jass.security.model.Address;
import jass.security.model.Skill;
import jass.security.model.SoftwareEngineer;
import jass.security.utils.ObjectMapperUtils;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Getter
@Setter
public class SearchSwResponseDto {
    private String name;
    private String surname;
    private AddressDto address;
    private String profession;
    private String phoneNumber;

    private String dateOfEmployment;

    private String email;

    private Boolean isBlocked;

    private List<SkillInfoDto> skills;

    public SearchSwResponseDto(String name, String surname, String profession, String phoneNumber,
                               String country, String city, String street, String streetNumber, SoftwareEngineer sw, String email, Boolean isBlocked) {
        this.name = name;
        this.surname = surname;
        this.profession = profession;
        this.phoneNumber = phoneNumber;
        this.dateOfEmployment = sw.getDateOfEmployment().toString();
        this.email = email;
        this.isBlocked = isBlocked;

        this.address = new AddressDto(country, city, street, streetNumber);
        this.skills = ObjectMapperUtils.mapAll(new ArrayList<>(sw.getSkills()), SkillInfoDto.class);
    }
}
