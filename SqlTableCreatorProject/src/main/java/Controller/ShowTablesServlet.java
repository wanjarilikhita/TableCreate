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

@WebServlet("/ShowTablesServlet")
public class ShowTablesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String url = "jdbc:mysql://localhost:3306/servlet"; // Change this to your database URL
        String user = "root"; // Change this to your database username
        String password = ""; // Change this to your database password

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);

            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables("servlet", null, "%", new String[] {"TABLE"});

            out.println("<html><body>");
            out.println("<h2>List of Tables in 'servlet' Database</h2>");
            out.println("<ul>");
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                out.println("<li><a href='ShowTableDataServlet?tableName=" + tableName + "'>" + tableName + "</a></li>");
            }
            out.println("</ul>");
            out.println("</body></html>");

            tables.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}