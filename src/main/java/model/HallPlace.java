package model;

/**
 * place to cinema hall
 * -1 place is free
 */

public class HallPlace {
    /**
     * number of row
     */
    private int row;
    /**
     * number place
     */
    private int place;
    /**
     * status
     */
    private int accountId;

    public HallPlace(int row, int place, int accountId) {
        this.row = row;
        this.place = place;
        this.accountId = accountId;
    }

    public int getRow() {
        return row;
    }

    public int getPlace() {
        return place;
    }

    public int getAccountId() {
        return accountId;
    }
}
