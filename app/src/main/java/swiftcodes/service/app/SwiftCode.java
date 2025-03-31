package swiftcodes.service.app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;

@Entity
@Table(name = "swift_codes")
public class SwiftCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @JsonDeserialize(using = StrictStringDeserializer.class)
    @Column(name = "swiftCode", unique = true, nullable = false)
    private String swiftCode;

    @JsonDeserialize(using = StrictStringDeserializer.class)
    @Column(name = "bankName")
    private String bankName;

    @JsonDeserialize(using = StrictStringDeserializer.class)
    @Column(name = "address")
    private String address;

    @JsonDeserialize(using = StrictStringDeserializer.class)
    @Column(name = "countryISO2")
    private String countryISO2;

    @JsonDeserialize(using = StrictStringDeserializer.class)
    @Column(name = "countryName")
    private String countryName;

    @Column(name = "isHeadquarter")
    private Boolean isHeadquarter;


    //Getters and Setters
    public Long  getId() {
        return id;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode.toUpperCase();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = checkEmpty(bankName);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = checkEmpty(address);
    }

    public String getCountryISO2() {
        return countryISO2;
    }

    public void setCountryISO2(String countryISO2) {
        this.countryISO2 = checkEmpty(countryISO2);
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = checkEmpty(countryName);
    }

    public Boolean getIsHeadquarter() {
        return isHeadquarter;
    }

    public void setIsHeadquarter(Boolean isHeadquarter) {
        this.isHeadquarter = isHeadquarter;
    }

    private String checkEmpty(String value) {
        return (value == null || value.trim().isEmpty()) ? "Not Specified" : value;
    }
}
