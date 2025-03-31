package swiftcodes.service.app;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/swift-codes")
public class APIController {

    @Autowired
    private SwiftCodeService swiftCodeService;
    private static final String ERROR_CODE="ERR-500";

    // Endpoint 1
    @GetMapping("/{swiftCode}")
    public ResponseEntity<Map<String, Object>> getSwiftCode(@PathVariable String swiftCode) {
        try {
            SwiftCode code = swiftCodeService.getSwiftCodeDetails(swiftCode);
            if (code == null) {
                throw new APIException("ERR-404", "SWIFT code not found: " + swiftCode);
            }

            //Check if the input swift code is a headquarter.
            boolean isHeadquarterInput = swiftCode.toUpperCase().endsWith("XXX");
            Map<String, Object> response = new LinkedHashMap<>();

            if (isHeadquarterInput) {
                APIDTO headquarterDto = new APIDTO(
                        code.getAddress(),
                        code.getBankName(),
                        code.getCountryISO2(),
                        code.getCountryName(),
                        true,
                        code.getSwiftCode()
                );
                response.put("address", headquarterDto.getAddress());
                response.put("bankName", headquarterDto.getBankName());
                response.put("countryISO2", headquarterDto.getCountryISO2());
                response.put("countryName", headquarterDto.getCountryName());
                response.put("isHeadquarter", headquarterDto.isHeadquarter());
                response.put("swiftCode", headquarterDto.getSwiftCode());


                List<SwiftCode> branches = swiftCodeService.getBranchesForHeadquarter(swiftCode);
                List<APIDTO> branchDtos = branches.stream()
                        .map(branch -> new APIDTO(
                                branch.getAddress(),
                                branch.getBankName(),
                                branch.getCountryISO2(),
                                null,  // No country name for branch
                                branch.getIsHeadquarter(),
                                branch.getSwiftCode()
                        ))
                        .toList();
                response.put("branches", branchDtos);
            } else {
                //Return for branch SWIFT CODE
                APIDTO branchDto = new APIDTO(
                        code.getAddress(),
                        code.getBankName(),
                        code.getCountryISO2(),
                        code.getCountryName(),
                        false,
                        code.getSwiftCode()
                );
                response.put("address", branchDto.getAddress());
                response.put("bankName", branchDto.getBankName());
                response.put("countryISO2", branchDto.getCountryISO2());
                response.put("countryName", branchDto.getCountryName());
                response.put("isHeadquarter", branchDto.isHeadquarter());
                response.put("swiftCode", branchDto.getSwiftCode());
            }
            return ResponseEntity.ok(response);
        } catch (APIException ex) {
            throw ex;
        }catch (Exception ex) {
            ex.printStackTrace();
            throw new APIException(ERROR_CODE, "Error retrieving SWIFT code details: " + ex.getMessage());
        }
    }

    // Endpoint 2
    @GetMapping("/country/{countryISO2}")
    public ResponseEntity<Map<String, Object>> getSwiftCodesByCountry(@PathVariable String countryISO2) {
        try {
            List<SwiftCode> codes = swiftCodeService.getSwiftCodesByCountry(countryISO2);
            if (codes.isEmpty()) {
                throw new APIException("ERR-404", "No SWIFT codes found for country: " + countryISO2);
            }

            String countryName = codes.getFirst().getCountryName();

            List<APIDTO> codeDtos = codes.stream()
                    .map(code -> new APIDTO(
                            code.getAddress(),
                            code.getBankName(),
                            code.getCountryISO2(),
                            code.getIsHeadquarter(),
                            code.getSwiftCode()
                    ))
                    .toList();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("countryISO2", countryISO2);
            response.put("countryName", countryName);
            response.put("swiftCodes", codeDtos);

            return ResponseEntity.ok(response);
        } catch (APIException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new APIException(ERROR_CODE, "Error retrieving SWIFT codes for country: " + ex.getMessage());
        }
    }

    // Endpoint 3
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> addSwiftCode(@RequestBody SwiftCode swiftCode) {
        try {
            if (isInvalid(swiftCode)) {
                Map<String, String> errorResponse = new LinkedHashMap<>();
                errorResponse.put("error_code", "INVALID_PAYLOAD");
                errorResponse.put("message", "Payload must include non-null value for address and non empty for: bankName, countryISO2, countryName, swiftCode and a valid isHeadquarter boolean.");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            swiftCodeService.addSwiftCode(swiftCode);
            Map<String, String> response = new LinkedHashMap<>();
            response.put("message", "SWIFT code added successfully");
            return ResponseEntity.ok(response);
        } catch (APIException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new APIException(ERROR_CODE, "Error adding SWIFT code: " + ex.getMessage());
        }
    }

    @ExceptionHandler(com.fasterxml.jackson.databind.JsonMappingException.class)
    public ResponseEntity<Map<String, String>> handleJsonMappingException(JsonMappingException ex) {
        Map<String, String> errorResponse = new LinkedHashMap<>();
        errorResponse.put("error_code", "INVALID_PAYLOAD");
        errorResponse.put("message",
                "One or more fields have invalid types. "
                        + "Please ensure all string fields are actually strings.");

        return ResponseEntity.badRequest().body(errorResponse);
    }

    private boolean isInvalid(SwiftCode code) {
        // Check for null or empty string fields
        if (code.getAddress() == null ) return true;
        if (code.getBankName() == null || code.getBankName().trim().isEmpty()) return true;
        if (code.getCountryISO2() == null || code.getCountryISO2().trim().isEmpty()) return true;
        if (code.getCountryName() == null || code.getCountryName().trim().isEmpty()) return true;
        if (code.getSwiftCode() == null || code.getSwiftCode().trim().isEmpty()) return true;
        if (code.getIsHeadquarter() == null) return true;
        return false;
    }

    // Endpoint 4
    @DeleteMapping(value ="/{swiftCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> deleteSwiftCode(@PathVariable String swiftCode) {
        try {
            swiftCodeService.deleteSwiftCode(swiftCode);
            Map<String, String> response = new LinkedHashMap<>();
            response.put("message", "SWIFT code deleted successfully");
            return ResponseEntity.ok(response);
        } catch (APIException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new APIException(ERROR_CODE, "Error deleting SWIFT code: " + ex.getMessage());
        }
    }
}
