package com.example.markus.diaeasy;

public class KEFaktor {

    public static final String KEFAKTOR_TABLE = "kefaktor";
    public static final String KEFAKTOR_TABLE_CURRENT = "kefaktor_current";

    public static final String KEFAKTOR_TIME= "ketimefrom";

    public static final String KEFAKTOR_COL_ID = "id";
    public static final String KEFAKTOR_COL_KEFTIMEFROM = "ketimefrom";
    public static final String KEFAKTOR_COL_KEFFAKTOR = "kefaktor";

    public static final String[] KEFAKTOR_SETUP = {
            "drop table if exists kefaktor;"
            ,"create table kefaktor (id INTEGER PRIMARY KEY AUTOINCREMENT,  ketimefrom integer, kefaktor decimal(10,4));"
            ,"drop view if exists kefaktor_current;"
            ,"create view kefaktor_current as  select * from kefaktor where ketimefrom in          (select max(ketimefrom)  from kefaktor         where ketimefrom <=                 cast(strftime('%H', 'now', 'localtime') as int) * 60 + cast(strftime('%M', 'now', 'localtime') as int)         );"
            ,"insert into kefaktor (ketimefrom, kefaktor) values (  0 , 0.75 ) ;"
            ,"insert into kefaktor (ketimefrom, kefaktor) values (  390 , 1.25 ) ;"
            ,"insert into kefaktor (ketimefrom, kefaktor) values (  570 , 0.75 ) ;"
            ,"insert into kefaktor (ketimefrom, kefaktor) values (  690 , 1.00 ) ;"
            ,"insert into kefaktor (ketimefrom, kefaktor) values (  1050 , 0.85 ) ;"
            ,"insert into kefaktor (ketimefrom, kefaktor) values (  1290 , 0.75 ) ;"
    };

    private long KefId;
    private int KefTime;
    private double KefFaktor;


    public KEFaktor(int KefTime , double KefFaktor, long KefId) {
        this.KefTime = KefTime;
        this.KefFaktor = KefFaktor;
        this.KefId = KefId;
    }

    public long getId() {
        return KefId;
    }

    public int getTime() {
        return KefTime;
    }

    public double getFaktor() {
        return KefFaktor;
    }

    public void setTime(int Time_neu) {
        this.KefTime  = Time_neu;
    }

    public void setFaktor(int Faktor_neu) {
        this.KefFaktor = Faktor_neu;
    }

    private String getTimeAsTime() {
        int hours = getTime() / 60;
        int minutes = getTime() % 60;

        return String.format("%02d:%02d", hours, minutes);
    }

    public String toString() {
        return "ab " + getTimeAsTime() + " => " + getFaktor();
    }


}

