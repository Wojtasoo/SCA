package swiftcodes.service.app;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelLoaderService {

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    public void loadExcelData() {
        try {
            ClassPathResource resource = new ClassPathResource("SWIFT_CODES.xlsx");
            InputStream inputStream = resource.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0); //Data is in the first sheet

            Iterator<Row> rowIterator = sheet.iterator();
            boolean firstRow = true;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (firstRow) { //Skip header
                    firstRow = false;
                    continue;
                }

                List<String> rowData = new ArrayList<>();
                for (Cell cell : row) {
                    cell.setCellType(CellType.STRING); //Converting all cells to String
                    rowData.add(cell.getStringCellValue().trim());
                }

                if (rowData.size() < 7)
                    continue;

                SwiftCode code = new SwiftCode();
                System.out.println("Country Code:"+rowData.get(0));
                code.setCountryISO2(rowData.get(0)); //Country Code
                System.out.println("Swift Code:"+rowData.get(1));
                code.setSwiftCode(rowData.get(1)); //SWIFT Code
                System.out.println("Bank Name:"+rowData.get(3));
                code.setBankName(rowData.get(3)); //Bank Name
                System.out.println("Bank Address:"+rowData.get(4));
                code.setAddress(rowData.get(4)); //Bank Address
                System.out.println("Country Name:"+rowData.get(6));
                code.setCountryName(rowData.get(6)); //Country Name
                code.setIsHeadquarter(rowData.get(1).endsWith("XXX"));

                swiftCodeRepository.save(code);
            }
            workbook.close();
        } catch (Exception e) {
            System.err.println("Error loading Excel data: " + e.getMessage());
        }
    }
}