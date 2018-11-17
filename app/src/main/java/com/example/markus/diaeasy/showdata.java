package com.example.markus.diaeasy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class showdata extends AppCompatActivity {

    private static final String LOG_TAG = showdata.class.getSimpleName();

    private DatabaseDataSource dataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdata);

        dataSource = new DatabaseDataSource(this);

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
        List<HistoryEntry> myDataList= dataSource.getAllHistoryEntries();

        ArrayAdapter<HistoryEntry> myDataArrayAdapter = new ArrayAdapter<HistoryEntry>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                myDataList);

        ListView myDataListView = (ListView) findViewById(R.id.listview_data);
        myDataListView.setAdapter(myDataArrayAdapter);
    }



    private void initializeContextualActionBar() {
        final ListView listview_data = (ListView) findViewById(R.id.listview_data);
        listview_data.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listview_data.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
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
                        SparseBooleanArray touchedData = listview_data.getCheckedItemPositions();
                        for (int i=0; i < touchedData.size(); i++) {
                            boolean isChecked = touchedData.valueAt(i);
                            if(isChecked) {
                                int postitionInListView = touchedData.keyAt(i);
                                HistoryEntry myData= (HistoryEntry) listview_data.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Löschen: Position im ListView: " + postitionInListView + " Inhalt: " + myData.toString());
                                dataSource.deleteHistoryEntry(myData);
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
