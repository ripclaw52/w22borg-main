package com.example.w22borg.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.w22borg.MainActivity;
import com.example.w22borg.R;
import com.example.w22borg.SQLdb;
import com.example.w22borg.calendar.CalendarActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EditAvailability extends AppCompatActivity {

    int mon, tue, wed, thu, fri, sat, sun;
    Spinner spinnerMon, spinnerTue, spinnerWed, spinnerThu, spinnerFri, spinnerSat, spinnerSun;
    TextView txtMon, txtTue, txtWed, txtThu, txtFri, txtSat, txtSun;
    Button saveAvail;

    // Availability options
    String[] availOptions = new String[]{"Open and Close", "Open", "Close", "Unavailable"};
    String[] availOptionsWkd = new String[]{"All day", "Unavailable"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_availability);

        Toolbar toolbar = findViewById(R.id.actionbar_Em_avail);
        setSupportActionBar(toolbar);

        txtMon = findViewById(R.id.textMon);
        txtTue = findViewById(R.id.textTue);
        txtWed = findViewById(R.id.textWed);
        txtThu = findViewById(R.id.textThu);
        txtFri = findViewById(R.id.textFri);
        txtSat = findViewById(R.id.textSat);
        txtSun = findViewById(R.id.textSun);

        spinnerMon = findViewById(R.id.spinnerMon);
        spinnerTue = findViewById(R.id.spinnerTue);
        spinnerWed = findViewById(R.id.spinnerWed);
        spinnerThu = findViewById(R.id.spinnerThu);
        spinnerFri = findViewById(R.id.spinnerFri);
        spinnerSat = findViewById(R.id.spinnerSat);
        spinnerSun = findViewById(R.id.spinnerSun);

        saveAvail = findViewById(R.id.saveAvail_btn);


        // get employee info from previous screen
        Intent prevIntent = getIntent();
        int empID = prevIntent.getIntExtra("ID", 0);
        String empFname = prevIntent.getStringExtra("firstname");
        String empLname = prevIntent.getStringExtra("lastname");
        int monday = prevIntent.getIntExtra("Monday", 0);
        int tuesday = prevIntent.getIntExtra("Tuesday", 0);
        int wednesday = prevIntent.getIntExtra("Wednesday",0);
        int thursday = prevIntent.getIntExtra("Thursday", 0);
        int friday = prevIntent.getIntExtra("Friday", 0);
        int saturday = prevIntent.getIntExtra("Saturday", 0);
        int sunday = prevIntent.getIntExtra("Sunday", 0);

        // set action bar title and back button
        Objects.requireNonNull(getSupportActionBar()).setTitle(empFname + " " + empLname + "'s Availability");
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // populate day selections
        final List<String> optionList = new ArrayList<>(Arrays.asList(availOptions));
        final List<String> optionListWkd = new ArrayList<>(Arrays.asList(availOptionsWkd));

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, optionList);
        final ArrayAdapter<String> spinnerArrayAdapterWkd = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, optionListWkd);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerArrayAdapterWkd.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerMon.setAdapter(spinnerArrayAdapter);
        spinnerTue.setAdapter(spinnerArrayAdapter);
        spinnerWed.setAdapter(spinnerArrayAdapter);
        spinnerThu.setAdapter(spinnerArrayAdapter);
        spinnerFri.setAdapter(spinnerArrayAdapter);
        spinnerSat.setAdapter(spinnerArrayAdapterWkd);
        spinnerSun.setAdapter(spinnerArrayAdapterWkd);


        // default current availability to database values
        if (saturday == 3){saturday = 1;}
        if (sunday == 3){sunday = 1;}
        spinnerMon.setSelection(monday);
        spinnerTue.setSelection(tuesday);
        spinnerWed.setSelection(wednesday);
        spinnerThu.setSelection(thursday);
        spinnerFri.setSelection(friday);
        spinnerSat.setSelection(saturday);
        spinnerSun.setSelection(sunday);


        // save button clicked
        saveAvail.setOnClickListener(view -> {
            // get current spinner selections
            mon = spinnerMon.getSelectedItemPosition();
            tue = spinnerTue.getSelectedItemPosition();
            wed = spinnerWed.getSelectedItemPosition();
            thu = spinnerThu.getSelectedItemPosition();
            fri = spinnerFri.getSelectedItemPosition();
            sat = spinnerSat.getSelectedItemPosition();
            sun = spinnerSun.getSelectedItemPosition();
            // update database
            SQLdb database = new SQLdb(EditAvailability.this);
            database.editEmpAvail(String.valueOf(empID), mon, tue, wed, thu,
                    fri, sat, sun);
            Toast.makeText(EditAvailability.this, "Updated " + empFname + " " +
                    empLname + "'s availability." , Toast.LENGTH_SHORT).show();
            database.close();
            Intent selectEmp = new Intent(EditAvailability.this, SelectEmployee.class);
            startActivity(selectEmp);
        });

    }
}