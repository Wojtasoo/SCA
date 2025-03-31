package swiftcodes.service.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestPropertySource(locations = "classpath:application-test.properties")
class SwiftCodeServiceTest {

    @InjectMocks
    private SwiftCodeService swiftCodeService;

    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSwiftCodeDetails_found() {
        SwiftCode code = new SwiftCode();
        code.setSwiftCode("TEST123");
        when(swiftCodeRepository.findBySwiftCode("TEST123")).thenReturn(code);

        SwiftCode found = swiftCodeService.getSwiftCodeDetails("TEST123");
        assertThat(found).isNotNull();
        assertThat(found.getSwiftCode()).isEqualTo("TEST123");
    }

    @Test
    void testGetSwiftCodeDetails_notFound() {
        when(swiftCodeRepository.findBySwiftCode(anyString())).thenReturn(null);
        SwiftCode found = swiftCodeService.getSwiftCodeDetails("NONEXISTENT");
        assertThat(found).isNull();
    }

    @Test
    void testGetBranchesForHeadquarter() {
        SwiftCode branch1 = new SwiftCode();
        branch1.setSwiftCode("HQSWFT01001");
        branch1.setIsHeadquarter(false);
        SwiftCode branch2 = new SwiftCode();
        branch2.setSwiftCode("HQSWFT01002");
        branch2.setIsHeadquarter(false);
        List<SwiftCode> branches = Arrays.asList(branch1, branch2);

        when(swiftCodeRepository.findBySwiftCodeStartingWithAndIsHeadquarterFalse("HQSWFT01")).thenReturn(branches);

        List<SwiftCode> result = swiftCodeService.getBranchesForHeadquarter("HQSWFT01XXX");
        assertThat(result).hasSize(2);
    }

    @Test
    void testAddSwiftCode_emptyFields() {
        SwiftCode code = new SwiftCode();
        code.setSwiftCode("TESTADD01XXX");
        code.setBankName("");
        code.setAddress("Address Test");
        code.setCountryISO2("US");
        code.setCountryName("");
        code.setIsHeadquarter(true);

        swiftCodeService.addSwiftCode(code);

        //Verify that repository.save() was called
        verify(swiftCodeRepository, times(1)).save(code);

        //Assert that the empty fields are now "Not specified"
        assertThat(code.getBankName()).isEqualTo("Not Specified");
        assertThat(code.getCountryName()).isEqualTo("Not Specified");
    }

    @Test
    void testAddSwiftCode_emptyFieldsSetToNotSpecified() {
        //SwiftCode with some empty fields
        SwiftCode code = new SwiftCode();
        code.setSwiftCode("ABCXXX");
        code.setBankName("  ");
        code.setAddress(null);
        code.setCountryISO2("");
        code.setCountryName("US");

        // When saving, the repository should return the same object
        when(swiftCodeRepository.save(any(SwiftCode.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SwiftCode saved = swiftCodeService.addSwiftCode(code);

        //Empty fields should be set to "Not Specified"
        assertThat(saved.getBankName()).isEqualTo("Not Specified");
        assertThat(saved.getAddress()).isEqualTo("Not Specified");
        assertThat(saved.getCountryISO2()).isEqualTo("Not Specified");
        //CountryName remains as provided if not empty.
        assertThat(saved.getCountryName()).isEqualTo("US");
    }

    @Test
    void testGetBranchesForHeadquarter_returnsCorrectBranches() {
        //headquarter swift code and two associated branches.
        String hqSwift = "ABCDEFGHXXX";
        String prefix = hqSwift.substring(0, 8);

        SwiftCode branch1 = new SwiftCode();
        branch1.setSwiftCode(prefix + "BRANCH1");
        branch1.setIsHeadquarter(false);

        SwiftCode branch2 = new SwiftCode();
        branch2.setSwiftCode(prefix + "BRANCH2");
        branch2.setIsHeadquarter(false);

        List<SwiftCode> branches = Arrays.asList(branch1, branch2);
        when(swiftCodeRepository.findBySwiftCodeStartingWithAndIsHeadquarterFalse(prefix))
                .thenReturn(branches);

        List<SwiftCode> result = swiftCodeService.getBranchesForHeadquarter(hqSwift);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSwiftCode()).contains(prefix);
        assertThat(result.get(1).getSwiftCode()).contains(prefix);
    }
}
