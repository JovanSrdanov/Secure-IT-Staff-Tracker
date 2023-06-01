package jass.security.repository;

import jass.security.dto.swengineer.SearchSwEngineerDto;
import jass.security.dto.swengineer.SearchSwResponseDto;
import jass.security.model.SoftwareEngineer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ISoftwareEngineerRepository extends JpaRepository<SoftwareEngineer, UUID> {
    @Query("select new jass.security.dto.swengineer.SearchSwResponseDto(sweng.name, sweng.surname, " +
            "sweng.profession, sweng.phoneNumber, sweng.address.country, sweng.address.city, sweng.address.street, sweng.address.streetNumber, sweng, acc.email, acc.isBlocked) " +
            "from SoftwareEngineer sweng left join Account acc " +
            "on sweng.id = acc.employeeId " +
            "where acc.status = 1 and " +
            "lower(sweng.name) like lower(concat('%', :#{#dto.name},'%')) and " +
            "lower(sweng.surname) like lower(concat('%', :#{#dto.surname},'%')) and " +
            "lower(acc.email) like lower(concat('%', :#{#dto.email},'%')) and " +
            "sweng.dateOfEmployment >= :#{#dto.employmentDateRange.startDate} and " +
            "sweng.dateOfEmployment <= :#{#dto.employmentDateRange.endDate}")
    List<SearchSwResponseDto> searchSw(@Param("dto") SearchSwEngineerDto dto);
}
