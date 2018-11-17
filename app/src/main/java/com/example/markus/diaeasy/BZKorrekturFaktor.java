package com.example.markus.diaeasy;

public class BZKorrekturFaktor {
    public static final String BZKORREKTURFAKTOR_TABLE = "korrekturfaktor";
    public static final String BZKORREKTURFAKTOR_TABLE_CURRENT = "korrekturfaktor_current";

    public static final String BZKORREKTURFAKTOR_COL_ID = "id";
    public static final String BZKORREKTURFAKTOR_COL_TIMEFROM = "korrekturtimefrom";
    public static final String BZKORREKTURFAKTOR_COL_FAKTOR = "korrekturfaktor";

    public static final String[] BZKORREKTURFAKTOR_SETUP =
    {
        "drop table if exists korrekturfaktor ;"
        ,"create table korrekturfaktor (id INTEGER primary key autoincrement, korrekturtimefrom INTEGER, korrekturfaktor INTEGER);"
        ,"drop view if exists korrekturfaktor_current;"
        ,"create view korrekturfaktor_current as select * from korrekturfaktor where korrekturtimefrom in (select max(korrekturtimefrom)from korrekturfaktor                 where korrekturtimefrom <= cast(strftime('%H', 'now', 'localtime')as int) * 60 + cast(strftime('%M', 'now', 'localtime')as int) ); "
        ,"insert into korrekturfaktor (korrekturtimefrom, korrekturfaktor) values (0, 150);"
        ,"insert into korrekturfaktor (korrekturtimefrom, korrekturfaktor) values (390, 100);"
        ,"insert into korrekturfaktor (korrekturtimefrom, korrekturfaktor) values (570, 100);"
        ,"insert into korrekturfaktor (korrekturtimefrom, korrekturfaktor) values (690, 100);"
        ,"insert into korrekturfaktor (korrekturtimefrom, korrekturfaktor) values (1200, 150);"

    };

    private long BZKorrId;
    private int BZKorrTime;
    private int BZKorrFaktor;

    public BZKorrekturFaktor(int BZKorrTime , int BZKorrFaktor, long BZKorrId) {
        this.BZKorrTime = BZKorrTime;
        this.BZKorrFaktor = BZKorrFaktor;
        this.BZKorrId = BZKorrId;
    }

    public long getId() {
        return BZKorrId;
    }

    public int getBZKorrTime() {
        return BZKorrTime;
    }

    public int getBZKorrFaktor() {
        return BZKorrFaktor;
    }

    public void setBZKorrTime(int BZKorrTime_neu) {
        this.BZKorrTime  = BZKorrTime_neu;
    }

    public void setBZKorrFaktor(int BZKorrFaktor_neu) {
        this.BZKorrFaktor = BZKorrFaktor_neu;
    }

    private String getTimeAsTime() {
        int hours = getBZKorrTime() / 60;
        int minutes = getBZKorrTime() % 60;

        return String.format("%02d:%02d", hours, minutes);
    }

    public String toString() {
        return "ab " + getTimeAsTime() + " => " + getBZKorrFaktor();
    }



}
