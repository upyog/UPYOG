package org.upyog.chb.web.models.transaction;

import lombok.*;

<<<<<<< HEAD
import jakarta.validation.constraints.NotNull;
=======
import javax.validation.constraints.NotNull;
>>>>>>> master-LTS
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TaxAndPayment {
	
	private BigDecimal taxAmount;
	
	@NotNull
	private BigDecimal amountPaid;
	
	@NotNull
	private String billId;
}
