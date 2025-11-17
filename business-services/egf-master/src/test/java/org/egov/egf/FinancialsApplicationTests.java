package org.egov.egf;

import org.egov.egf.master.TestConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Import(TestConfiguration.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Disabled
public class FinancialsApplicationTests {

	@Test
	public void contextLoads() {
	}

}
