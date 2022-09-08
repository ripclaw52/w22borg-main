package com.example.w22borg.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.w22borg.MainActivity;
import com.example.w22borg.R;
import com.example.w22borg.SQLdb;
import com.example.w22borg.data.EmployeeModel;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Objects;

public class AddEmployee extends AppCompatActivity {

    EditText firstname, lastname, email, phone;
    TextView startdate;
    Button addEmployee;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        Toolbar toolbar = findViewById(R.id.actionbar_AddEm);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Employees");

        //set home button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firstname = findViewById(R.id.f_name);
        lastname = findViewById(R.id.l_name);
        email = findViewById(R.id.em_email);
        phone = findViewById(R.id.phone_number);
        startdate = findViewById(R.id.start_date);

        //date will be fill in automatically
        /*
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        */
        String currentDate = LocalDate.now().toString();

        //LocalDate otherDate = LocalDate.parse(currentDate);

        startdate.setText(currentDate);

        addEmployee = findViewById(R.id.addEm_bttn);


        addEmployee.setOnClickListener(v -> {

            EmployeeModel employee = null;
            String fname = firstname.getText().toString();
            String lname = lastname.getText().toString();
            String ph = phone.getText().toString();
            String emailS = email.getText().toString();
            if (emailS.isEmpty() || !isValidEmail(emailS) || ph.length() < 10 || ph.length() > 10 || !isValidName(fname) || fname.isEmpty() || !isValidName(lname) || lname.isEmpty())
            {
                if (emailS.isEmpty() || !isValidEmail(emailS))
                    email.setError("Invalid Email");
                if (ph.length() < 10 || ph.length() > 10)
                    phone.setError("Invalid phone number");
                if (!isValidName(fname) || fname.isEmpty())
                    firstname.setError("Invalid name");
                if (!isValidName(lname) || lname.isEmpty())
                    lastname.setError("Invalid name");
            }
            else {
                try {
                    employee = new EmployeeModel(1, firstname.getText().toString(),
                            lastname.getText().toString(), email.getText().toString(), phone.getText().toString(),
                            startdate.getText().toString());
                    Toast.makeText(AddEmployee.this, "Added '" + firstname.getText().toString() +
                            " " + lastname.getText().toString() + "'", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(AddEmployee.this, "Error creating employee", Toast.LENGTH_SHORT).show();

                }
                SQLdb database = new SQLdb(AddEmployee.this);
                boolean success = database.addEmployee(Objects.requireNonNull(employee));
                //Toast.makeText(AddEmployee.this,"status: " + success, Toast.LENGTH_SHORT).show();
                database.close();
                Intent i = new Intent(AddEmployee.this, SelectEmployee.class);
                startActivity(i);
            }

        });
    }


    //for email validation
    public static boolean isValidEmail (CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidName (String string) {
        return string.matches("[a-zA-Z]+");
    }

    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}