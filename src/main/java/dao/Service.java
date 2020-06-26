package dao;

import java.util.Map;

public interface Service {
    Map<Integer, Boolean> getHallSchema(int hallId);
    boolean doPayment(String name, String phone, String hole, String row, String place);
}
