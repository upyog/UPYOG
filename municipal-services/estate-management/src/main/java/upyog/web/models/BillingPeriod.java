package upyog.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BillingPeriod {

    private LocalDate periodFrom;
    private LocalDate periodTo;

}