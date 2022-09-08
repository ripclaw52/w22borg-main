package com.example.w22borg.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.w22borg.R;
import com.example.w22borg.SQLdb;

public class EditingEmployee extends AppCompatActivity {

    Button saveEm_bttn, delEm_bttn;
    SwitchCompat sw_canOpen, sw_canClose, sw_active;
    EditText edt_phoneNo, edt_email, edt_firstname, edt_lastname;
    TextView edt_startDate;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_employee);


        saveEm_bttn = findViewById(R.id.saveEm_bttn);
        delEm_bttn = findViewById(R.id.delEm_bttn);
        sw_canOpen = findViewById(R.id.sw_canOpen);
        sw_canClose = findViewById(R.id.sw_canClose);
        edt_phoneNo = findViewById(R.id.edt_phoneNo);
        edt_email = findViewById(R.id.edt_email);
        edt_firstname = findViewById(R.id.edt_f_name);
        edt_lastname = findViewById(R.id.edt_l_name);
        edt_startDate = findViewById(R.id.edt_startDate);
        sw_active = findViewById(R.id.sw_Active2);

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


        edt_firstname.setText(empFname);
        edt_lastname.setText(empLname);
        edt_email.setText(empEmail);
        edt_phoneNo.setText(empPhoneNo);
        edt_startDate.setText(empStartDate);
        sw_canOpen.setChecked(empCanOpen);
        sw_canClose.setChecked(empCanClose);
        sw_active.setChecked(active == 0);

        // delete button only visible with employees who have not worked
        if (empTotalShift > 0) {
            delEm_bttn.setVisibility(View.INVISIBLE);
        }

        saveEm_bttn.setOnClickListener(view -> {
            String emailS = edt_email.getText().toString();
            String fname = edt_firstname.getText().toString();
            String lname = edt_lastname.getText().toString();
            String ph = edt_phoneNo.getText().toString();
            if (emailS.isEmpty() || !isValidEmail(emailS) || ph.length() < 10 || ph.length() > 10 || !isValidName(fname) || fname.isEmpty() || !isValidName(lname) || lname.isEmpty())
            {
                if (!emailS.isEmpty() && !isValidEmail(emailS) || emailS.isEmpty())
                    edt_email.setError("Invalid Email");
                if (ph.length() < 10 || ph.length() > 10)
                    edt_phoneNo.setError("Invalid phone number");
                if (!isValidName(fname) || fname.isEmpty())
                    edt_firstname.setError("Invalid name");
                if (!isValidName(lname) || lname.isEmpty())
                    edt_lastname.setError("Invalid name");
            }

            else {
                String str_canOpen = "0", str_canClose = "0";
                int int_active = -1;
                if (sw_canOpen.isChecked()) {
                    str_canOpen = "1";
                }
                if (sw_canClose.isChecked()) {
                    str_canClose = "1";
                }
                if (sw_active.isChecked()) {
                    int_active = 0;
                }
                SQLdb database = new SQLdb(EditingEmployee.this);
                database.editEmployee(String.valueOf(empID), edt_firstname.getText().toString(),
                        edt_lastname.getText().toString(), edt_phoneNo.getText().toString(),
                        edt_email.getText().toString(), edt_startDate.getText().toString(),
                        str_canOpen, str_canClose, int_active);
                Toast.makeText(EditingEmployee.this, "Updated '" +
                        edt_firstname.getText().toString() + " " +
                        edt_lastname.getText().toString() + "'", Toast.LENGTH_SHORT).show();

                Intent selectEmp = new Intent(EditingEmployee.this, SelectEmployee.class);
                startActivity(selectEmp);
            }

        });


        delEm_bttn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                // creating alert dialog popup for employee delete confirmation
                final AlertDialog.Builder builder = new AlertDialog.Builder(EditingEmployee.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_delete_employee, viewGroup, false);

                Button cancelDel_bttn = dialogView.findViewById(R.id.cancelDel_bttn);
                Button confirmDel_bttn = dialogView.findViewById(R.id.confirmDel_bttn);
                TextView confirmDel_txt = dialogView.findViewById(R.id.confirmDel_txt);
                TextView confirmDelMsg_txt = dialogView.findViewById(R.id.confirmDelMsg_txt);

                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();

                confirmDel_txt.setText("Delete " + empFname + " " + empLname + "?");
                confirmDelMsg_txt.setText("WARNING\nAre you sure you want to delete this employee? This action cannot be reversed.");

                confirmDel_bttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // deleting the employee from employee database on confirmation
                        SQLdb db = new SQLdb(EditingEmployee.this);
                        db.deleteEmployee(String.valueOf(empID));

                        Toast.makeText(EditingEmployee.this, "Employee Deleted", Toast.LENGTH_SHORT).show();
                        Intent selectEmp = new Intent(EditingEmployee.this, SelectEmployee.class);
                        alertDialog.dismiss();
                        startActivity(selectEmp);
                    }
                });

                cancelDel_bttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });


    }

    //for email validation
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidName(String string) {
        return string.matches("[a-zA-Z]+");
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
