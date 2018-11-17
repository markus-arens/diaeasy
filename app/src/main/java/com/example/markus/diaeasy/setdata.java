package com.example.markus.diaeasy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class setdata extends AppCompatActivity {

    private static final String LOG_TAG = setdata.class.getSimpleName();

    private DatabaseDataSource dataSource;

    private String str_object_Type_Class_Name = "";
    private boolean two_value_fields_needed = false;

    Spinner spn_dropdown;
    Button btn_addEntry ;
    EditText et_zeit ;
    EditText et_value1 ;
    EditText et_value2 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setdata);

        spn_dropdown = (Spinner) findViewById(R.id.spin_selection);
        btn_addEntry = (Button) findViewById(R.id.button_add_entry);
        et_zeit = (EditText) findViewById(R.id.et_TimeFrom1);
        et_value1 = (EditText) findViewById(R.id.et_value1);
        et_value2 = (EditText) findViewById(R.id.et_value1);

        dataSource = new DatabaseDataSource(this);

        activateAddButton();
        initializeContextualActionBar();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        dataSource.open();

        Log.d(LOG_TAG, "Folgende Einträge sind in der Datenbank vorhanden:");
        showAllListEntries();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        dataSource.close();
    }


    private void showAllListEntries () {
        ListView mylistview_Data = (ListView) findViewById(R.id.listview_Data);

        if (str_object_Type_Class_Name.equals(BZKorrekturFaktor.class.getSimpleName())) {
            ArrayAdapter<BZKorrekturFaktor> myArrayAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_multiple_choice,
                    dataSource.getAllBZKorrekturFaktor());
            mylistview_Data.setAdapter(myArrayAdapter);

        }
        else if (str_object_Type_Class_Name.equals(KEFaktor.class.getSimpleName())) {
            ArrayAdapter<KEFaktor> myArrayAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_multiple_choice,
                    dataSource.getAllKEFaktor());
            mylistview_Data.setAdapter(myArrayAdapter);
        }
        else {
            Log.d(LOG_TAG, "Kein Objekt-Typ-Gewählt => Keine Einträge!");
        }


    }

    private void show_second_value_field(){

        if (two_value_fields_needed) et_value2.setVisibility(View.VISIBLE);
    }

    private void activateAddButton() {

        String[] items = new String[]{KEFaktor.class.getSimpleName(), BZKorrekturFaktor.class.getSimpleName()};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spn_dropdown.setAdapter(adapter);
        spn_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                if (item != null) {
                    str_object_Type_Class_Name = item.toString();
                    showAllListEntries();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btn_addEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String timeValue_str  = et_zeit.getText().toString();
                String value1_str = et_value1.getText().toString();
                String value2_str = et_value2.getText().toString();


                if(TextUtils.isEmpty(timeValue_str)) {
                    et_zeit.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                if(TextUtils.isEmpty(value1_str)) {
                    et_value1.setError(getString(R.string.editText_errorMessage));
                    return;
                }

                if(TextUtils.isEmpty(value2_str) && et_value2.getVisibility() == View.VISIBLE) {
                    et_value2.setError(getString(R.string.editText_errorMessage));
                    return;
                }

                dataSource.createNewEntry(str_object_Type_Class_Name, timeValue_str, value1_str,  value2_str);

                et_zeit.setText("");
                et_value1.setText("");
                et_value2.setText("");



                /*
                Mit den folgenden Anweisungen lassen wir das Eingabefeld verschwinden, so dass unsere Einkaufsliste komplett zu sehen ist. Anschließend geben wir alle Einträge der SQLite Datenbank mit Hilfe des ListViews auf dem Display aus.
                Den Code für das Verstecken des Eingabefeldes haben wir aus einer Antwort bei StackOverflow entnommen. Ihr könnt ihn hier: How to hide soft keyboard on android after clicking outside EditText nachlesen.
                 *//*
                InputMethodManager inputMethodManager;
                inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
*/
                showAllListEntries();
            }
        });

    }



    private void initializeContextualActionBar() {
        final ListView listview_Data = (ListView) findViewById(R.id.listview_Data);
        listview_Data.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listview_Data.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.cab_delete:
                        SparseBooleanArray touchedDataPositions = listview_Data.getCheckedItemPositions();
                        for (int i=0; i < touchedDataPositions.size(); i++) {
                            boolean isChecked = touchedDataPositions.valueAt(i);
                            if(isChecked) {
                                int postitionInListView = touchedDataPositions.keyAt(i);

                                Object myDataEntry = listview_Data.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + myDataEntry.toString());
                                dataSource.deleteDBEntry(myDataEntry);
                            }
                        }
                        showAllListEntries();
                        mode.finish();
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setdata, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
