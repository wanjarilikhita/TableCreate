package Controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/CreateTableServlet")
public class CreateTableServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String tableName = request.getParameter("table");
        String url = "jdbc:mysql://localhost:3306/servlet";
        String user = "root";
        String password = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(url, user, password);

            StringBuilder query = new StringBuilder("CREATE TABLE ");
            query.append(tableName).append(" (");

            String[] columnNames = request.getParameterValues("name");
            String[] dataTypes = request.getParameterValues("datatype");
            String[] lengths = request.getParameterValues("len");
            String[] defaults = request.getParameterValues("default");

            for (int i = 0; i < columnNames.length; i++) {
                if (!columnNames[i].isEmpty() && !dataTypes[i].isEmpty()) {
                    query.append(columnNames[i]).append(" ").append(dataTypes[i]);
                    if (dataTypes[i].equals("varchar") || dataTypes[i].equals("char")) {
                        if (!lengths[i].isEmpty()) {
                            query.append("(").append(lengths[i]).append(")");
                        }
                    }
                    if (defaults[i] != null && !defaults[i].isEmpty()) {
                        query.append(" DEFAULT '").append(defaults[i]).append("'");
                    }
                    query.append(",");
                }
            }
            query.deleteCharAt(query.length() - 1);
            query.append(")");

            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query.toString());

            out.println("<html><body>");
            out.println("<h3>Table " + tableName + " created successfully!</h3>");
            out.println("</body></html>");

            stmt.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            out.println("Error: MySQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            out.println("Error executing SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
