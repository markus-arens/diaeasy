package com.example.markus.diaeasy;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    //TODO: Daten nochmals prüfen. Werte werden nicht mehr ausgelesen.
    //TODO: History-Speichern implementieren.

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    DatabaseHelper dbke;
    DatabaseDataSource dataSource;

    List<String> strHistory = new ArrayList<>();;

//    ListView lv_history;
    RadioButton rb_snack;


    EditText et_KEs
            , et_Ergebnis
            , et_Blutzucker
            , et_Notiz;


    TextView
             tv_KEFaktor
            , tv_Kohlenhydrate
            , tv_BolusInfo
            ;

    BZZiel myBZZiel ;
    BZKorrekturFaktor myBZKorrekturFaktor;
    KEFaktor myKEFaktor;
    Basalrate myBasalrate;
    Button mybtn_khcalculator;

    boolean _ignore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.diaeasy_transparent);
        //toolbar.setLogo(R.drawable.diaeasy_transparent);


//        lv_history = (ListView) findViewById(R.id.lv_historie);
        tv_KEFaktor = (TextView) findViewById(R.id.tv_currentKEFaktor);

        et_KEs = (EditText) findViewById(R.id.et_inputKEs);
        et_Blutzucker = (EditText) findViewById(R.id.et_blutzucker);
        et_Notiz = (EditText) findViewById(R.id.et_notiz);
        tv_Kohlenhydrate= (TextView) findViewById(R.id.tv_KH);
        et_Ergebnis = (EditText) findViewById(R.id.et_ergebnis);
        mybtn_khcalculator = (Button) findViewById(R.id.btn_khcalculator);
        tv_BolusInfo = (TextView) findViewById(R.id.tv_BolusInfo);
        rb_snack = (RadioButton) findViewById(R.id.rb_snack);

        dbke = new DatabaseHelper(this);
        dataSource = new DatabaseDataSource(this);


        initialise_ItemAktivities();


    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        dataSource.open();

        Log.d(LOG_TAG, "Anzeigedaten aktualisieren:");
        refresh_current_Data();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        dataSource.close();
    }


    void initialise_ItemAktivities() {
        mybtn_khcalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, KHCalc.class);
                //myIntent.putExtra("key", value); //Optional parameters
                startActivityForResult(myIntent, 1);
            }
        });

        et_Blutzucker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    calcucate_InsulinUnits();
                    if (get_unterzucker_abzug()>0) {
                        Snackbar sb = Snackbar.make(findViewById(R.id.myLayout), "Achtung: Bitte " + (get_unterzucker_abzug() * -1) + " KEs essen", Snackbar.LENGTH_LONG);
                        sb.getView().setBackgroundColor(Color.RED);
                        sb.show();

                    }
                }
            }
        });

        et_Blutzucker.addTextChangedListener(new TextWatcher() {
                                                 @Override
                                                 public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                                 @Override
                                                 public void onTextChanged(CharSequence s, int start, int before, int count) {}

                                                 @Override
                                                 public void afterTextChanged(Editable s) {
//                                                     calcucate_InsulinUnits();
                                                 }
                                             }
        );


        et_KEs.addTextChangedListener(new TextWatcher() {
                                          @Override
                                          public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                                          @Override
                                          public void onTextChanged(CharSequence s, int start, int before, int count) { }

                                          @Override
                                          public void afterTextChanged(Editable s) {
                                              if (!_ignore) {
                                                  _ignore= true;
                                                  if("".equals(et_KEs.getText().toString())) {
                                                      tv_Kohlenhydrate.setText("");
                                                  }
                                                  else {
                                                      tv_Kohlenhydrate.setText("Kohlenhydrate : " + String.valueOf(Double.parseDouble(et_KEs.getText().toString()) * 10));
                                                  }
                                                  calcucate_InsulinUnits();

                                                  _ignore = false;
                                              }
                                          }
                                      }
        );


        rb_snack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calcucate_InsulinUnits();
            }
        });

    }

    void refresh_current_Data() {

        if (!dataSource.check_table_exists())
            return;

        myBZZiel = dbke.getBZZiel_current();
        myBZKorrekturFaktor = dataSource.getCurrentBZKorrekturFaktor();
        myKEFaktor = dataSource.getCurrentKEFaktor();
        myBasalrate = dbke.getBasalrate_current();

        setcurrent_Data();

        checkReturnResults();
    }

    void setcurrent_Data() {
        if (myKEFaktor != null) {
            tv_KEFaktor.setText(String.valueOf(myKEFaktor.getFaktor()));
//            history_add_Entry("KEFaktor: " + myKEFaktor.getFaktor());
        }

        if (myBZKorrekturFaktor != null) {
//            history_add_Entry( "Korrekturfaktor: " + myBZKorrekturFaktor.getBZKorrFaktor());
        }

        if (myBZZiel != null) {
//            history_add_Entry("Blutzucker-Ziel: " + dbke.getBZZiel_current().BZZielMin + " - " + dbke.getBZZiel_current().BZZielMax);
        }

        if (myBasalrate != null) {
//            history_add_Entry( "Basalrate: " + dbke.getBasalrate_current().BasalRate);
        }


    }

    /*Gibt bei einer Unterzuckerung einen festen Wert zurück, der an KEs gegessen werden soll und für die Berechnung frei ist.*/
    double get_unterzucker_abzug() {
        if (get_Blutzucker() == 0)
            return 0;
        else if (get_Blutzucker()  < 50)
            return 1.5;
        else if (get_Blutzucker()  < 60)
            return 1.0;
        else if (get_Blutzucker()  < 80)
            return 0.5;
        else
            return 0;
    }

    //Gibt den aktuell eingegebenen Blutzuckerwert zurück. 0, falls nicht gefüllt.
    int get_Blutzucker() {
        if ("".equals(et_Blutzucker.getText().toString()))
            return 0;
        else
            return  Integer.parseInt(et_Blutzucker.getText().toString());
    }

    double get_KEs() {
        if ("".equals(et_KEs.getText().toString()))
            return 0.0;
        else
            return  Double.parseDouble(et_KEs.getText().toString());
    }

    double get_Bolus() {
        if ("".equals(et_Ergebnis.getText().toString()))
            return 0.0;
        else
            return Double.parseDouble(et_Ergebnis.getText().toString());
    }


    void calcucate_InsulinUnits(){
        double  dbl_ergebnis =0 , dbl_blutzucker_ergebnis =0, dbl_KEs_ergebnis = 0;
        StringBuffer str_history = new StringBuffer();

        et_Ergebnis.setText("");
        et_Ergebnis.setError(null);
        tv_BolusInfo.setText("");


       /* if (get_Blutzucker() == 0 ) {
            bool_blutzucker = false; //keine BZ Eingabe = keine Berechnung
        }
        else {
            dbl_blutzucker = get_Blutzucker();
            bool_blutzucker = true;
        }*/

        /*if (get_KEs() == 0)
            bool_KEs = false;
        else {
            dbl_KEs = get_KEs();
            bool_KEs = true;
        }*/



        if( get_KEs() != 0) {
            dbl_KEs_ergebnis = dataSource.getCurrentKEFaktor().getFaktor() *  get_KEs();
            str_history.append("KEs = " +dataSource.getCurrentKEFaktor().getFaktor() + " * " + get_KEs() + "\n");
        }


         if(get_Blutzucker() != 0) {
             if (myBZZiel == null)
                 et_Ergebnis.setError("Berechnung fehlgeschlagen. Kein BZ-Ziel angegeben.");
             else if (get_Blutzucker() < myBZZiel.BZZielMin){
                //Hypo (ziel 80 - ist 60 = 20 Diff. => KEFaktor
//                dbl_blutzucker_ergebnis = (dbl_blutzucker - myBZZiel.BZZielMax)  ; // Bis Max-Ziel ist sind die KEs frei.
//                dbl_blutzucker_ergebnis = dbl_blutzucker_ergebnis / myBZKorrekturFaktor.getBZKorrFaktor();
                //dbl_blutzucker_einheiten spiegelt den Wert wieder, um den abzugebendes Insulin reduziert werden muss.
//                str_history.append( "Hypo: (" + dbl_blutzucker + " - " + myBZZiel.BZZielMax  + ") / " +  myBZKorrekturFaktor.getBZKorrFaktor() + " = " + dbl_blutzucker_ergebnis + "\n");

                 dbl_blutzucker_ergebnis = (-1) * get_unterzucker_abzug() * myKEFaktor.getFaktor();
                tv_BolusInfo.setText("Achtung: " + get_unterzucker_abzug()  + " KEs essen.");
            }
            else if (get_Blutzucker() > myBZZiel.BZZielMax) {
                // Hyper
                if (rb_snack.isChecked()) {
                    dbl_blutzucker_ergebnis = 0;
                    tv_BolusInfo.setText("Zur Snack-Zeit wird keine Korrektur gespritzt.");
                }
                else {
                    dbl_blutzucker_ergebnis = (get_Blutzucker() - myBZZiel.BZZielMax)  ;
                    dbl_blutzucker_ergebnis = dbl_blutzucker_ergebnis / myBZKorrekturFaktor.getBZKorrFaktor();
                    str_history.append( "Hyper: (" + get_Blutzucker()  + " - " + myBZZiel.BZZielMax + ") / " +  myBZKorrekturFaktor.getBZKorrFaktor() + " = " + dbl_blutzucker_ergebnis + "\n");
                }

            }
            else{
                //innerhalb = keine Korrektur
                str_history.append( "im Zielbereich\n");
            }
        }

        dbl_ergebnis = dbl_KEs_ergebnis + dbl_blutzucker_ergebnis;

        dbl_ergebnis = Math.round(dbl_ergebnis*40.0)/40.0;
        if (dbl_ergebnis <0) dbl_ergebnis = 0;

        str_history.append("Ergebnis = Runden: (" +dbl_KEs_ergebnis   + " + " + dbl_blutzucker_ergebnis + "*40.0)/40.0 = " + dbl_ergebnis + "\n");

        et_Ergebnis.setText(String.valueOf(dbl_ergebnis));
    }

    void save_Input() {


        if (get_Blutzucker() + get_KEs() + get_Bolus() > 0 || !"".equals(et_Notiz.getText().toString())) {
            HistoryEntry myHistoryEntry = dataSource.createHistoryEntry(new Date(),
                    get_Blutzucker(),
                    get_KEs(),
                    get_Bolus(),
                    et_Notiz.getText().toString(),
                    myBZKorrekturFaktor.getBZKorrFaktor(),
                    myBZZiel.BZZielMax,
                    myBZZiel.BZZielMin,
                    myKEFaktor.getFaktor());

            if (myHistoryEntry != null) {
                Toast.makeText(this, "Gespeichert: " + myHistoryEntry.toString(), Toast.LENGTH_LONG).show();
                clean_editfields();
            }

        }
        else{
            Toast.makeText(this, "Keine Daten zum speichern eingegeben. Bitte geben Sie mindestens einen Wert ein.", Toast.LENGTH_SHORT).show();
        }


    }

    void clean_editfields() {
        et_Blutzucker.setText("");
        et_KEs.setText("");
        et_Ergebnis.setText("");
        et_Notiz.setText("");
        returnedResult = "";
        hideKeyboard(this);
    }

    void checkReturnResults(){
        if (!"".equals(returnedResult) && returnedResult != null) {
            et_KEs.setText(returnedResult);
        }
    }


    void test_permission() {
        int permission;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            permission = 0;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);

        }
        else {
            permission = 1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, setdata.class);
            startActivity(intent);
        }
        else if (id == R.id.action_showdata) {
            Intent intent = new Intent(this, showdata.class);
            startActivity(intent);
        }

        else if (id == R.id.action_save) {
            save_Input();
        }
        else if (id == R.id.action_export_history) {
            test_permission();
            exportData(dataSource.getAllHistoryEntries());
        }
/*
        else if (id == R.id.demo_data) {

            refresh_current_Data();
            Toast.makeText(this, "!!Datenbanken auf Demo zurückgesetzt!!" , Toast.LENGTH_LONG).show();
        }
*/
        return super.onOptionsItemSelected(item);
    }

    String returnedResult = "";
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                returnedResult = data.getStringExtra("result");

                // OR
                // String returnedResult = data.getDataString();
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }


    public String exportData(List<HistoryEntry> list_arr) {
        {

            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/diaeasy");

            boolean var = false;
            if (!folder.exists())
                var = folder.mkdir();

            System.out.println("" + var);


            final String filename = folder.toString() + "/" + "HistoryEntry.csv";


            try {

                FileWriter fw = new FileWriter(filename);

                fw.write("");
                for (HistoryEntry myhist : list_arr) {
                    fw.append("\n");
                    fw.append(myhist.exportFormat(";"));
                }
                fw.close();
                Toast.makeText(this, "Datei gespeichert. \n" + filename , Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(this, "Fehler beim speichern. \n" + filename + "\n" + e.getMessage() , Toast.LENGTH_LONG).show();
                return e.getMessage();

            }
            finally {


            }
            return "";
        }

    }
}
