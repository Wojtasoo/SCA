package swiftcodes.service.app;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface SwiftCodeRepository extends JpaRepository<SwiftCode, Long> {

    //Finding bank by SWIFT CODE
    SwiftCode findBySwiftCode(String swiftCode);

    //Finding all bank by Country Code
    List<SwiftCode> findByCountryISO2(String countryISO2);

    //Retrieving branch records based on the first 8 characters and ensuring they are not headquarters.
    List<SwiftCode> findBySwiftCodeStartingWithAndIsHeadquarterFalse(String prefix);

    //Deleting by SWIFT CODE
    @Modifying
    void deleteBySwiftCode(String swiftCode);
}
