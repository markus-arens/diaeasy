package com.example.markus.diaeasy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DatabaseDataSource.class.getSimpleName();


    private static final int DATABASE_VERSION = 1;
    public static final String DB_NAME = "diaeasy.db";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        check_table_exists(db, false);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public boolean check_table_exists(SQLiteDatabase db, boolean reset_to_default)  {
        Log.d(LOG_TAG, "Prüfe, ob alle benötigten Tabellen existieren.");
        boolean return_value = true;
        return_value = return_value  && table_exists(db, Basalrate.BASAL_TABLE, reset_to_default);
        return_value = return_value  && table_exists(db, BZZiel.BZZIEL_TABLE, reset_to_default);
        return_value = return_value  && table_exists(db, BZKorrekturFaktor.BZKORREKTURFAKTOR_TABLE, reset_to_default);
        return_value = return_value  && table_exists(db, KEFaktor.KEFAKTOR_TABLE, reset_to_default);
        return_value = return_value  && table_exists(db, HistoryEntry.HISTORY_TABLE, reset_to_default);

        return_value = return_value  && table_exists(db, Basalrate.BASAL_TABLE, reset_to_default);
        return_value = return_value  && table_exists(db, BZZiel.BZZIEL_TABLE, reset_to_default);
        return_value = return_value  && table_exists(db, BZKorrekturFaktor.BZKORREKTURFAKTOR_TABLE_CURRENT, reset_to_default);
        return_value = return_value  && table_exists(db, KEFaktor.KEFAKTOR_TABLE_CURRENT, reset_to_default);
        return_value = return_value  && table_exists(db, HistoryEntry.HISTORY_TABLE_LAST, reset_to_default);

        return return_value;
    }

    private boolean table_exists(SQLiteDatabase db, String table_name, boolean reset_to_default) {
        Log.d(LOG_TAG, "table_exists: " +  db.toString() + " - Table_Name: " + table_name );

        String selectQuery = "SELECT name FROM sqlite_master WHERE (type='table' OR type ='view') AND name='" + table_name + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0 && !reset_to_default)
            return true;
        else {
            return reset_table(db, table_name);
        }
    }


    private boolean reset_table(SQLiteDatabase db, String tableName) {
        switch (tableName) {
            case Basalrate.BASAL_TABLE:             return setup_table(db, Basalrate.BASAL_SETUP);
            case Basalrate.BASAL_CURRENT:             return setup_table(db, Basalrate.BASAL_SETUP);
            case BZZiel.BZZIEL_TABLE :              return setup_table(db, BZZiel.BZZIEL_SETUP);
            case BZZiel.BZZIEL_CURRENT:              return setup_table(db, BZZiel.BZZIEL_SETUP);
            case BZKorrekturFaktor.BZKORREKTURFAKTOR_TABLE : return setup_table(db, BZKorrekturFaktor.BZKORREKTURFAKTOR_SETUP);
            case BZKorrekturFaktor.BZKORREKTURFAKTOR_TABLE_CURRENT: return setup_table(db, BZKorrekturFaktor.BZKORREKTURFAKTOR_SETUP);
            case KEFaktor.KEFAKTOR_TABLE:           return setup_table(db, KEFaktor.KEFAKTOR_SETUP);
            case KEFaktor.KEFAKTOR_TABLE_CURRENT:           return setup_table(db, KEFaktor.KEFAKTOR_SETUP);
            case HistoryEntry.HISTORY_TABLE :   return setup_table(db,  HistoryEntry.HISTORY_SETUP);
            case HistoryEntry.HISTORY_TABLE_LAST :   return setup_table(db,  HistoryEntry.HISTORY_SETUP);

            default:
                return false;
        }

    }

    private boolean setup_table(SQLiteDatabase db , String[] str_setup_arr)  {

        for (String str_setup : str_setup_arr){
            try{
                Log.d(LOG_TAG, "setup_table: " +  db.toString() + " - Setup-SQL: " + str_setup);
                db.execSQL(str_setup);

            } catch (Exception e) {
                Log.d(LOG_TAG, "setup_table ERROR: [stmt] " + str_setup + " [MSG]" +e.getMessage());
                return false;
            }
        }
        return true;

    }




    public BZZiel getBZZiel_current() {

        String selectQuery = "SELECT  * FROM " + BZZiel.BZZIEL_CURRENT;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        BZZiel myBZZiel = new BZZiel();

        if (cursor.moveToFirst()) {

            myBZZiel.BZTime = cursor.getInt(0);
            myBZZiel.BZZielMin= cursor.getInt(1);
            myBZZiel.BZZielMax = cursor.getInt(2);
        }
        cursor.close();

        return myBZZiel;
    }


    public Basalrate getBasalrate_current() {

        String selectQuery = "SELECT  * FROM " + Basalrate.BASAL_CURRENT;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        Basalrate myBasalrate= new Basalrate();

        if (cursor.moveToFirst()) {

            myBasalrate.BasalTime= cursor.getInt(0);
            myBasalrate.BasalRate= cursor.getDouble(1);
        }
        cursor.close();

        return myBasalrate;
    }

}



