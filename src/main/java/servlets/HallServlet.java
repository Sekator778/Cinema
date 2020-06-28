package servlets;

import dao.DBConnector;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 *
 */

public class HallServlet extends HttpServlet {
    private final DBConnector service = DBConnector.getINSTANCE();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int hallId = Integer.parseInt(req.getParameter("id"));
        Map<Integer, Boolean> schema = service.getHallSchema(hallId);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = new PrintWriter(resp.getOutputStream());
        JSONObject object = new JSONObject();
        for (Map.Entry<Integer, Boolean> entry : schema.entrySet()
             ) {
            object.put(entry.getKey().toString(), entry.getValue());
        }
        writer.append(object.toString());
        writer.flush();
    }
}
