package com.example.portal;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Ative quando o PostgreSQL estiver acessível com as credenciais de application.properties")
class PortalApplicationTests {

	@Test
	void contextLoads() {
	}

}
