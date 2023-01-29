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
     * @throws IOException if forward to the JSP page failed.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        Integer anyMethodSelected = Integer.valueOf(request.getParameter("sa"));
        Integer uploadSuccessful = Integer.valueOf(request.getParameter("su"));
        
        Object exception = session.getAttribute("GeneralApplicationException");
        
        if (anyMethodSelected == 0) {
            request.setAttribute("operationResultDesc", 
                    "No method was selected.");
        } else {
            if (uploadSuccessful > 0) {
                request.setAttribute("operationResultDesc",
                        "Records were added to the database");
            }
            if (exception != null) {
                request.setAttribute("operationResultDesc", 
                        "Operation was unsuccessful: " + exception);
            }
        }
        
        // Removing exception attribute for the next request.
        session.removeAttribute("GeneralApplicationException");
        
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
        return "Sevlet to display the page with the result after the operation "
                + "was performed by another servlet.";
    }// </editor-fold>

}
