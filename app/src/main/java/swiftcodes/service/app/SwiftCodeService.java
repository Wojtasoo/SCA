package swiftcodes.service.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SwiftCodeService {

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    public SwiftCode getSwiftCodeDetails(String swiftCode) {
        return swiftCodeRepository.findBySwiftCode(swiftCode);
    }

    public List<SwiftCode> getSwiftCodesByCountry(String countryISO2) {
        return swiftCodeRepository.findByCountryISO2(countryISO2.toUpperCase());
    }

    public SwiftCode addSwiftCode(SwiftCode swiftCode) {
        //Checking if country fields are empty
        swiftCode.setCountryISO2(checkEmpty(swiftCode.getCountryISO2()));
        swiftCode.setCountryName(checkEmpty(swiftCode.getCountryName()));
        return swiftCodeRepository.save(swiftCode);
    }

    @Transactional
    public void deleteSwiftCode(String swiftCode) {
        SwiftCode existing = swiftCodeRepository.findBySwiftCode(swiftCode);
        if (existing == null) {
            throw new APIException("SWIFT_NOT_FOUND", "Swift code " + swiftCode + " not found");
        }
        try {
            swiftCodeRepository.deleteBySwiftCode(swiftCode);
        } catch (Exception ex) {
            throw new APIException("ERR-DELETE", "Failed to delete SWIFT code: " + swiftCode + ". " + ex.getMessage());
        }
    }

    public List<SwiftCode> getBranchesForHeadquarter(String headquarterSwiftCode) {
        //8 characters to find associated branch codes
        String prefix = headquarterSwiftCode.substring(0, 8);
        return swiftCodeRepository.findBySwiftCodeStartingWithAndIsHeadquarterFalse(prefix);
    }

    private String checkEmpty(String value) {
        return (value == null || value.trim().isEmpty()) ? "Not Specified" : value;
    }
}
