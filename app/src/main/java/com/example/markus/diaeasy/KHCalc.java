package com.example.markus.diaeasy;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class KHCalc extends AppCompatActivity {


    Button mybtn_add;
    ListView mylistview_Data;
    EditText et_100g, et_g_gegessen, et_Zutat;
    TextView tv_gesamt;

    ArrayList<KHCalc_Unit> lv_eintraege = new ArrayList<KHCalc_Unit>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khcalc);

        mylistview_Data = (ListView) findViewById(R.id.listview_Data);
        et_100g = (EditText) findViewById(R.id.et_100g);
        et_g_gegessen = (EditText)findViewById(R.id.et_g_essen);
        tv_gesamt = (TextView) findViewById(R.id.tv_KH_gesamt);
        et_Zutat = (EditText) findViewById(R.id.et_Zutat);

        initilise_button();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_khcalc, menu);
        return true;
    }

    private void initilise_button() {
        mybtn_add = (Button) findViewById(R.id.button_add_entry);

        mybtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neuer_Eintrag();

            }
        });

        // Set an editor action listener for edit text
        et_Zutat.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // If user press done key
                if(i == EditorInfo.IME_ACTION_DONE){
                    neuer_Eintrag();
                    et_100g.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_save) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", tv_gesamt.getText().toString());
            setResult(this.RESULT_OK, returnIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void neuer_Eintrag() {
        // beide Felder müssen gefüllt sein.

        if ("".equals(et_100g.getText().toString())) {
            et_100g.setError(getString(R.string.editText_errorMessage));
            return;
        }
        if ("".equals(et_g_gegessen.getText().toString())) {
            et_g_gegessen.setError(getString(R.string.editText_errorMessage));
            return;
        }

        if (Double.parseDouble(et_100g.getText().toString())> 100.0) {
            et_100g.setError(getString(R.string.editText_errorMessage));
            return;
        }


        KHCalc_Unit myKhCalc_Unit = new KHCalc_Unit(et_Zutat.getText().toString(), Double.parseDouble(et_100g.getText().toString()), Double.parseDouble(et_g_gegessen.getText().toString()));

        lv_eintraege.add(myKhCalc_Unit );

        ArrayAdapter<KHCalc_Unit> myArrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                lv_eintraege);

        mylistview_Data.setAdapter(myArrayAdapter);

        et_g_gegessen.setText("");
        et_100g.setText("");
        et_Zutat.setText("");

        summiere_Eintraege();
    }

    private void summiere_Eintraege() {
        double dbl_sum = 0;
        for (KHCalc_Unit str_entry : lv_eintraege)  {
            dbl_sum += str_entry.getKEs();
        }

        tv_gesamt.setText(String.valueOf(Math.round(dbl_sum*10.0)/10.0));
    }
}
