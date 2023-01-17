package web.servlets;

import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Sevlet to display the page with the result using doGet() method after the 
 * operation was performed by another servlet. E.g. after uploading data to the 
 * database from a file or downloading data from database to a file.
 * 
 * This Servlet is the part of PRG (post-redirect-get) approach implementation.
 * It avoids duplicate data submission when user refreshes the page after
 * performing an operation that involves change to the database.
 * 
 * @author SoundlyGifted
 */
@WebServlet(name = "DisplayServlet", urlPatterns = {"/display.do"})
public class DisplayServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
        
        HttpSession session = request.getSession();
        Integer uploadSuccessful;
        try {
            uploadSuccessful = Integer.valueOf(request.getParameter("su"));
            if (uploadSuccessful > 0) {
                request.setAttribute("uploadResult",
                        "Records were added to the database");
            } else {
                request.setAttribute("uploadResult",
                        "Records were not added: " 
                                + session.getAttribute("GeneralApplicationException"));
            }
        } catch (NumberFormatException e) {
            uploadSuccessful = null;
        }
        
        Integer downloadSuccessful;
        try {
            downloadSuccessful = Integer.valueOf(request.getParameter("sd"));
            if (downloadSuccessful > 0) {
                request.setAttribute("downloadResult",
                        "Records were downloaded as a file into your downloads folder");
            } else {
                request.setAttribute("downloadResult",
                        "Records were not downloaded as a file: " 
                                + session.getAttribute("GeneralApplicationException"));
            }
        } catch (NumberFormatException e) {
            downloadSuccessful = null;
        }

        getServletContext().getRequestDispatcher("/index.jsp")
                .forward(request, response);
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
