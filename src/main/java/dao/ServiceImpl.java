package dao;


import model.HallPlace;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */

public class ServiceImpl implements Service {
    /**
     * instance ServiceImpl class
     */
    private static final ServiceImpl INSTANCE = new ServiceImpl();
    /**
     * instance connection to DB
     */
    private final DBConnector dbCon = DBConnector.getINSTANCE();

    /**
     * constructor
     */
    public ServiceImpl() {
    }

    /**
     * getter for instance class
     *
     * @return instance
     */
    public static ServiceImpl getINSTANCE() {
        return INSTANCE;
    }

    /**
     * return hallSchema
     *
     * @param hallId the hall id
     * @return - the map for places and their status
     */
    @Override
    public Map<Integer, Boolean> getHallSchema(int hallId) {
        Map<Integer, Boolean> occupiedPlace = new HashMap<>();
        Set<HallPlace> hall = dbCon.getHallSchema(hallId);
        for (HallPlace hallPlace : hall
        ) {
            int row = hallPlace.getRow();
            int place = hallPlace.getPlace();
            int accountId = hallPlace.getAccountId();
            int sitNumber = row * 10 + place;
            if (accountId > 0) {
                occupiedPlace.put(sitNumber, true);
            } else {
                occupiedPlace.put(sitNumber, false);
            }
        }
        return occupiedPlace;
    }

    @Override
    public boolean doPayment(String name, String phone, String hall, String row, String place) {
        int userPhone = Integer.parseInt(phone);
        int holeId = Integer.parseInt(hall);
        int rowNum = Integer.parseInt(row);
        int placeNum = Integer.parseInt(place);
        return dbCon.doTransaction(name, userPhone, holeId, rowNum, placeNum);
    }

}
