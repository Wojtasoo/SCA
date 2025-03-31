package swiftcodes.service.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SwiftCodesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwiftCodesApplication.class, args);
    }

    //Loading Excel Data at startup
    @Bean
    CommandLineRunner init(ExcelLoaderService csvLoaderService) {
        return args -> csvLoaderService.loadExcelData();
    }
}
