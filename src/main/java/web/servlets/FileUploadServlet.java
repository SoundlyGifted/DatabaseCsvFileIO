package web.servlets;

import java.io.IOException;
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
import jakarta.servlet.http.HttpSession;
import web.process.database.DatabaseHandlerLocal;
import web.process.csvData.CSVFileData;
import web.exceptions.GeneralApplicationException;
import web.process.parse.AppCSVParser;

/**
 * This Servlet handles ".csv" file upload to Database requests based on 
 * different file parsing methods selected.
 * 
 * @author SoundlyGifted
 */
@WebServlet(name = "FileUploadServlet", urlPatterns = {"/upload.do"})
@MultipartConfig(maxFileSize = 16177215)
public class FileUploadServlet extends HttpServlet {

    @EJB
    private DatabaseHandlerLocal databaseHandler;
    
    @EJB
    private AppCSVParser appCSVParser;
    
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

        /* csv-file received from request as a part of "multipart/form-data" 
         * POST request. 
         */
        Part filePart = request.getPart("file");
        
        int uploadSuccessful = 0;
        HttpSession session = request.getSession();
        try {
            // Getting selected parsing method from the Request.
            String parsingMethodSelected = defineParsingMethodSelected(request);
            // Parsing csv-file using the selected method.
            CSVFileData csvFileData = parseCSVFile(filePart, parsingMethodSelected);
            // Uploading the parsed data into the database.
            if (uploadCSVDataToDB(csvFileData)) {
                System.out.println("*** [FileUploadServlet] File Successfully "
                        + "uploaded using '" + parsingMethodSelected + "' ***");
                uploadSuccessful = 1;
            } else {
                System.out.println("*** [FileUploadServlet] File was not uploaded, "
                        + "attempted to use '" + parsingMethodSelected + "' ***");
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
        response.sendRedirect("display.do?su=" + uploadSuccessful);
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
    
    
    private CSVFileData parseCSVFile (Part filePart, String parsingMethodSelected) 
            throws GeneralApplicationException, IOException {
        /* Collection to keep records from csv-file.
         * Each record is a Map with a csv table values mapped to 
         * the csv table headers (Map<String, String>).
         */
        CSVFileData csvFileData;

        if (filePart != null) {
            String fileName = filePart.getSubmittedFileName();
            if (!fileName.trim().isEmpty()) {
                System.out
                        .println("*** [FileUploadServlet.uploadFileToDB] File "
                                + "name = " + fileName + " ***");
                if (fileName.endsWith(".csv")) {
                    switch (parsingMethodSelected) {
                        case "CommonsCSV":
                            csvFileData = appCSVParser
                                    .parseWithCommonsCSV(filePart);
                            break;
                        case "OpenCSV":
                            csvFileData = appCSVParser
                                    .parseWithOpenCSV(filePart);
                            break;
                        default:
                            csvFileData = new CSVFileData();
                            break;
                    }
                    return csvFileData;
                } else {
                    System.out.println("*** [FileUploadServlet.uploadFileToDB] "
                            + "This is not a CSV file ***");
                    throw new GeneralApplicationException("The selected "
                                    + "file is not a CSV file.");
                }
            } else {
                System.out.println("*** [FileUploadServlet.uploadFileToDB] "
                        + "No file selected ***");
                throw new GeneralApplicationException("No file selected.");
            }
        }
        return new CSVFileData();
    }
    

    private boolean uploadCSVDataToDB(CSVFileData csvFileData) 
            throws GeneralApplicationException {
        if (csvFileData.getRecordListWithCSVFileHeaders().isEmpty()) {
            System.out.println("*** "
                    + "[FileUploadServlet.uploadFileToDB] "
                    + "This CSV file contains no data ***");
            return false;           
        }
        return databaseHandler.insertMultRecs(csvFileData);
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
