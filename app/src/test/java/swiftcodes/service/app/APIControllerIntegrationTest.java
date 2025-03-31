package swiftcodes.service.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class APIControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SwiftCodeService swiftCodeService;

    private SwiftCode sampleHQ;

    private SwiftCode sampleBranch;

    @BeforeEach
    public void setup() {
        //Clean database and add test data before each test
        swiftCodeRepository.deleteAll();

        sampleHQ = new SwiftCode();
        sampleHQ.setSwiftCode("SMPHQ001XXX");
        sampleHQ.setBankName("Sample Bank HQ");
        sampleHQ.setAddress("HQ Sample Address");
        sampleHQ.setCountryISO2("us");  // intentionally lowercase to test formatting
        sampleHQ.setCountryName("united states");  // intentionally lowercase
        sampleHQ.setIsHeadquarter(true);

        sampleBranch = new SwiftCode();
        sampleBranch.setSwiftCode("SMPHQ001001");
        sampleBranch.setBankName("Sample Bank Branch");
        sampleBranch.setAddress("Branch Sample Address");
        sampleBranch.setCountryISO2("us");  // intentionally lowercase
        sampleBranch.setCountryName("united states");  // intentionally lowercase
        sampleBranch.setIsHeadquarter(false);

    }

    @Test
    void testGetSwiftCode_notFound() throws Exception {
        mockMvc.perform(get("/v1/swift-codes/INVALID"))
                .andExpect(status().isNotFound())  // Ensure 404 is returned
                .andExpect(jsonPath("$.message").value("SWIFT code not found: INVALID"));
    }

    @Test
    void testGetSwiftCode_notFound_2() throws Exception {
        when(swiftCodeService.getSwiftCodeDetails("UNKNOWN")).thenReturn(null);

        mockMvc.perform(get("/v1/swift-codes/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddSwiftCode_withEmptyFields() throws Exception {
        // Creating a new swift code with some empty fields.
        SwiftCode newCode = new SwiftCode();
        newCode.setSwiftCode("NEWSWFT02XXX");
        newCode.setBankName(""); // Empty field should fail validity check
        newCode.setAddress("Some Address");
        newCode.setCountryISO2("GB");
        newCode.setCountryName(""); // Empty field should fail validity check
        newCode.setIsHeadquarter(true);

        String json = objectMapper.writeValueAsString(newCode);

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SWIFT code added successfully"));

        //Verify that the saved entity is returning null after failing validity check
        SwiftCode saved = swiftCodeRepository.findBySwiftCode("NEWSWFT02XXX");
        assertThat(saved).isNull();
    }

    @Test
    void testAddSwiftCode_invalidPayload() throws Exception {
        //Missing required fields.
        String jsonPayload = "{\"bankName\":\"Test Bank\"}";
        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteSwiftCode() throws Exception {
        doNothing().when(swiftCodeService).deleteSwiftCode("US123XXX");

        mockMvc.perform(delete("/v1/swift-codes/US123XXX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SWIFT code deleted successfully"));
    }

    @Test
    void testDeleteSwiftCode_success() throws Exception {
        // Assume that "HQSWFT01XXX" exists from setup
        mockMvc.perform(delete("/v1/swift-codes/HQSWFT01XXX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SWIFT code deleted successfully"));

    }

    @Test
    void testDeleteSwiftCode_notFound() throws APIException {
        // Attempt to delete a non-existent SWIFT code.
        doThrow(new APIException("SWIFT_NOT_FOUND", "Swift code NONEXISTENT not found"))
                .when(swiftCodeService).deleteSwiftCode("NONEXISTENT");
    }

    @Test
    void testGetSwiftCode_found() throws Exception {
        //Given: a SwiftCode representing a headquarter.
        SwiftCode code = new SwiftCode();
        code.setAddress("123 Main St");
        code.setBankName("Test Bank");
        code.setCountryISO2("US");
        code.setCountryName("United States");
        code.setIsHeadquarter(true);
        code.setSwiftCode("US123XXX");

        when(swiftCodeService.getSwiftCodeDetails("US123XXX")).thenReturn(code);
        //No branches returned for simplicity.
        when(swiftCodeService.getBranchesForHeadquarter("US123XXX")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/v1/swift-codes/US123XXX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.bankName").value("Test Bank"))
                .andExpect(jsonPath("$.countryISO2").value("US"))
                .andExpect(jsonPath("$.isHeadquarter").value(true))
                .andExpect(jsonPath("$.swiftCode").value("US123XXX"));
    }

    // ----------------------
    // Endpoint 1: GET /v1/swift-codes/{swiftCode}
    // ----------------------

    @Test
    void testGetSwiftCode_HQ_ResponseFormat() throws Exception {
        //When a HQ is requested, the top-level response should include countryName,
        //while each branch entry (if any) should have countryName omitted (null).
        when(swiftCodeService.getSwiftCodeDetails("SMPHQ001XXX")).thenReturn(sampleHQ);
        when(swiftCodeService.getBranchesForHeadquarter("SMPHQ001XXX"))
                .thenReturn(Arrays.asList(sampleBranch));

        mockMvc.perform(get("/v1/swift-codes/SMPHQ001XXX"))
                .andExpect(status().isOk())
                // Check top-level fields exist and are strings
                .andExpect(jsonPath("$.swiftCode", is("SMPHQ001XXX")))
                .andExpect(jsonPath("$.bankName", is("Sample Bank HQ")))
                .andExpect(jsonPath("$.countryISO2", is("us")))
                .andExpect(jsonPath("$.countryName", is("united states")))
                .andExpect(jsonPath("$.isHeadquarter", is(true)))
                // Check branches exists and each branch's countryName is null.
                .andExpect(jsonPath("$.branches", is(notNullValue())))
                .andExpect(jsonPath("$.branches[0].swiftCode", is("SMPHQ001001")))
                .andExpect(jsonPath("$.branches[0].countryName").doesNotExist());
    }

    @Test
    void testGetSwiftCode_Branch_ResponseFormat() throws Exception {
        // When a branch is requested, the response should include countryName at top level.
        when(swiftCodeService.getSwiftCodeDetails("SMPHQ001001")).thenReturn(sampleBranch);

        mockMvc.perform(get("/v1/swift-codes/SMPHQ001001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.swiftCode", is("SMPHQ001001")))
                .andExpect(jsonPath("$.bankName", is("Sample Bank Branch")))
                .andExpect(jsonPath("$.countryISO2", is("us")))
                .andExpect(jsonPath("$.countryName", is("united states")))
                .andExpect(jsonPath("$.isHeadquarter", is(false)))
                // For branch endpoints, there should be no "branches" array.
                .andExpect(jsonPath("$.branches").doesNotExist());
    }

    @Test
    void testGetSwiftCode_NotFound_ErrorResponse() throws Exception {
        // When the service returns null, the API should return a 404 error.
        when(swiftCodeService.getSwiftCodeDetails("NONEXISTENT")).thenReturn(null);

        mockMvc.perform(get("/v1/swift-codes/NONEXISTENT"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("SWIFT code not found")));
    }

    // ----------------------
    // Endpoint 2: GET /v1/swift-codes/country/{countryISO2}
    // ----------------------

    @Test
    void testGetSwiftCodesByCountry_ResponseFormat() throws Exception {
        // Given a mix of HQ and branch codes for a country, ensure that the top-level countryName is present,
        // but none of the entries in swiftCodes array include countryName.
        List<SwiftCode> codes = Arrays.asList(sampleHQ, sampleBranch);
        when(swiftCodeService.getSwiftCodesByCountry("US")).thenReturn(codes);

        //This test simulate that our sample objects already have uppercase country fields.
        sampleHQ.setCountryISO2("US");
        sampleHQ.setCountryName("UNITED STATES");
        sampleBranch.setCountryISO2("US");
        sampleBranch.setCountryName("UNITED STATES");

        mockMvc.perform(get("/v1/swift-codes/country/US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryISO2", is("US")))
                .andExpect(jsonPath("$.countryName", is("UNITED STATES")))
                .andExpect(jsonPath("$.swiftCodes", hasSize(2)))
                // Ensure that swiftCodes entries do not have countryName property.
                .andExpect(jsonPath("$.swiftCodes[0].countryName").doesNotExist())
                .andExpect(jsonPath("$.swiftCodes[1].countryName").doesNotExist());
    }

    @Test
    void testGetSwiftCodesByCountry_NoData() throws Exception {
        //When there are no swift codes found for the country, the API should return a 404.
        when(swiftCodeService.getSwiftCodesByCountry("ZZ")).thenReturn(List.of());

        mockMvc.perform(get("/v1/swift-codes/country/ZZ"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("No SWIFT codes found for country")));
    }

    // ----------------------
    // Endpoint 3: POST /v1/swift-codes
    // ----------------------

    @Test
    void testAddSwiftCode_InvalidPayload_WrongFieldType() throws Exception {
        // Send a JSON payload where bankName is a number instead of a string.
        // The API should respond with a bad request.
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("swiftCode", "WRNGTYP01XXX");
        payload.put("bankName", 12345);  // wrong type
        payload.put("address", "Some Address");
        payload.put("countryISO2", "GB");
        payload.put("countryName", "UNITED KINGDOM");
        payload.put("isHeadquarter", true);

        String json = objectMapper.writeValueAsString(payload);

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // ----------------------
    // Endpoint 4: DELETE /v1/swift-codes/{swiftCode}
    // ----------------------

    @Test
    void testDeleteSwiftCode_NotFound_ErrorResponse() throws Exception {
        // When attempting to delete a code that doesn't exist, the service should throw an APIException.
        doThrow(new APIException("ERR-404", "Swift code NONEXISTENT not found"))
                .when(swiftCodeService).deleteSwiftCode("NONEXISTENT");

        mockMvc.perform(delete("/v1/swift-codes/NONEXISTENT"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Swift code NONEXISTENT not found")));
    }
}