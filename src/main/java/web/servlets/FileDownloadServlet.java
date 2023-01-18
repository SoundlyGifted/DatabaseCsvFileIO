package web.servlets;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import web.process.database.DatabaseHandlerLocal;
import web.process.csvData.CSVFileData;
import web.exceptions.GeneralApplicationException;
import web.process.download.AppCSVDownloader;

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
    
    @EJB
    private AppCSVDownloader appCSVDownloader;
    
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
            // Getting the records from the database.
            CSVFileData csvFileData = databaseHandler.selectAll();
            // Getting the selected download method from the Request.
            String downloadMethodSelected = defineMethodSelected(request);
            /* Downloading the data into the csv-file using the selected 
             * download method.
             */
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
        switch (downloadMethodSelected) {
            case "CommonsCSV":
                return appCSVDownloader
                        .downloadWithCommonsCSV(csvFileData, outputFile);
            case "OpenCSV":
                return appCSVDownloader
                        .downloadWithOpenCSV(csvFileData, outputFile);
            default:
                return false;
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
        return "Short description";
    }// </editor-fold>

}
