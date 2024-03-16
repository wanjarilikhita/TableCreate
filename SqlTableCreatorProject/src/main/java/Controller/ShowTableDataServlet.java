package Controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/ShowTableDataServlet")
public class ShowTableDataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String url = "jdbc:mysql://localhost:3306/servlet";
        String user = "root";
        String password = "";

        String tableName = request.getParameter("tableName");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet columns = meta.getColumns(null, null, tableName, null);
            List<String> columnNames = new ArrayList<>();
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                columnNames.add(columnName);
            }
            out.println("<html><body>");
            out.println("<h2>Table: " + tableName + "</h2>");
            out.println("<table border='1'>");
            out.println("<tr>");
            for (String columnName : columnNames) {
                out.println("<th>" + columnName + "</th>");
            }
            out.println("</tr>");
            Statement stmt = conn.createStatement();
            ResultSet data = stmt.executeQuery("SELECT * FROM " + tableName);
            while (data.next()) {
                out.println("<tr>");
                for (String columnName : columnNames) {
                    out.println("<td>" + data.getString(columnName) + "</td>");
                }
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("<h2>Insert Data</h2>");
            out.println("<form method='post'>");
            for (String columnName : columnNames) {
                out.println(columnName + ": <input type='text' name='" + columnName + "'><br>");
            }
            out.println("<input type='hidden' name='tableName' value='" + tableName + "'>");
            out.println("<input type='submit' value='Insert'>");
            out.println("<a href=index.html>Home Page</a>");
            out.println("</form>");

            out.println("</body></html>");
            columns.close();
            data.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            PrintWriter writer = response.getWriter();
            writer.println("Error: " + e.getMessage());
            e.printStackTrace(writer);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = "jdbc:mysql://localhost:3306/servlet";
        String user = "root";
        String password = "";

        String tableName = request.getParameter("tableName");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet columns = meta.getColumns(null, null, tableName, null);
            List<String> columnNames = new ArrayList<>();
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                columnNames.add(columnName);
            }
            StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
            for (String columnName : columnNames) {
                query.append(columnName).append(",");
            }
            query.deleteCharAt(query.length() - 1); 
            query.append(") VALUES (");
            for (String columnName : columnNames) {
                String columnValue = request.getParameter(columnName);
                query.append("'").append(columnValue).append("',");
            }
            query.deleteCharAt(query.length() - 1);
            query.append(")");

            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query.toString());
            columns.close();
            stmt.close();
            conn.close();
            response.sendRedirect("ShowTableDataServlet?tableName=" + tableName);
        } catch(ClassNotFoundException | SQLException e) {
            PrintWriter writer = response.getWriter();
            writer.println("Error: " + e.getMessage());
            e.printStackTrace(writer);
        }
    }
}
