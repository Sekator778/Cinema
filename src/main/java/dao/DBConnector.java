package dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.*;

/**
 * connector to DB
 */

public class DBConnector {
    /**
     * logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(DBConnector.class);
    /**
     * connection to DB from app.properties files
     */
    private Connection connection;
    /**
     * instance class
     */
    private final static DBConnector INSTANCE = new DBConnector();

    /**
     * constructor connection to psql DB cinema
     */
    private DBConnector() {
        init();
    }

    /**
     * getter for instance class
     *
     * @return - instance
     */
    public static DBConnector getINSTANCE() {
        return INSTANCE;
    }

    private void init() {
        try (InputStream in = DBConnector.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * method read data from table hall
     *
     * @param hallId - hallId
     * @return set with places
     */
    public Map<Integer, Boolean> getHallSchema(int hallId) {
        Map<Integer, Boolean> occupiedPlace = new HashMap<>();
        String query = "select * from halls where hall_id = ?;";
        try (
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setInt(1, hallId);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                int row = set.getInt("row");
                int place = set.getInt("place");
                int account = set.getInt("account_id");
                int sitNumber = row * 10 + place;
                if (account > 0) {
                    occupiedPlace.put(sitNumber, true);
                } else {
                    occupiedPlace.put(sitNumber, false);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return occupiedPlace;
    }

    /**
     * work transaction to DB with jdbc
     * 1. find user
     * 2. if user not present - addUser
     * 3. booking current place
     * 4. if check dta -1 method clear place
     *
     * @param userName  - customer name
     * @param userPhone - customer phone
     * @param hall      - id hall cinema
     * @param row       - row number
     * @param place     - place number
     * @return true if booking place
     */
    public boolean doTransaction(String userName, int userPhone, int hall, int row, int place) {
        boolean result = false;
        int accountId = -1;
        String queryFindAccount = "select * from accounts where name = ? and phone = ?;";
        String queryAddAccount = "insert into accounts (name, phone) values (?, ?);";
        String queryBookPlace = "update halls set account_id = ? where hall_id = ? and row = ? and place = ?";
        String queryClearPlace = "update halls set account_id = ? where account_id = ? and hall_id = ?";
        String queryClearAccount = "delete from accounts where account_id = ?";
        PreparedStatement statement = null;
        ResultSet set;
        try {
            LOGGER.info("=======DATABASE CONNECTION OPEN=======");
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(queryFindAccount);
            statement.setString(1, userName);
            statement.setInt(2, userPhone);
            set = statement.executeQuery();
            while (set.next()) {
                accountId = set.getInt(1);
            }
            if (accountId == -1) {
                statement = connection.prepareStatement(queryAddAccount, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, userName);
                statement.setInt(2, userPhone);
                statement.executeUpdate();
                set = statement.getGeneratedKeys();
                while (set.next()) {
                    accountId = set.getInt(1);
                }
            }
            if ((row == -1) && (place == -1)) {
                statement = connection.prepareStatement(queryClearPlace);
                statement.setInt(1, -1);
                statement.setInt(2, accountId);
                statement.setInt(3, hall);
                statement.executeUpdate();
                //clear account
                statement = connection.prepareStatement(queryClearAccount);
                statement.setInt(1, accountId);

            } else {
                statement = connection.prepareStatement(queryBookPlace);
                statement.setInt(1, accountId);
                statement.setInt(2, hall);
                statement.setInt(3, row);
                statement.setInt(4, place);
            }
            statement.executeUpdate();
            connection.commit();
            result = true;

        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    LOGGER.info("=======!Db Exception! Rolling Back Data=======");
                } catch (SQLException exception) {
                    LOGGER.error(exception.getMessage(), exception);
                }
            }
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            LOGGER.info("=======DATABASE CONNECTION CLOSED=======");
        }
        return result;
    }
}
