import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import modal.KnittedProduct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class FilesParsingTest {
    private ClassLoader cl = FilesParsingTest.class.getClassLoader();
    HashSet<String> h = new HashSet<String>();
    String[] expectedFileNames = {"all_countries.xls", "sample.pdf", "SampleCSVFile_2kb.csv"};


    @Test
    void zipTest() throws Exception {

        try (InputStream is = cl.getResourceAsStream("testData.zip"); ZipInputStream zs = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zs.getNextEntry()) != null) {
                h.add(entry.getName());
            }
            for (int i = 0; i < expectedFileNames.length; i++) {
                Assertions.assertTrue(h.contains(expectedFileNames[i]));
            }
        }
    }

    @Test
    void pdfParseTest() throws Exception {

        try (InputStream is = cl.getResourceAsStream("testData.zip"); ZipInputStream zs = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zs.getNextEntry()) != null) {
                if (entry.getName().equals("sample.pdf")) {
                    PDF pdf = new PDF(zs);
                    Assertions.assertTrue(pdf.text.contains("A Simple PDF File"));
                }
            }
        }
    }

    @Test
    void xlsParseTest() throws Exception {

        try (InputStream is = cl.getResourceAsStream("testData.zip"); ZipInputStream zs = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zs.getNextEntry()) != null) {
                if (entry.getName().equals("all_countries.xls")) {
                    XLS xls = new XLS(zs);
                    Assertions.assertTrue(xls.excel.getSheetAt(0).getRow(151).getCell(0).getStringCellValue().startsWith("Serbia"));
                }
            }
        }
    }

    @Test
    void csvParseTest() throws Exception {

        try (InputStream is = cl.getResourceAsStream("testData.zip"); ZipInputStream zs = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zs.getNextEntry()) != null) {
                if (entry.getName().equals("SampleCSVFile_2kb.csv")) {
                    InputStreamReader isr = new InputStreamReader(zs);
                    CSVReader csvReader = new CSVReader(isr);
                    List<String[]> content = csvReader.readAll();
                    Assertions.assertArrayEquals(new String[]{"1", "Eldon Base for stackable storage shelf", "platinum", "0.8"}, content.get(0));
                }
            }
        }
    }

    @Test
    void jsonParseTest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream is = cl.getResourceAsStream("KnittedProduct.json"); InputStreamReader isr = new InputStreamReader(is)) {
            KnittedProduct knittedProduct = objectMapper.readValue(isr, KnittedProduct.class);
            Assertions.assertEquals("Sweater", knittedProduct.name);
            Assertions.assertTrue(knittedProduct.isReady);
            Assertions.assertEquals("Warm sweater", knittedProduct.tags.get(1));
            Assertions.assertEquals("Natural", knittedProduct.yarnDescription.colorOfYarn);
            Assertions.assertEquals(100, knittedProduct.yarnDescription.weightOfOneSkeinInGrams);
        }
    }
}
