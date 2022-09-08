package com.example.w22borg.calendar;

import static com.example.w22borg.calendar.CalendarUtils.selectedDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.w22borg.MainActivity;
import com.example.w22borg.R;
import com.example.w22borg.SQLdb;
import com.example.w22borg.data.EmployeeModel;
import com.example.w22borg.data.ShiftModel;
import com.example.w22borg.view.AddEmployee;
import com.example.w22borg.view.EditingEmployee;
import com.example.w22borg.view.SelectEmployee;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddEmployeeToShiftOpen extends AppCompatActivity {

    Button btn_viewAll, btn_open, btn_close, btn_active, btn_inactive, confirm, cancel;
    SwitchCompat sw_busyDay;
    ListView lv_empList, workingEmployeesInShiftOpen;
    LinearLayout linearLayout, linearLayout2, confirmation_selection;
    EmployeeModel selectedEmployee;
    ShiftModel shiftModel;

    public int shiftType, closeSType;
    public String shiftID, closeID;
    public LocalDate shiftDate, closeDate;
    public List<EmployeeModel> shiftEmployees, closeEmployees;
    public int numEmpsAllowed = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee_to_shift_open);

        Toolbar toolbar = findViewById(R.id.actionbar_addEmployeeToShift);
        setSupportActionBar(toolbar);

        initWidgets();

        Intent prevIntent = getIntent();
        shiftID = String.valueOf(prevIntent.getIntExtra("ID", 0));
        shiftDate = LocalDate.parse(prevIntent.getStringExtra("LocalDate"));
        shiftType = prevIntent.getIntExtra("shiftType", 0);
        shiftEmployees = (List<EmployeeModel>) prevIntent.getSerializableExtra("employeeList");

        closeID = String.valueOf(prevIntent.getIntExtra("ID", 0));
        closeDate = LocalDate.parse(prevIntent.getStringExtra("LocalDate"));
        closeSType = prevIntent.getIntExtra("shiftType2", 1);
        closeEmployees = (List<EmployeeModel>) prevIntent.getSerializableExtra("employeeList2");

        Objects.requireNonNull(getSupportActionBar()).setTitle(CalendarUtils.monthDayFromDate(CalendarUtils.selectedDate));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get list of all employees
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftOpen.this);

        //ShiftModel shiftModel = SQLdb.getShiftType0(CalendarUtils.selectedDate);
        //ArrayList<EmployeeModel> shiftEmployees = new ArrayList<>(shiftModel.getEmployeeModels());

        ArrayAdapter<EmployeeModel> shiftModelAdapterOpen = new ArrayAdapter<>(AddEmployeeToShiftOpen.this, android.R.layout.simple_list_item_1, shiftEmployees);
        workingEmployeesInShiftOpen.setAdapter(shiftModelAdapterOpen);

        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        //Toast.makeText(AddEmployeeToShiftOpen.this, dayOfWeek, Toast.LENGTH_SHORT).show();
        String shiftType = "openShift";
        List<EmployeeModel> empList = SQLdb.getEmpAvailListOP(dayOfWeek);
        empList.removeAll(shiftEmployees);
        empList.removeAll(closeEmployees);
        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftOpen.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);

        //SQLdb.close();

        if (shiftEmployees.size() > 2) {
            sw_busyDay.setChecked(true);
            numEmpsAllowed = 3;
        }

        lv_empList.setOnItemClickListener((parent, view, position, id) -> {
            if (workingEmployeesInShiftOpen.getAdapter().getCount() < numEmpsAllowed) {
            selectedEmployee = (EmployeeModel) parent.getItemAtPosition(position);
            shiftEmployees.add(selectedEmployee);
            empList.remove(selectedEmployee);
            ArrayAdapter<EmployeeModel> workingEmployeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftOpen.this, android.R.layout.simple_list_item_1, shiftEmployees);
            workingEmployeesInShiftOpen.setAdapter(workingEmployeeArrayAdaptor);
            Toast.makeText(AddEmployeeToShiftOpen.this, "Added " + selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName() + " To Shift", Toast.LENGTH_SHORT).show();}
        });

        workingEmployeesInShiftOpen.setOnItemClickListener((parent, view, position, id) -> {
            selectedEmployee = (EmployeeModel) parent.getItemAtPosition(position);
            shiftEmployees.remove(selectedEmployee);
            empList.add(selectedEmployee);
            ArrayAdapter<EmployeeModel> workingEmployeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftOpen.this, android.R.layout.simple_list_item_1, shiftEmployees);
            workingEmployeesInShiftOpen.setAdapter(workingEmployeeArrayAdaptor);

            Toast.makeText(AddEmployeeToShiftOpen.this, "Removed " + selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName() + " from Shift", Toast.LENGTH_SHORT).show();
        });
        SQLdb.close();
    }

    private void initWidgets() {
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout2 = findViewById(R.id.linearLayout2);
        confirmation_selection = findViewById(R.id.confirmation_selection);

        sw_busyDay = findViewById(R.id.sw_busyDay);

        lv_empList = findViewById(R.id.lv_empList);
        workingEmployeesInShiftOpen = findViewById(R.id.workingEmployeesInShiftOpen);

        btn_viewAll = findViewById(R.id.btn_viewAll_os);
        btn_open = findViewById(R.id.btn_open_os);
        btn_close = findViewById(R.id.btn_close_os);
        btn_active = findViewById(R.id.btn_active_os);
        btn_inactive = findViewById(R.id.btn_inactive_os);

        confirm = findViewById(R.id.confirm_os);
        cancel = findViewById(R.id.cancel_os);
    }

    public void switchIsBusyDay(View view) {
        if (sw_busyDay.isChecked()){
            numEmpsAllowed = 3;
        } else { numEmpsAllowed = 2; }
    }

    public void buttonPressViewAll(View view) {
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftOpen.this);
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        List<EmployeeModel> empList = SQLdb.getEmpAvailListOP(dayOfWeek);

        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftOpen.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(AddEmployeeToShiftOpen.this, "Showing All Employees", Toast.LENGTH_SHORT).show();
        SQLdb.close();
    }

    public void buttonPressOpenTrained(View view) {
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftOpen.this);
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        String searchParam = "open";
        List<EmployeeModel> empList = SQLdb.getEmpAvailSearchListOP(searchParam, dayOfWeek);

        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftOpen.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(AddEmployeeToShiftOpen.this, "Showing Openers", Toast.LENGTH_SHORT).show();
        SQLdb.close();
    }

    public void buttonPressCloseTrained(View view) {
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftOpen.this);
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        String searchParam = "close";
        List<EmployeeModel> empList = SQLdb.getEmpAvailSearchListOP(searchParam, dayOfWeek);

        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftOpen.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(AddEmployeeToShiftOpen.this, "Showing Closers", Toast.LENGTH_SHORT).show();
        SQLdb.close();
    }

    public void buttonPressActive(View view) {
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftOpen.this);
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        String searchParam = "active";
        List<EmployeeModel> empList = SQLdb.getEmpAvailSearchListOP(searchParam, dayOfWeek);

        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftOpen.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(AddEmployeeToShiftOpen.this, "Showing Active Employees", Toast.LENGTH_SHORT).show();
        SQLdb.close();
    }

    public void buttonPressInactive(View view) {
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftOpen.this);
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        String searchParam = "inactive";
        List<EmployeeModel> empList = SQLdb.getEmpAvailSearchListOP(searchParam, dayOfWeek);

        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftOpen.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(AddEmployeeToShiftOpen.this, "Showing Inactive Employees", Toast.LENGTH_SHORT).show();
        SQLdb.close();
    }

    public void AddToDatabase(View view) {
        SQLdb database = new SQLdb(AddEmployeeToShiftOpen.this);
        ShiftModel shiftModel = database.getShiftType0(CalendarUtils.selectedDate);

        List<EmployeeModel> workingEmployeeList = shiftEmployees.stream().distinct().collect(Collectors.toList());
        //List<EmployeeModel> workingEmployeeList = new ArrayList<>(shiftEmployees);

        if (workingEmployeeList.isEmpty()) {
            System.out.println("List is empty!!!");
            database.editShift(
                    String.valueOf(shiftModel.getId()),
                    shiftModel.getLocalDate().toString(),
                    shiftModel.getShiftType(),
                    null,
                    null,
                    null
            );
        } else {
            switch (workingEmployeeList.size()) {
                case 1:
                    System.out.println("1 employee in the list");
                    database.editShift(
                            String.valueOf(shiftModel.getId()),
                            shiftModel.getLocalDate().toString(),
                            shiftModel.getShiftType(),
                            workingEmployeeList.get(0).getId(),
                            null,
                            null
                    );
                    database.close();
                    break;
                case 2:
                    System.out.println("2 employee in the list");
                    database.editShift(
                            String.valueOf(shiftModel.getId()),
                            shiftModel.getLocalDate().toString(),
                            shiftModel.getShiftType(),
                            workingEmployeeList.get(0).getId(),
                            workingEmployeeList.get(1).getId(),
                            null
                    );
                    database.close();
                    break;
                case 3:
                    System.out.println("3 employee in the list");
                    database.editShift(
                            String.valueOf(shiftModel.getId()),
                            shiftModel.getLocalDate().toString(),
                            shiftModel.getShiftType(),
                            workingEmployeeList.get(0).getId(),
                            workingEmployeeList.get(1).getId(),
                            workingEmployeeList.get(2).getId()
                    );
                    database.close();
                    break;
                default:
                    System.out.println("Too many employees in the list");
                    database.close();
                    break;
            }
        }
        Intent i = new Intent(AddEmployeeToShiftOpen.this, WeekViewActivity.class);
        startActivity(i);
    }


    public void ConfirmEmployeeShiftChangesOpen(View view) {
        int countCanOpen = 0;
        for (EmployeeModel emp : shiftEmployees) {
            if (emp.isCanOpenStore()) {
                countCanOpen++;
            }
        }
        System.out.println("Count can_open: " + countCanOpen);
        if (countCanOpen < 1) {
            // creating alert dialog popup for confirming shift with no openers
            final AlertDialog.Builder builder = new AlertDialog.Builder(AddEmployeeToShiftOpen.this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_unqualified, viewGroup, false);

            Button cancelShift_bttn = dialogView.findViewById(R.id.cancelShift_bttn);
            Button confirmShift_bttn = dialogView.findViewById(R.id.confirmShift_bttn);
            TextView unqualified_txt = dialogView.findViewById(R.id.unqualified_txt);
            TextView unqualifiedMsg_txt = dialogView.findViewById(R.id.unqualifiedMsg_txt);

            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();

            unqualified_txt.setText("No Openers");
            unqualifiedMsg_txt.setText("WARNING\nAre you sure you want to confirm this shift? There are no qualified openers.");

            confirmShift_bttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // deleting the employee from employee database on confirmation
                    AddToDatabase(view);
                }
            });

            cancelShift_bttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();

                }
            });

            alertDialog.show();
        } else {
            AddToDatabase(view);
        }
    }

    public void RemoveEmployeeShiftChangesOpen (View view){
        startActivity(new Intent(AddEmployeeToShiftOpen.this, WeekViewActivity.class));
    }
}