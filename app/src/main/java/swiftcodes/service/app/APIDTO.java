package swiftcodes.service.app;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonPropertyOrder({
        "address",
        "bankName",
        "countryISO2",
        "countryName",
        "isHeadquarter",
        "swiftCode"
})

public class APIDTO {
    @JsonDeserialize(using = StrictStringDeserializer.class)
    private final String swiftCode;

    @JsonDeserialize(using = StrictStringDeserializer.class)
    private final String bankName;

    @JsonDeserialize(using = StrictStringDeserializer.class)
    private final String address;

    @JsonDeserialize(using = StrictStringDeserializer.class)
    private final String countryISO2;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String countryName;

    @JsonProperty("isHeadquarter")
    private final boolean isHeadquarter;


    public APIDTO(String address, String bankName, String countryISO2, boolean isHeadquarter, String swiftCode) {
        this(address, bankName, countryISO2, null, isHeadquarter, swiftCode);  // Calls the full constructor
    }

    public APIDTO(String address, String bankName, String countryISO2, String countryName, boolean isHeadquarter, String swiftCode) {
        this.swiftCode = swiftCode;
        this.bankName = checkEmpty(bankName);
        this.address = checkEmpty(address);
        this.countryISO2 = checkEmpty(countryISO2);
        this.countryName = (countryName == null || countryName.trim().isEmpty()) ? null : countryName;  // Only set when passed (HQ or country-level)
        this.isHeadquarter = isHeadquarter;
    }

    // Getters
    public String getSwiftCode() { return swiftCode; }

    public String getBankName() { return bankName; }

    public String getAddress() { return address; }

    public String getCountryISO2() { return countryISO2; }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getCountryName() { return countryName; }

    @JsonProperty("isHeadquarter")
    public boolean isHeadquarter() { return isHeadquarter; }

    private String checkEmpty(String value) {
        return (value == null || value.trim().isEmpty()) ? "Not Specified" : value;
    }
}
