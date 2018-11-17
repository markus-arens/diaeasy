package com.example.markus.diaeasy;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoryEntry {

    private static final String LOG_TAG = DatabaseDataSource.class.getSimpleName();


    public static final String HISTORY_TABLE = "history";
    public static final String HISTORY_TABLE_LAST = "history_last";

    public static final String HISTORY_COL_TIMESTAMP        = "tbl_timestamp";
    public static final String HISTORY_COL_ID               = "id";
    public static final String HISTORY_COL_BLUTZUCKER       = "blutzucker";
    public static final String HISTORY_COL_KORREKTURFAKTOR  = "korrekturfaktor";
    public static final String HISTORY_COL_KE               = "ke";
    public static final String HISTORY_COL_KEFFAKTOR        = "kefaktor";
    public static final String HISTORY_COL_ZIELWERT_MAX     = "zielwert_max";
    public static final String HISTORY_COL_ZIELWERT_MIN     = "zielwert_min";
    public static final String HISTORY_COL_BOLUS            = "bolus";
    public static final String HISTORY_COL_NOTIZ            = "notiz";


    private static final String HISTORY_COL_CREBY= "creby";
    private static final String HISTORY_COL_CREON= "creon";
    private static final String HISTORY_COL_UPDBY= "updby";
    private static final String HISTORY_COL_UPDON= "updon";


    public static final String[] HISTORY_SETUP = {
            "drop table if exists "+ HISTORY_TABLE +";"
            ,"create table "+ HISTORY_TABLE +" ("
            + HISTORY_COL_ID +        " INTEGER PRIMARY KEY AUTOINCREMENT  "
            + ", " + HISTORY_COL_TIMESTAMP + " timestamp "
            + ", " + HISTORY_COL_BLUTZUCKER + " integer"
            + ", " + HISTORY_COL_KORREKTURFAKTOR + " integer "
            + ", " + HISTORY_COL_KE + " decimal(10,4)"
            + ", " + HISTORY_COL_KEFFAKTOR + " decimal(10,4) "
            + ", " + HISTORY_COL_ZIELWERT_MAX + " integer "
            + ", " + HISTORY_COL_ZIELWERT_MIN + " integer "
            + ", " + HISTORY_COL_BOLUS + " decimal (10, 4) "
            + ", " + HISTORY_COL_NOTIZ + " varchar (10000) "
            + ", " + HISTORY_COL_CREBY + " varchar(255)"
            + ", " + HISTORY_COL_CREON + " timestamp DEFAULT CURRENT_TIMESTAMP"
            + ", " + HISTORY_COL_UPDBY + " varchar(255)"
            + ", " + HISTORY_COL_UPDON + " timestamp DEFAULT CURRENT_TIMESTAMP"
            + ");"
            ,"drop view if exists "+ HISTORY_TABLE_LAST +";"
            ,"create view "+ HISTORY_TABLE_LAST +" as  select * from "+ HISTORY_TABLE_LAST
            +" where "+HISTORY_COL_TIMESTAMP+" in          (select max("+HISTORY_COL_TIMESTAMP+")  from "+HISTORY_TABLE+"  );"

    };

    private long histId;
    private Date histTimestamp;
    private int histBlutzucker, histKorrekturfaktor, histZielwertMax, histZielwertMin;
    private double histKeFaktor, histBolus, histKe;
    private String histNotiz;


   public HistoryEntry(
            long histId
            , Date histTimestamp
            , int histBlutzucker
            , double histKe
            , double histBolus
            , String histNotiz
            , int histKorrekturfaktor
            , int histZielwertMax
            , int histZielwertMin
            , double histKeFaktor
    ) {
        this.histId = histId;
        this.histTimestamp = histTimestamp;
        this.histBlutzucker = histBlutzucker;
        this.histKe = histKe;
        this.histBolus = histBolus;
        this.histNotiz = histNotiz;
        this.histKorrekturfaktor = histKorrekturfaktor;
        this.histZielwertMax = histZielwertMax;
        this.histZielwertMin = histZielwertMin;
        this.histKeFaktor = histKeFaktor;
    }

    public long getId() {
        return histId;
    }

    public String exportFormat(String separator) {
        StringBuffer strbuf = new StringBuffer();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        strbuf.append(df.format(histTimestamp));
        strbuf.append(separator+ histBlutzucker);
        strbuf.append(separator+ histKe);
        strbuf.append(separator+ histBolus);
        strbuf.append(separator+ histNotiz.substring(0, Math.min(histNotiz.length(), 20)) + "..");


        return strbuf.toString();
        //return "Blutzucker: " + histBlutzucker + "\nKE:" + histKe + "\nTimestamp:" + histTimestamp.toString();
    }


    public String toString() {
        StringBuffer strbuf = new StringBuffer();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        strbuf.append(df.format(histTimestamp));
        strbuf.append("\n");
        strbuf.append(" " + histBlutzucker);
        strbuf.append(" | " + histKe);
        strbuf.append(" | " + histBolus);
        strbuf.append(" | " + histNotiz.substring(0, Math.min(histNotiz.length(), 20)) + "..");


        return strbuf.toString();
       //return "Blutzucker: " + histBlutzucker + "\nKE:" + histKe + "\nTimestamp:" + histTimestamp.toString();
    }



}

