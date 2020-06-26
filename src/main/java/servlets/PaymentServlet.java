package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.Service;
import dao.ServiceImpl;
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
 * servlet for payment
 */

public class PaymentServlet extends HttpServlet {
    Service service = ServiceImpl.getINSTANCE();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        StringBuilder builder = new StringBuilder();
        reader.lines().forEach(builder::append);

        ObjectMapper mapper = new ObjectMapper();
        String json = builder.toString();
        HashMap map = mapper.readValue(json, HashMap.class);
        String name = (String) map.get("name");
        String phone = (String) map.get("phone");
        String hall = (String) map.get("hall");
        String row = (String) map.get("row");
        String place = (String) map.get("place");
        boolean result = service.doPayment(name, phone, hall, row, place);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = new PrintWriter(resp.getOutputStream());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);
        writer.append(jsonObject.toString());
        writer.flush();
    }
}
