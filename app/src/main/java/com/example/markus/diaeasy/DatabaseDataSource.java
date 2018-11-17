package com.example.markus.diaeasy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseDataSource {

    private static final String LOG_TAG = DatabaseDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;


    public DatabaseDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();



        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    public boolean check_table_exists() {
        return dbHelper.check_table_exists(database, false);
    }

    public boolean reset_database() {
        Log.d(LOG_TAG, "ACHTUNG!! Daten werden auf Default zurückgesetzt!  " + database.getPath());
        return dbHelper.check_table_exists(database, true);
    }

    private String[] keFaktor_columns = {
            KEFaktor.KEFAKTOR_COL_ID,
            KEFaktor.KEFAKTOR_COL_KEFTIMEFROM,
            KEFaktor.KEFAKTOR_COL_KEFFAKTOR
    };

    public boolean createNewEntry(String obj_Class_Name, String timeValue_str, String value1_str, String value2_str) {


        String[] hourMin = timeValue_str.split(":");
        int hour = Integer.parseInt(hourMin[0]);
        int mins = Integer.parseInt(hourMin[1]);
        int hoursInMins = hour * 60;
        int timefrom =  hoursInMins + mins;

        if (BZKorrekturFaktor.class.getSimpleName().equals(obj_Class_Name )) {
            int faktor =  Integer.parseInt(value1_str);
            createBZKorrekturFaktor(timefrom, faktor);
        }
        else if (KEFaktor.class.getSimpleName().equals(obj_Class_Name)) {
            double faktor =  Double.parseDouble(value1_str);
            createKEFaktor(timefrom, faktor);
        }
        else
        {
            Log.d(LOG_TAG, "createNewEntry: Kein Gültiger Eintrag für (obj_Class_Name) = " + obj_Class_Name);
        }



        return true;
    }

    public void deleteDBEntry(Object obj) {
        if (obj instanceof Basalrate) {
            //TODO: Basalrate - Delete
        }
        else if (obj instanceof BZKorrekturFaktor) {
            deleteBZKorrekturFaktor((BZKorrekturFaktor) obj);
        }
        else if (obj instanceof  BZZiel) {
            //TODO: BZZiel - Delete
        }
        else if (obj instanceof  HistoryEntry) {
            deleteHistoryEntry((HistoryEntry) obj);
        }
        else if (obj instanceof  KEFaktor) {
            deleteKEFaktor((KEFaktor) obj);
        }
    }




    private KEFaktor cursorToKEFaktor(Cursor cursor) {
        int idKEFid = cursor.getColumnIndex(KEFaktor.KEFAKTOR_COL_ID);
        int idKEFTime = cursor.getColumnIndex(KEFaktor.KEFAKTOR_COL_KEFTIMEFROM);
        int idKEFaktor = cursor.getColumnIndex(KEFaktor.KEFAKTOR_COL_KEFFAKTOR);

        long kefId = cursor.getLong(idKEFid);
        int kefTime = cursor.getInt(idKEFTime);
        double kefFaktor = cursor.getDouble(idKEFaktor);

        KEFaktor mykeFaktor= new KEFaktor(kefTime, kefFaktor, kefId);

        return mykeFaktor;
    }

    public KEFaktor createKEFaktor(int Time, double Faktor) {
        ContentValues values = new ContentValues();
        values.put(KEFaktor.KEFAKTOR_COL_KEFTIMEFROM, Time);
        values.put(KEFaktor.KEFAKTOR_COL_KEFFAKTOR, Faktor);

        long insertId = database.insert(KEFaktor.KEFAKTOR_TABLE, null, values);

        Cursor cursor = database.query(KEFaktor.KEFAKTOR_TABLE,
                keFaktor_columns,  KEFaktor.KEFAKTOR_COL_ID + "=" + insertId,
                null, null, null, KEFaktor.KEFAKTOR_COL_KEFTIMEFROM);
        //COMPLETE: cursor - query(.... columns, ID = insertId, ....) hier wird die Row-Number zurück gegeben.

        cursor.moveToFirst();
        KEFaktor mykeFaktor = cursorToKEFaktor(cursor);
        cursor.close();

        return mykeFaktor;
    }

    public void deleteKEFaktor(KEFaktor keFaktor_del){
        long id = keFaktor_del.getId();

        database.delete(KEFaktor.KEFAKTOR_TABLE,
                KEFaktor.KEFAKTOR_COL_ID + "=" + id, null);
        Log.d(LOG_TAG, "Eintag gelöscht: ID: " + id + " Inhalt:" + keFaktor_del.toString());

    }

    public List<KEFaktor> getAllKEFaktor() {
        List<KEFaktor> keFaktorList = new ArrayList<>();

        Cursor cursor = database.query(KEFaktor.KEFAKTOR_TABLE,
                keFaktor_columns, null, null, null, null, KEFaktor.KEFAKTOR_COL_KEFTIMEFROM);

        cursor.moveToFirst();
        KEFaktor myKEFaktor;

        while(!cursor.isAfterLast()) {
            myKEFaktor= cursorToKEFaktor(cursor);
            keFaktorList.add(myKEFaktor);
            Log.d(LOG_TAG, "ID: " + myKEFaktor.getId() + ", Inhalt: " + myKEFaktor.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return keFaktorList;
    }

    public KEFaktor getCurrentKEFaktor() {

        Cursor cursor = database.query(KEFaktor.KEFAKTOR_TABLE_CURRENT,
                keFaktor_columns, null, null, null, null, null);

        if (cursor.getCount()==0){
            Log.d(LOG_TAG, "getCurrentKEFaktor: KEINE Faktoren gefunden.");
            return null;
        }
        cursor.moveToFirst();
        KEFaktor myKEFaktor;

        myKEFaktor= cursorToKEFaktor(cursor);
        Log.d(LOG_TAG, "ID: " + myKEFaktor.getId() + ", Inhalt: " + myKEFaktor.toString());

        cursor.moveToNext();
        cursor.close();

        return myKEFaktor;
    }







    private String[] bzKorrekturFaktor_columns = {
            BZKorrekturFaktor.BZKORREKTURFAKTOR_COL_ID,
            BZKorrekturFaktor.BZKORREKTURFAKTOR_COL_TIMEFROM,
            BZKorrekturFaktor.BZKORREKTURFAKTOR_COL_FAKTOR
    };
    private BZKorrekturFaktor cursorToBZKorrekturFaktor(Cursor cursor) {
        int idKorrId = cursor.getColumnIndex(BZKorrekturFaktor.BZKORREKTURFAKTOR_COL_ID);
        int idKorrTime = cursor.getColumnIndex(BZKorrekturFaktor.BZKORREKTURFAKTOR_COL_TIMEFROM);
        int idKorrFaktor = cursor.getColumnIndex(BZKorrekturFaktor.BZKORREKTURFAKTOR_COL_FAKTOR);

        long bzKorrId = cursor.getLong(idKorrId);
        int bzKorrTime = cursor.getInt(idKorrTime);
        int bzKorrFaktor = cursor.getInt(idKorrFaktor);

        BZKorrekturFaktor myBZKorrekturFaktor = new BZKorrekturFaktor(bzKorrTime, bzKorrFaktor, bzKorrId);

        return myBZKorrekturFaktor;
    }

    public BZKorrekturFaktor createBZKorrekturFaktor(int KorrTime, int KorrFaktor) {
        ContentValues values = new ContentValues();
        values.put(BZKorrekturFaktor.BZKORREKTURFAKTOR_COL_TIMEFROM, KorrTime);
        values.put(BZKorrekturFaktor.BZKORREKTURFAKTOR_COL_FAKTOR, KorrFaktor);

        long insertId = database.insert(BZKorrekturFaktor.BZKORREKTURFAKTOR_TABLE, null, values);

        Cursor cursor = database.query(BZKorrekturFaktor.BZKORREKTURFAKTOR_TABLE,
                bzKorrekturFaktor_columns, BZKorrekturFaktor.BZKORREKTURFAKTOR_COL_ID + "=" + insertId,
                null, null, null, BZKorrekturFaktor.BZKORREKTURFAKTOR_COL_TIMEFROM);
        //COMPLETE: cursor - query(.... columns, ID = insertId, ....) hier wird die Row-Number zurück gegeben.

        cursor.moveToFirst();
        BZKorrekturFaktor bzKorrekturFaktor = cursorToBZKorrekturFaktor(cursor);
        cursor.close();

        return bzKorrekturFaktor;
    }

    public void deleteBZKorrekturFaktor(BZKorrekturFaktor bzKorrekturFaktor_del){
        long id = bzKorrekturFaktor_del.getId();

        database.delete(BZKorrekturFaktor.BZKORREKTURFAKTOR_TABLE,
                BZKorrekturFaktor.BZKORREKTURFAKTOR_COL_ID + "=" + id, null);
        Log.d(LOG_TAG, "Eintag gelöscht: ID: " + id + " Inhalt:" + bzKorrekturFaktor_del.toString());

    }

    public List<BZKorrekturFaktor> getAllBZKorrekturFaktor() {
        List<BZKorrekturFaktor> bzKorrekturFaktorList = new ArrayList<>();
        Cursor cursor = database.query(BZKorrekturFaktor.BZKORREKTURFAKTOR_TABLE,
                bzKorrekturFaktor_columns, null, null, null, null, BZKorrekturFaktor.BZKORREKTURFAKTOR_COL_TIMEFROM);

        cursor.moveToFirst();
        BZKorrekturFaktor bzKorrekturFaktor;

        while(!cursor.isAfterLast()) {
            bzKorrekturFaktor = cursorToBZKorrekturFaktor(cursor);
            bzKorrekturFaktorList .add(bzKorrekturFaktor);
            Log.d(LOG_TAG, "ID: " + bzKorrekturFaktor.getId() + ", Inhalt: " + bzKorrekturFaktor.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return bzKorrekturFaktorList;
    }

    public BZKorrekturFaktor getCurrentBZKorrekturFaktor() {
        try {
            Cursor cursor = database.query(BZKorrekturFaktor.BZKORREKTURFAKTOR_TABLE_CURRENT,
                    bzKorrekturFaktor_columns, null, null, null, null, null);
            cursor.moveToFirst();
            BZKorrekturFaktor mybzKorrekturFaktor;

            mybzKorrekturFaktor = cursorToBZKorrekturFaktor(cursor);
            Log.d(LOG_TAG, "ID: " + mybzKorrekturFaktor.getId() + ", Inhalt: " + mybzKorrekturFaktor.toString());

            cursor.close();

            return mybzKorrekturFaktor;
        } catch (Exception e) {
            Log.d(LOG_TAG, "Fehler beim Datenabruf: " + e.getMessage());
            return null;
        }
    }




    private String[] HistoryEntry_columns = {
            HistoryEntry.HISTORY_COL_TIMESTAMP
            ,HistoryEntry.HISTORY_COL_ID
            ,HistoryEntry.HISTORY_COL_BLUTZUCKER
            ,HistoryEntry.HISTORY_COL_KORREKTURFAKTOR
            ,HistoryEntry.HISTORY_COL_KE
            ,HistoryEntry.HISTORY_COL_KEFFAKTOR
            ,HistoryEntry.HISTORY_COL_ZIELWERT_MAX
            ,HistoryEntry.HISTORY_COL_ZIELWERT_MIN
            ,HistoryEntry.HISTORY_COL_BOLUS
            ,HistoryEntry.HISTORY_COL_NOTIZ
    };
    private HistoryEntry cursorToHistoryEntry(Cursor cursor) {

        long histId = cursor.getLong(cursor.getColumnIndex(HistoryEntry.HISTORY_COL_ID));
        Date histTimestamp = new Date(0);
        try {
            histTimestamp  = new Date(new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(cursor.getString(cursor.getColumnIndex(HistoryEntry.HISTORY_COL_TIMESTAMP))).getTime());
        } catch (Exception e) {
            Log.d(LOG_TAG, "SQLITE Timestamp to String to Date Conversion fehlgeschlagen." + cursor.getString(cursor.getColumnIndex(HistoryEntry.HISTORY_COL_TIMESTAMP)));
        }

        int histBlutzucker =  cursor.getInt(cursor.getColumnIndex(HistoryEntry.HISTORY_COL_BLUTZUCKER));
        double histKe = cursor.getDouble(cursor.getColumnIndex(HistoryEntry.HISTORY_COL_KE));
        double histBolus = cursor.getDouble(cursor.getColumnIndex(HistoryEntry.HISTORY_COL_BOLUS));
        String histNotiz = cursor.getString(cursor.getColumnIndex(HistoryEntry.HISTORY_COL_NOTIZ));
        int histKorrekturfaktor = cursor.getInt(cursor.getColumnIndex(HistoryEntry.HISTORY_COL_KORREKTURFAKTOR));
        int histZielwertMax = cursor.getInt(cursor.getColumnIndex(HistoryEntry.HISTORY_COL_ZIELWERT_MAX));
        int histZielwertMin= cursor.getInt(cursor.getColumnIndex(HistoryEntry.HISTORY_COL_ZIELWERT_MIN));
        double histKeFaktor = cursor.getDouble(cursor.getColumnIndex(HistoryEntry.HISTORY_COL_KEFFAKTOR));

        HistoryEntry myHistoryEntry = new HistoryEntry(
                histId, histTimestamp, histBlutzucker,
                histKe, histBolus, histNotiz,
                histKorrekturfaktor, histZielwertMax, histZielwertMin,
                histKeFaktor);
        return myHistoryEntry;
    }

    public HistoryEntry createHistoryEntry(Date histTimestamp,
                                           int histBlutzucker
            , double histKe
            , double histBolus
            , String histNotiz
            , int histKorrekturfaktor
            , int histZielwertMax
            , int histZielwertMin
            , double histKeFaktor) {

        ContentValues values = new ContentValues();
        values.put(HistoryEntry.HISTORY_COL_TIMESTAMP      , histTimestamp.toString());
        values.put(HistoryEntry.HISTORY_COL_BLUTZUCKER      , histBlutzucker);
        values.put(HistoryEntry.HISTORY_COL_KE              , histKe);
        values.put(HistoryEntry.HISTORY_COL_BOLUS           , histBolus);
        values.put(HistoryEntry.HISTORY_COL_NOTIZ           , histNotiz);
        values.put(HistoryEntry.HISTORY_COL_KORREKTURFAKTOR , histKorrekturfaktor);
        values.put(HistoryEntry.HISTORY_COL_KEFFAKTOR       , histKeFaktor);
        values.put(HistoryEntry.HISTORY_COL_ZIELWERT_MAX    , histZielwertMax);
        values.put(HistoryEntry.HISTORY_COL_ZIELWERT_MIN    , histZielwertMin);


        long insertId = database.insert(HistoryEntry.HISTORY_TABLE, null, values);

        Cursor cursor = database.query(HistoryEntry.HISTORY_TABLE,
                HistoryEntry_columns, HistoryEntry.HISTORY_COL_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        HistoryEntry myHistoryEntry = cursorToHistoryEntry(cursor);
        cursor.close();

        return myHistoryEntry ;
    }

    public void deleteHistoryEntry(HistoryEntry myHistoryEntry_del){
        long id = myHistoryEntry_del.getId();

        database.delete(HistoryEntry.HISTORY_TABLE,
                HistoryEntry.HISTORY_COL_ID+ "=" + id, null);
        Log.d(LOG_TAG, "Eintag gelöscht: ID: " + id + " Inhalt:" + myHistoryEntry_del.toString());

    }

    public List<HistoryEntry> getAllHistoryEntries() {
        List<HistoryEntry> historyEntryList = new ArrayList<>();
        Cursor cursor = database.query(HistoryEntry.HISTORY_TABLE,
                HistoryEntry_columns, null, null, null, null, HistoryEntry.HISTORY_COL_ID +" DESC");

        cursor.moveToFirst();
        HistoryEntry myHistoryEntry;

        while(!cursor.isAfterLast()) {
            myHistoryEntry = cursorToHistoryEntry(cursor);
            historyEntryList .add(myHistoryEntry);
            Log.d(LOG_TAG, "ID: " + myHistoryEntry.getId() + ", Inhalt: " + myHistoryEntry.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return historyEntryList;
    }

    public HistoryEntry getLastHistoryEntry() {
        try {
            Cursor cursor = database.query(HistoryEntry.HISTORY_TABLE_LAST,
                    HistoryEntry_columns, null, null, null, null, null);
            cursor.moveToFirst();
            HistoryEntry myHistoryEntry;

            myHistoryEntry = cursorToHistoryEntry(cursor);
            Log.d(LOG_TAG, "ID: " + myHistoryEntry.getId() + ", Inhalt: " + myHistoryEntry.toString());

            cursor.close();

            return myHistoryEntry;
        } catch (Exception e) {
            Log.d(LOG_TAG, "Fehler beim Datenabruf: " + e.getMessage());
            return null;
        }
    }
}
