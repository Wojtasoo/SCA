package swiftcodes.service.app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class SwiftCodeIntegrationTest {

    @Autowired
    private SwiftCodeService swiftCodeService;

    @Test
    void testAddAndRetrieveSwiftCode() {
        //Given: a new SwiftCode record
        SwiftCode code = new SwiftCode();
        code.setSwiftCode("USXYZXXX");
        code.setBankName("Integration Bank");
        code.setAddress("456 Integration Ave");
        code.setCountryISO2("US");
        code.setCountryName("United States");
        code.setIsHeadquarter(true);

        //adding the record
        SwiftCode saved = swiftCodeService.addSwiftCode(code);
        assertThat(saved).isNotNull();

        //retrieve it by its SWIFT code
        SwiftCode retrieved = swiftCodeService.getSwiftCodeDetails("USXYZXXX");
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getBankName()).isEqualTo("Integration Bank");
        assertThat(retrieved.getAddress()).isEqualTo("456 Integration Ave");
    }
}
