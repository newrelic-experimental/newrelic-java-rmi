package com.newrelic.rmi.demo;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.Naming;
import java.rmi.RemoteException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/character-count")
public class CharacterCountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        // Get the text parameter from the request
        String text = request.getParameter("text");

        if (text == null || text.isEmpty()) {
            out.println("Error: missing text parameter");
            return;
        }

        try {
            // Lookup the remote CharacterCounter object
            CharacterCounter counter = (CharacterCounter) Naming.lookup("//localhost/CharacterCounter");

            // Call the remote method to count the characters
            int count = counter.countCharacters(text);

            // Send the character count as the response
            out.println("Character count: " + count);
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            response.setStatus(500);
        }
    }
}
