package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DBConnector;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * servlet for payment or cancel booking
 */

public class BookingServlet extends HttpServlet {
    DBConnector service = DBConnector.getINSTANCE();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        StringBuilder builder = new StringBuilder();
        int row, place;
        reader.lines().forEach(builder::append);
        ObjectMapper mapper = new ObjectMapper();
        String json = builder.toString();
        HashMap map = mapper.readValue(json, HashMap.class);
        String name = (String) map.get("name");
        int phone = Integer.parseInt((String) map.get("phone"));
        int hall = Integer.parseInt((String) map.get("hall"));
        if (!map.containsKey("row")) {
            row = -1;
        } else {
            row = Integer.parseInt((String) map.get("row"));
        }
        if (!map.containsKey("place")) {
            place = -1;
        } else {
            place = Integer.parseInt((String) map.get("place"));
        }
        boolean result = service.doTransaction(name, phone, hall, row, place);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = new PrintWriter(resp.getOutputStream());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);
        writer.append(jsonObject.toString());
        writer.flush();
    }
}
