
package web.servlets;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.commons.csv.*;
import org.apache.commons.io.input.*;
import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import web.common.DatabaseHandlerLocal;
import web.csvData.CSVFileData;

/**
 * This Servlet handles ".csv" file upload to Database requests based on 
 * different file parsing methods selected.
 * 
 * @author SoundlyGifted
 */
@WebServlet(name = "FileUploadServlet", urlPatterns = {"/FileUploadServlet"})
@MultipartConfig(maxFileSize = 16177215)
public class FileUploadServlet extends HttpServlet {

    @EJB
    private DatabaseHandlerLocal databaseHandler;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, 
            HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        /* ".csv" file received from request as part of "multipart/form-data" 
        Post request. */
        Part filePart = request.getPart("file");
        
        String parsingMethodSelected = defineParsingMethodSelected(request);
       
        if (uploadFileToDB(filePart, parsingMethodSelected)) {
            System.out.println("*** [FileUploadServlet] File Successfully "
                    + "uploaded using '" + parsingMethodSelected + "' ***");
        } else {
            System.out.println("*** [FileUploadServlet] File was not uploaded, "
                    + "attempted to use '" + parsingMethodSelected + "' ***");
        }
        
        getServletContext().getRequestDispatcher("/index.jsp").forward(request, 
                response);
    }
    
    private String defineParsingMethodSelected (HttpServletRequest request) {
        Map<String, String> parsingMethods = new HashMap<>();
        String parsingMethodSelected = "NoMethodSelected";

        parsingMethods.put("CommonsCSV", request
                .getParameter("UploadWithCommonsCSV"));
        parsingMethods.put("OpenCSV", request
                .getParameter("UploadWithOpenCSV"));
        parsingMethods.put("SomeOtherMethod", request
                .getParameter("SomeOtherButton"));

        for (Map.Entry<String, String> entry : parsingMethods.entrySet()) {
            if (entry.getValue() != null) {
                parsingMethodSelected = entry.getKey();
            }
        }
        return parsingMethodSelected;
    }

    private boolean uploadFileToDB(Part filePart, String parsingMethodSelected)
            throws IOException {
        /* Collection to keep records from csv-file.
        Each record is a Map with a csv table values mapped to 
        the csv table headers (Map<String, String>). */
        CSVFileData csvFileData;

        if (filePart != null) {
            String fileName = filePart.getSubmittedFileName();
            if (!fileName.trim().isEmpty()) {
                System.out
                        .println("*** [FileUploadServlet.uploadFileToDB] File "
                                + "name = " + fileName + " ***");
                if (fileName.endsWith(".csv")) {
                    System.out.println("*** [FileUploadServlet.uploadFileToDB] "
                            + "This is a CSV file ***");

                    switch (parsingMethodSelected) {
                        case "CommonsCSV":
                            csvFileData = parseWithCommonsCSV(filePart);
                            if (csvFileData.getRecordListWithCSVFileHeaders()
                                    .isEmpty()) {
                                System.out.println("*** "
                                        + "[FileUploadServlet.uploadFileToDB] "
                                        + "This CSV file contains no data ***");
                                return false;
                            }
                            break;
                        case "OpenCSV":
                            csvFileData = parseWithOpenCSV(filePart);
                            if (csvFileData.getRecordListWithCSVFileHeaders()
                                    .isEmpty()) {
                                System.out.println("*** "
                                        + "[FileUploadServlet.uploadFileToDB] "
                                        + "This CSV file contains no data ***");
                                return false;
                            }
                            break;
                        case "SomeOtherMethod2":
                            csvFileData = new CSVFileData();
                            break;
                        default:
                            csvFileData = new CSVFileData();
                            break;
                    }
                    return databaseHandler.insertMultRecs(csvFileData);
                } else {
                    System.out.println("*** [FileUploadServlet.uploadFileToDB] "
                            + "This is not a CSV file ***");
                }
            } else {
                System.out.println("*** [FileUploadServlet.uploadFileToDB] "
                        + "No file selected ***");
            }
        }
        return false;
    }
    
    private CSVFileData parseWithCommonsCSV(Part filePart)
            throws IOException {
        /* Collection to keep records from csv-file.
        Each record is a Map with a csv table values mapped to 
        the csv table headers (Map<String, String>). */
        CSVFileData csvFileData = new CSVFileData();

        /* Using BOMInputStream class from Apache Commons IO library to deal 
        with Byte Order Marks (BOM).
        BOMInputStream is a wrapper class for InputStream. */
        try (BOMInputStream inputStream = new BOMInputStream(filePart
                .getInputStream())) {
            if (inputStream != null) {
                try (InputStreamReader inputStreamReader
                        = new InputStreamReader(inputStream, "UTF-8")) {
                    // Parsing CSV file using Apache Commons CSV library.
                    try (org.apache.commons.csv.CSVParser csvParser 
                            = new org.apache.commons.csv
                                    .CSVParser(inputStreamReader, CSVFormat
                                            .EXCEL.withDelimiter(';')
                                    .withFirstRecordAsHeader())) {
                        Map<String, String> rec;
                        for (CSVRecord record : csvParser) {
                            rec = record.toMap();
                            // Checking CSVFile headers on the first record only.
                            if (record.getRecordNumber() == 1) {
                                ArrayList<String> allowedHeaders
                                        = csvFileData.getAllowedCSVFileHeaders();
                                ArrayList<String> actualHeaders 
                                        = new ArrayList<>();
                                for (String actualHeader : rec.keySet()) {
                                    actualHeaders.add(actualHeader);
                                }
                                if (!actualHeaders.equals(allowedHeaders)) {
                                    return csvFileData;
                                }
                            }
                            if (!rec.isEmpty()) {
                                csvFileData.addRecord(rec);
                            }
                        }
                    }
                }
            }
        }
        return csvFileData;
    }
    
    private CSVFileData parseWithOpenCSV(Part filePart)
            throws IOException {
        /* Collection to keep records from csv-file.
        Each record is a Map with a csv table values mapped to 
        the csv table headers (Map<String, String>). */
        CSVFileData csvFileData = new CSVFileData();

        try (InputStreamReader inputStreamReader
                = new InputStreamReader(filePart.getInputStream(), "UTF-8")) {

            com.opencsv.ICSVParser parser
                    = new CSVParserBuilder().withSeparator(';')
                            .build();

            CSVReaderHeaderAwareBuilder readerBuilder
                    = new CSVReaderHeaderAwareBuilder(inputStreamReader);
            readerBuilder.withCSVParser(parser);

            try (CSVReaderHeaderAware reader = readerBuilder.build()) {
                try {
                    Map<String, String> rec;
                    while ((rec = reader.readMap()) != null) {
                        // Checking CSVFile headers on the first record only.
                        if (reader.getRecordsRead() == 1) {
                            ArrayList<String> allowedHeaders 
                                    = csvFileData.getAllowedCSVFileHeaders();
                            ArrayList<String> actualHeaders = new ArrayList<>();                           
                            for (String actualHeader : rec.keySet()) {
                                actualHeaders.add(actualHeader);

                            }
                            if (!actualHeaders.equals(allowedHeaders)) {
                                return csvFileData;
                            }
                        }
                        if (!rec.isEmpty()) {
                            csvFileData.addRecord(rec);
                        }
                    }
                } catch (CsvValidationException ex) {
                    System.out.println("*** [FileUploadServlet"
                            + ".parseWithOpenCSV] + csv file contains rows "
                            + "with invalid values. ***");
                }
            }
        }
        return csvFileData;
    }

    
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
