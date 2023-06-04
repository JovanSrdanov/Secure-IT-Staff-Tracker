package jass.security.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class DateRange implements Serializable {
    @Column(nullable = false)
    private Date startDate;
    @Column(nullable = true)
    private Date endDate;
}
