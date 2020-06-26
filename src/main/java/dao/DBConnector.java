package dao;

import model.HallPlace;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * connector to DB
 */

public class DBConnector {
    /**
     * logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(DBConnector.class);
    /**
     * pool connection
     */
    private static final BasicDataSource SOURCE = new BasicDataSource();
    /**
     * instance class
     */
    private final static DBConnector INSTANCE = new DBConnector();

    /**
     * constructor connection to psql DB cinema
     */
    private DBConnector() {
        SOURCE.setUrl("jdbc:postgresql://localhost:5432/postgres");
        SOURCE.setUsername("postgres");
        SOURCE.setPassword("password");
        SOURCE.setMinIdle(5);
        SOURCE.setMaxIdle(10);
        SOURCE.setMaxOpenPreparedStatements(100);
        SOURCE.setDriverClassName("org.postgresql.Driver");
    }

    /**
     * getter for instance class
     *
     * @return - instance
     */
    public static DBConnector getINSTANCE() {
        return INSTANCE;
    }

    /**
     * method read data from table hall
     *
     * @param hallId - hallId
     * @return set with places
     */
    public Set<HallPlace> getHallSchema(int hallId) {
        Set<HallPlace> hall = new HashSet<>();
        String query = "select * from halls where hall_id = ?;";
        try (Connection connection = SOURCE.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setInt(1, hallId);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                int row = set.getInt("row");
                int place = set.getInt("place");
                int account = set.getInt("account_id");
                hall.add(new HallPlace(row, place, account));
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return hall;
    }

    /**
     * work transaction to DB with jdbc
     * 1. find user
     * 2. if user not present - addUser
     * 3. booking current place
     * @param userName - customer name
     * @param userPhone - customer phone
     * @param hall - id hall cinema
     * @param row - row number
     * @param place - place number
     * @return true if booking place
     */
    public boolean doTransaction(String userName, int userPhone, int hall, int row, int place) {
        boolean result = false;
        int accountId = -1;
        String queryFindAccount = "select * from accounts where name = ? and phone = ?;";
        String queryAddAccount = "insert into accounts (name, phone) values (?, ?);";
        String queryBookPlace = "update halls set account_id = ? where hall_id = ? and row = ? and place = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet set = null;
        try {
            connection = SOURCE.getConnection();
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
            statement = connection.prepareStatement(queryBookPlace);
            statement.setInt(1, accountId);
            statement.setInt(2, hall);
            statement.setInt(3, row);
            statement.setInt(4, place);
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
