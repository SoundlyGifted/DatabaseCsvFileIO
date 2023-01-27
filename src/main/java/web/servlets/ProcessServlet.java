package web.servlets;

import com.opencsv.exceptions.CsvValidationException;
import jakarta.ejb.EJB;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.sql.SQLException;
import web.exceptions.GeneralApplicationException;
import web.process.csvData.CSVFileData;
import web.process.download.AppCSVWriterLocal;
import web.process.parse.AppCSVParserLocal;
import web.process.database.DBDataHandlerLocal;

/**
 * Sevlet to process the submitted input data from the JSP page.
 * 
 * @author SoundlyGifted
 */
@WebServlet(name = "ProcessServlet", urlPatterns = {"/process.do"})
@MultipartConfig(maxFileSize = 16177215)
public class ProcessServlet extends HttpServlet {

    @EJB
    private DBDataHandlerLocal databaseHandler;

    @EJB
    private AppCSVParserLocal appCSVParser;    
    
    @EJB
    private AppCSVWriterLocal appCSVWriter;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws java.io.UnsupportedEncodingException if the character encoding is 
     * not supported.
     * @throws java.io.IOException if redirect to display servlet failed.
     */
    protected void processRequest(HttpServletRequest request, 
            HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String clickedUpload = request.getParameter("clicked_Upload");
        String clickedDownload = request.getParameter("clicked_Download");
        String selectedMethod = request.getParameter("selected_method");
        
        String clickedClear = request.getParameter("clicked_Clear");

        int anyMethodSelected = 0;
        int uploadSuccessful = 0;        
        int downloadSuccessful = 0;

        if (!(selectedMethod == null || selectedMethod.isEmpty())) {
            anyMethodSelected = 1;
        } else {
            selectedMethod = "";
        }
        
        HttpSession session;
        
        if (clickedUpload != null) {
            /* csv-file received from request as a part of "multipart/form-data" 
             * POST request. 
             */
            Part filePart = request.getPart("file");
            try {
                // Parsing csv-file using the selected method.
                CSVFileData csvFileData
                        = parseCSVFile(filePart, selectedMethod);
                // Uploading the parsed data into the database.
                uploadCSVDataToDB(csvFileData);
            } catch (GeneralApplicationException
                    | SQLException
                    | IOException
                    | NumberFormatException
                    |CsvValidationException e) {
                session = request.getSession();
                session.setAttribute("GeneralApplicationException",
                        e.getMessage());
            }
            uploadSuccessful = 1;
        }
        
        if (clickedDownload != null) {
            
            String downloadFileName = "myDataDownload";
            String home = System.getProperty("user.home");
            File outputFile = new File(home + "/Downloads/" + downloadFileName 
                    + ".csv");

            try {
                // Getting the records from the database.
                CSVFileData csvFileData = databaseHandler.selectAll();
                /* Downloading the data into the csv-file using the selected 
                 * download method.
                 */
                downloadFileFromDB(csvFileData, outputFile, selectedMethod);
            } catch (IOException|SQLException e) {
                session = request.getSession();
                session.setAttribute("GeneralApplicationException", e.getMessage());
            }
            downloadSuccessful = 1;
        }
        
        if (clickedClear != null) {
            try {
                databaseHandler.deleteAll();
            } catch (IOException|SQLException e) {
                session = request.getSession();
                session.setAttribute("GeneralApplicationException", e.getMessage());
            }
        }
        /* Using PRG (Post-Redirect-Get) pattern.
         * Instead of forwarding from doPost() method redirecting to the doGet()
         * method of another servlet (display servlet).
         * This is needed to avoid duplicate data submission when user
         * refreshes the page.        
         */
        response.sendRedirect("display.do?sa=" + anyMethodSelected 
                + "&su=" + uploadSuccessful 
                + "&sd=" + downloadSuccessful);
    }

    
    private CSVFileData parseCSVFile (Part filePart, String parsingMethodSelected) 
            throws GeneralApplicationException, IOException, CsvValidationException {
        /* Collection to keep records from csv-file.
         * Each record is a Map with a csv table values mapped to 
         * the csv table headers (Map<String, String>).
         */
        CSVFileData csvFileData;

        if (filePart != null) {
            String fileName = filePart.getSubmittedFileName();
            if (!fileName.trim().isEmpty()) {
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
                    throw new GeneralApplicationException("The selected "
                                    + "file is not a CSV file.");
                }
            } else {
                throw new GeneralApplicationException("No file selected.");
            }
        }
        return new CSVFileData();
    }
    

    private void uploadCSVDataToDB(CSVFileData csvFileData) 
            throws IOException, SQLException, NumberFormatException {
        databaseHandler.insertMultRecs(csvFileData);
    }
    
    
    private void downloadFileFromDB(CSVFileData csvFileData, File outputFile, 
            String downloadMethodSelected) throws IOException {
        switch (downloadMethodSelected) {
            case "CommonsCSV":
                appCSVWriter.writeWithCommonsCSV(csvFileData, outputFile);
            case "OpenCSV":
                appCSVWriter.writeWithOpenCSV(csvFileData, outputFile);
        }
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
        return "Sevlet to process the submitted input data from the JSP page.";
    }// </editor-fold>

}
