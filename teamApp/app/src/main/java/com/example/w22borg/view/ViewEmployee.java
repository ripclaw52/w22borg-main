package com.example.w22borg.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.example.w22borg.R;

import java.util.Objects;

public class ViewEmployee extends AppCompatActivity {

    Button editingEm_bttn, editingAvil_btn;
    TextView txt_id, txt_firstname, txt_lastname, txt_email, txt_phoneNo, txt_startDate, txt_totalShift;
    SwitchCompat sw_canOpen, sw_canClose, sw_active;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_employee);

        Toolbar toolbar = findViewById(R.id.actionbar_ViewEm);
        setSupportActionBar(toolbar);


        txt_id = findViewById(R.id.txt_id);
        txt_firstname = findViewById(R.id.txt_fname);
        txt_lastname = findViewById(R.id.txt_lname);
        txt_email = findViewById(R.id.txt_email);
        txt_phoneNo = findViewById(R.id.txt_phoneNo);
        txt_startDate = findViewById(R.id.txt_startDate);
        txt_totalShift = findViewById(R.id.txt_totalShift);
        sw_canOpen = findViewById(R.id.sw_canOpen);
        sw_canClose = findViewById(R.id.sw_canClose);
        sw_active = findViewById(R.id.sw_Active);

        editingEm_bttn = findViewById(R.id.editingEm_bttn);
        editingAvil_btn = findViewById(R.id.editingAvil_bttn);

        // get employee info from previous screen
        Intent prevIntent = getIntent();
        int empID = prevIntent.getIntExtra("ID", 0);
        String empFname = prevIntent.getStringExtra("firstname");
        String empLname = prevIntent.getStringExtra("lastname");
        String empEmail = prevIntent.getStringExtra("email");
        String empPhoneNo = prevIntent.getStringExtra("phoneNo");
        String empStartDate = prevIntent.getStringExtra("startDate");
        int empTotalShift = prevIntent.getIntExtra("totalShift", 0);
        boolean empCanOpen = prevIntent.getBooleanExtra("canOpen", false);
        boolean empCanClose = prevIntent.getBooleanExtra("canClose", false);
        int active = prevIntent.getIntExtra("Active", 0);
        int monday = prevIntent.getIntExtra("Monday", 0);
        int tuesday = prevIntent.getIntExtra("Tuesday", 0);
        int wednesday = prevIntent.getIntExtra("Wednesday",0);
        int thursday = prevIntent.getIntExtra("Thursday", 0);
        int friday = prevIntent.getIntExtra("Friday", 0);
        int saturday = prevIntent.getIntExtra("Saturday", 0);
        int sunday = prevIntent.getIntExtra("Sunday", 0);

        // set action bar title and back button
        Objects.requireNonNull(getSupportActionBar()).setTitle(empFname + " " + empLname);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_id.setText(String.valueOf(empID));
        txt_firstname.setText(empFname);
        txt_lastname.setText(empLname);
        txt_email.setText(empEmail);
        txt_phoneNo.setText(empPhoneNo);
        txt_startDate.setText(empStartDate);
        txt_totalShift.setText(String.valueOf(empTotalShift));
        sw_canOpen.setChecked(empCanOpen);
        sw_canClose.setChecked(empCanClose);
        sw_active.setChecked(active == 0);

        // Make so canOpen switch cannot be toggled on view employee screen
        sw_canOpen.setOnClickListener(view -> sw_canOpen.setChecked(!sw_canOpen.isChecked()));

        // Make so canClose switch cannot be toggled on view employee screen
        sw_canClose.setOnClickListener(view -> sw_canClose.setChecked(!sw_canClose.isChecked()));

        // Make so active switch cannot be toggled on view employee screen
        sw_active.setOnClickListener(view -> sw_active.setChecked(!sw_active.isChecked()));

        // Send employee info to EditingEmployee Class on Edit Employee Profile button press
        editingEm_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editingEm = new Intent(getApplicationContext(), com.example.w22borg.view.EditingEmployee.class);
                editingEm.putExtra("ID", empID);
                editingEm.putExtra("firstname", empFname);
                editingEm.putExtra("lastname", empLname);
                editingEm.putExtra("email", empEmail);
                editingEm.putExtra("phoneNo", empPhoneNo);
                editingEm.putExtra("startDate", empStartDate);
                editingEm.putExtra("totalShift", empTotalShift);
                editingEm.putExtra("canOpen", empCanOpen);
                editingEm.putExtra("canClose", empCanClose);
                editingEm.putExtra("Active", active);

                startActivity(editingEm);
            }
        });

        // Send employee info to EditAvailability Class on Edit Employee Availability press
        editingAvil_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editingAvail = new Intent(getApplicationContext(), com.example.w22borg.view.EditAvailability.class);
                editingAvail.putExtra("ID", empID);
                editingAvail.putExtra("firstname", empFname);
                editingAvail.putExtra("lastname", empLname);
                editingAvail.putExtra("Monday", monday);
                editingAvail.putExtra("Tuesday", tuesday);
                editingAvail.putExtra("Wednesday", wednesday);
                editingAvail.putExtra("Thursday", thursday);
                editingAvail.putExtra("Friday", friday);
                editingAvail.putExtra("Saturday", saturday);
                editingAvail.putExtra("Sunday", sunday);

                startActivity(editingAvail);
            }
        });
    }
}