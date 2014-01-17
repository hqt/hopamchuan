package com.hac.android.model.json;

import java.util.Date;

/**
 * Store version detail
 */
public class DBVersion {
    public int no;
    public Date date;
    public int numbers;

    public DBVersion(int no, Date date, int numbers) {
        this.no = no;
        this.date = date;
        this.numbers = numbers;
    }

    @Override
    public String toString() {
        return "DBVersion{" +
                "no=" + no +
                ", date=" + date +
                ", numbers=" + numbers +
                '}';
    }
}
