package web.servlets;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.csv.*;
import web.common.DatabaseHandlerLocal;
import web.csvData.CSVFileData;
import web.exceptions.GeneralApplicationException;

/**
 * This Servlet handles ".csv" file download from Database requests based on 
 * different file writing methods selected.
 * 
 * @author SoundlyGifted
 */
@WebServlet(name = "FileDownloadServlet", urlPatterns = {"/download.do"})
public class FileDownloadServlet extends HttpServlet {

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
            HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String downloadFileName = "myDataDownload";
        String home = System.getProperty("user.home");
        File outputFile = new File(home + "/Downloads/" + downloadFileName + ".csv");

        int downloadSuccessful = 0;
        HttpSession session = request.getSession();
        try {
            CSVFileData csvFileData = databaseHandler.selectAll();

            String downloadMethodSelected = defineMethodSelected(request);

            if (downloadFileFromDB(csvFileData, outputFile,
                    downloadMethodSelected)) {
                System.out.println("*** [FileDownloadServlet] File Successfully "
                        + "downloaded using '" + downloadMethodSelected + "' ***");
                downloadSuccessful = 1;
            } else {
                System.out.println("*** [FileDownloadServlet] File was not "
                        + "downloaded, attempted to use '"
                        + downloadMethodSelected + "' ***");
            }
        } catch (GeneralApplicationException e) {
            session.setAttribute("GeneralApplicationException", e.getMessage());
        }
        
        /* Using PRG (Post-Redirect-Get) pattern.
         * Instead of forwarding from doPost() method redirecting to the doGet()
         * method of another servlet (display servlet).
         * This is needed to avoid duplicate data submission when user
         * refreshes the page.        
         */
        response.sendRedirect("display.do?sd=" + downloadSuccessful);
    }

    
    private String defineMethodSelected(HttpServletRequest request) {
        Map<String, String> downloadMethods = new HashMap<>();
        String downloadMethodSelected = "NoMethodSelected";
        
        downloadMethods.put("CommonsCSV", 
                request.getParameter("DownloadWithCommonsCSV"));
        downloadMethods.put("OpenCSV", 
                request.getParameter("DownloadWithOpenCSV"));        
        downloadMethods.put("SomeOtherMethod", 
                request.getParameter("SomeOtherButton")); 
        
        for (Map.Entry<String, String> entry : downloadMethods.entrySet()) {
            if (entry.getValue() != null) {
                downloadMethodSelected = entry.getKey();
            }
        }
        return downloadMethodSelected;
    }
    
    private boolean downloadFileFromDB(CSVFileData csvFileData, File outputFile, 
            String downloadMethodSelected) throws IOException {
        ArrayList<String> csvFileHeaders = csvFileData
                .getAllowedCSVFileHeaders();
        List<Map<String, String>> recordList = csvFileData
                .getRecordListWithCSVFileHeaders();        

        FileWriter fileWriter = new FileWriter(outputFile);

        switch (downloadMethodSelected) {
            case "CommonsCSV":
                
                CSVFormat csvFormat = CSVFormat.EXCEL;
                CSVFormat.Builder csvFormatBuilder = csvFormat.builder();
                csvFormatBuilder.setDelimiter(';');
                csvFormatBuilder.setHeader(csvFileHeaders.get(0), 
                        csvFileHeaders.get(1));            
                
                try (CSVPrinter printer 
                        = new CSVPrinter(fileWriter, csvFormatBuilder.build())) {
                        for (Map<String, String> record : recordList) {
                            printer.printRecord(record.get(csvFileHeaders.get(0)),
                                    record.get(csvFileHeaders.get(1)));
                        }
                        printer.flush();
                }
                break;
            case "OpenCSV":
                try (CSVWriter writer = new CSVWriter(fileWriter, ';', 
                        CSVWriter.NO_QUOTE_CHARACTER, 
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER, 
                        CSVWriter.DEFAULT_LINE_END)) {
                    // writing headers first.
                    String[] headers = {csvFileHeaders.get(0), 
                        csvFileHeaders.get(1)};
                    writer.writeNext(headers);
                    
                    // then writing values of each record one by one.
                    for (Map<String, String> record : recordList) {
                        String[] values = {record.get(csvFileHeaders.get(0)),
                                    record.get(csvFileHeaders.get(1))};
                        writer.writeNext(values);
                    }
                    writer.flush();
                }
                break;
            case "SomeOtherMethod":
                break;
            default:
                break;
        }
        return true;
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
        getServletContext().getRequestDispatcher("/index.jsp").forward(request, 
                response);
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