package com.example.markus.diaeasy;

public class Basalrate {
    public int BasalTime;
    public double BasalRate;

    public static final String BASAL_TABLE = "basalrate";
    public static final String BASAL_CURRENT = "basalrate_current";

    public static final String BASAL_COL_BASALTIMEFROM = "basaltimefrom";
    public static final String BASAL_COL_BASALRATE = "basalrate";


    public static final String[] BASAL_SETUP = {
            "drop table if exists basalrate;"
            ,"create table basalrate (basaltimefrom int primary key, basalrate decimal(10,4));"
            ,"drop view if exists basalrate_current;"
            ,"create view basalrate_current as  select * from basalrate  where basaltimefrom in          (select max(basaltimefrom)  from basalrate          where basaltimefrom <=                 cast(strftime('%H', 'now', 'localtime') as int) * 60 + cast(strftime('%M', 'now', 'localtime') as int)         );"
            ,"insert	into	basalrate	values	(	0	    ,	0.275	)	;"
            ,"insert	into	basalrate	values	(	60	    ,	0.250	)	;"
            ,"insert	into	basalrate	values	(	120	    ,	0.225	)	;"
            ,"insert	into	basalrate	values	(	180	    ,	0.250	)	;"
            ,"insert	into	basalrate	values	(	240	    ,	0.250	)	;"
            ,"insert	into	basalrate	values	(	300	    ,	0.250	)	;"
            ,"insert	into	basalrate	values	(	360	    ,	0.250	)	;"
            ,"insert	into	basalrate	values	(	420	    ,	0.250	)	;"
            ,"insert	into	basalrate	values	(	480	    ,	0.250	)	;"
            ,"insert	into	basalrate	values	(	540	    ,	0.225	)	;"
            ,"insert	into	basalrate	values	(	600	    ,	0.200	)	;"
            ,"insert	into	basalrate	values	(	660	    ,	0.175	)	;"
            ,"insert	into	basalrate	values	(	720	    ,	0.175	)	;"
            ,"insert	into	basalrate	values	(	780	    ,	0.200	)	;"
            ,"insert	into	basalrate	values	(	840	    ,	0.225	)	;"
            ,"insert	into	basalrate	values	(	900	    ,	0.250	)	;"
            ,"insert	into	basalrate	values	(	960 	,	0.275	)	;"
            ,"insert	into	basalrate	values	(	1020	,	0.300	)	;"
            ,"insert	into	basalrate	values	(	1080	,	0.325	)	;"
            ,"insert	into	basalrate	values	(	1140	,	0.350	)	;"
            ,"insert	into	basalrate	values	(	1200	,	0.400	)	;"
            ,"insert	into	basalrate	values	(	1260	,	0.400	)	;"
            ,"insert	into	basalrate	values	(	1320	,	0.375	)	;"
            ,"insert	into	basalrate	values	(	1380	,	0.350	)	;"
    };
}
