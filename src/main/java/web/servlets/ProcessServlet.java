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
import java.sql.SQLException;
import javax.naming.OperationNotSupportedException;
import web.process.parse.exceptions.FileValidationException;
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
            } catch (OperationNotSupportedException
                    | FileValidationException
                    | SQLException
                    | IOException
                    | NumberFormatException
                    | CsvValidationException e) {
                session = request.getSession();
                session.setAttribute("GeneralApplicationException",
                        e.getMessage());
            }
            uploadSuccessful = 1;
            postRedirectGet(response, anyMethodSelected, uploadSuccessful);
        }
        
        if (clickedDownload != null) {
            try {
                if (!selectedMethod.isEmpty()) {
                    // Getting the records from the database.
                    CSVFileData csvFileData = databaseHandler.selectAll();

                    String downloadFileName = "content.csv";
                    // Force the server to download a csv-file.
                    /* Setting proper response header to inform the client that 
                     * the content is not meant to be displayed.
                     * The "Content-Disposition" header is used for this purpose,
                     * it can be interpreted by HTTP clients like web browsers.
                     */
                    response.setContentType("application/octet-stream");
                    String headerName = "Content-Disposition";
                    /* Specifying the disposition type.
                     * 1) inline -  The body part is intended to be displayed 
                     * automatically when the message content is displayed.
                     * 2) attachment -  The body part is separate from the main 
                     * content of the message and should not be displayed 
                     * automatically except when prompted by the user.
                     */
                    String headerValue 
                            = String.format("attachment; filename=\"%s\"", 
                                    downloadFileName);
                    response.setHeader(headerName, headerValue);
                    /* Downloading the data into the csv-file using the selected 
                     * download method.
                     */
                    downloadFileFromDB(csvFileData, response, selectedMethod);
                } else {
                    postRedirectGet(response, anyMethodSelected, uploadSuccessful);
                }
            } catch (OperationNotSupportedException|IOException|SQLException e) {
                session = request.getSession();
                session.setAttribute("GeneralApplicationException", e.getMessage());
                postRedirectGet(response, anyMethodSelected, uploadSuccessful);
            }
        }
        
        if (clickedClear != null) {
            try {
                databaseHandler.deleteAll();
            } catch (IOException|SQLException e) {
                session = request.getSession();
                session.setAttribute("GeneralApplicationException", e.getMessage());
            }
            postRedirectGet(response, anyMethodSelected, uploadSuccessful);
        }
    }

    
    private CSVFileData parseCSVFile (Part filePart, String parsingMethodSelected) 
            throws IOException, CsvValidationException, FileValidationException,
            OperationNotSupportedException {
        /* CSVFileData is a Collection to keep records from csv-file.
         * Each record is a Map with a csv table values mapped to 
         * the csv table headers (Map<String, String>).
         */
        switch (parsingMethodSelected) {
            case "CommonsCSV":
                return appCSVParser.parseWithCommonsCSV(filePart);
            case "OpenCSV":
                return appCSVParser.parseWithOpenCSV(filePart);
            default:
                throw new OperationNotSupportedException("Provided parsing "
                        + "method is not supported.");
        }
    }
    
    
    private void uploadCSVDataToDB(CSVFileData csvFileData) 
            throws IOException, SQLException, NumberFormatException {      
        databaseHandler.insertMultRecs(csvFileData);
    }
    
    
    private void downloadFileFromDB(CSVFileData csvFileData, 
            HttpServletResponse response, String downloadMethodSelected) 
            throws IOException, OperationNotSupportedException {
        switch (downloadMethodSelected) {
            case "CommonsCSV":
                appCSVWriter.writeWithCommonsCSV(csvFileData, response);
                break;
            case "OpenCSV":
                appCSVWriter.writeWithOpenCSV(csvFileData, response);
                break;
            default:
                throw new OperationNotSupportedException("Provided download "
                        + "method is not supported.");
        }
    }
    
    private void postRedirectGet(HttpServletResponse response, int ... params) 
            throws IOException {
        /* Using PRG (Post-Redirect-Get) pattern.
         * Instead of forwarding from doPost() method redirecting to the doGet()
         * method of another servlet (display servlet).
         * This is needed to avoid duplicate data submission when user
         * refreshes the page.
         */
        response.sendRedirect("display.do?sa=" + params[0]
                + "&su=" + params[1]);
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
