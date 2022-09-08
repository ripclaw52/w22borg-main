package com.example.w22borg.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.w22borg.MainActivity;
import com.example.w22borg.R;
import com.example.w22borg.SQLdb;
import com.example.w22borg.calendar.CalendarActivity;
import com.example.w22borg.data.EmployeeModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.Objects;

public class SelectEmployee extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    Button btn_viewAll;
    Button btn_open;
    Button btn_close;
    Button btn_active;
    Button btn_inactive;

    ListView lv_empList;
    EmployeeModel selectedEmployee;
    //btn to add employee
    FloatingActionButton toAdd;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_employee_view);

        Toolbar toolbar = findViewById(R.id.actionbar_Em_avail);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Employees");

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.open_nav_drawer, R.string.close_nav_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.to_emp);

        btn_viewAll = findViewById(R.id.btn_viewAll);
        btn_open = findViewById(R.id.btn_open);
        btn_close = findViewById(R.id.btn_close);
        btn_active = findViewById(R.id.btn_active);
        btn_inactive = findViewById(R.id.btn_inactive);
        lv_empList = findViewById(R.id.lv_empList);

        // get list of all employees
        SQLdb SQLdb = new SQLdb(SelectEmployee.this);
        List<EmployeeModel> empList = SQLdb.getEmpList();

        ArrayAdapter employeeArrayAdaptor = new ArrayAdapter<>(SelectEmployee.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(SelectEmployee.this, "Viewing All Employees", Toast.LENGTH_SHORT).show();

        // Click view all button
        btn_viewAll.setOnClickListener(v -> {
            //SQLdb SQLdb = new SQLdb(SelectEmployee.this);
            List<EmployeeModel> empList1 = SQLdb.getEmpList();

            ArrayAdapter employeeArrayAdaptor1 = new ArrayAdapter<>(SelectEmployee.this, android.R.layout.simple_list_item_1, empList1);
            lv_empList.setAdapter(employeeArrayAdaptor1);
            Toast.makeText(SelectEmployee.this, "Showing All Employees", Toast.LENGTH_SHORT).show();
        });

        // click view openers button
        btn_open.setOnClickListener(view -> {
            String searchParam = "open";
            List<EmployeeModel> empList12 = SQLdb.getEmpSearchList(searchParam);

            ArrayAdapter employeeArrayAdaptor12 = new ArrayAdapter<>(SelectEmployee.this, android.R.layout.simple_list_item_1, empList12);
            lv_empList.setAdapter(employeeArrayAdaptor12);
            Toast.makeText(SelectEmployee.this, "Showing Openers", Toast.LENGTH_SHORT).show();

        });

        // click view closers button
        btn_close.setOnClickListener(view -> {
            String searchParam = "close";
            List<EmployeeModel> empList13 = SQLdb.getEmpSearchList(searchParam);

            ArrayAdapter employeeArrayAdaptor13 = new ArrayAdapter<>(SelectEmployee.this, android.R.layout.simple_list_item_1, empList13);
            lv_empList.setAdapter(employeeArrayAdaptor13);
            Toast.makeText(SelectEmployee.this, "Showing Closers", Toast.LENGTH_SHORT).show();

        });

        // click view active button
        btn_active.setOnClickListener(view -> {
            String searchParam = "active";
            List<EmployeeModel> empList14 = SQLdb.getEmpSearchList(searchParam);

            ArrayAdapter employeeArrayAdaptor14 = new ArrayAdapter<>(SelectEmployee.this, android.R.layout.simple_list_item_1, empList14);
            lv_empList.setAdapter(employeeArrayAdaptor14);
            Toast.makeText(SelectEmployee.this, "Showing Active Employees", Toast.LENGTH_SHORT).show();

        });

        // click view inactive button
        btn_inactive.setOnClickListener(view -> {
            String searchParam = "inactive";
            List<EmployeeModel> empList15 = SQLdb.getEmpSearchList(searchParam);

            ArrayAdapter employeeArrayAdaptor15 = new ArrayAdapter<>(SelectEmployee.this, android.R.layout.simple_list_item_1, empList15);
            lv_empList.setAdapter(employeeArrayAdaptor15);
            Toast.makeText(SelectEmployee.this, "Showing Inactive Employees", Toast.LENGTH_SHORT).show();

        });
                lv_empList.setOnItemClickListener((parent, view, position, id) -> {
                    // get employee from click, pass employee ID to next screen
                    selectedEmployee = (EmployeeModel) parent.getItemAtPosition(position);

                    Intent viewEm = new Intent(SelectEmployee.this, ViewEmployee.class);
                    viewEm.putExtra("ID", selectedEmployee.getId());
                    viewEm.putExtra("firstname", selectedEmployee.getFirstName());
                    viewEm.putExtra("lastname", selectedEmployee.getLastName());
                    viewEm.putExtra("email", selectedEmployee.getEmail());
                    viewEm.putExtra("phoneNo", selectedEmployee.getPhoneNumber());
                    viewEm.putExtra("startDate", selectedEmployee.getDateOnStartingJob());
                    viewEm.putExtra("totalShift", selectedEmployee.getTotalWorkingShiftsSinceStartDate());
                    viewEm.putExtra("canOpen", selectedEmployee.isCanOpenStore());
                    viewEm.putExtra("canClose", selectedEmployee.isCanCloseStore());
                    viewEm.putExtra("Active", selectedEmployee.getIsAnActiveEmployee());
                    viewEm.putExtra("Monday", selectedEmployee.getMon());
                    viewEm.putExtra("Tuesday", selectedEmployee.getTue());
                    viewEm.putExtra("Wednesday", selectedEmployee.getWed());
                    viewEm.putExtra("Thursday", selectedEmployee.getThu());
                    viewEm.putExtra("Friday", selectedEmployee.getFri());
                    viewEm.putExtra("Saturday", selectedEmployee.getSat());
                    viewEm.putExtra("Sunday", selectedEmployee.getSun());

                    startActivity(viewEm);
                });
        toAdd = findViewById(R.id.to_add);
        toAdd.setOnClickListener(view -> startActivity(new Intent(SelectEmployee.this, AddEmployee.class)));
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.to_home) {
            startActivity(new Intent(SelectEmployee.this, MainActivity.class));
        }
        else if (id == R.id.to_sche) {
            startActivity(new Intent(SelectEmployee.this, CalendarActivity.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    //hitting back will closed navigation instead
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}