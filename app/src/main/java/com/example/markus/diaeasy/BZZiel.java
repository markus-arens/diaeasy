package com.example.markus.diaeasy;

public class BZZiel {
    public int BZTime;
    public int BZZielMin;
    public int BZZielMax;

    public static final String BZZIEL_TABLE = "zielwerte";
    public static final String BZZIEL_CURRENT = "zielwerte_current";

    public static final String BZZIEL_COL_TIMEFROM = "zielwerttimefrom";
    public static final String BZZIEL_COL_MIN = "zielwertmin";
    public static final String BZZIEL_COL_MAX = "zielwertmax";

    public static final String[] BZZIEL_SETUP = {
            "drop table if exists zielwerte;"
            ,"create table zielwerte (zielwerttimefrom int primary key, zielwertmin int, zielwertmax int);"
            ,"drop view if exists zielwerte_current;"
            ,"create view zielwerte_current as  select * from zielwerte where zielwerttimefrom in (select max(zielwerttimefrom)  from zielwerte                where zielwerttimefrom <=                        cast(strftime('%H', 'now', 'localtime') as int) * 60 + cast(strftime('%M', 'now', 'localtime') as int)                );"
            ,"insert into zielwerte values (0, 90, 120);"
            ,"insert into zielwerte values (6*60, 100, 120);"
            ,"insert into zielwerte values (13*60, 80, 120);"
            ,"insert into zielwerte values (20*60, 90, 120);"  };
}
