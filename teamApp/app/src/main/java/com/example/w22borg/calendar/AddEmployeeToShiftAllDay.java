package com.example.w22borg.calendar;

import static com.example.w22borg.calendar.CalendarUtils.selectedDate;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.example.w22borg.R;
import com.example.w22borg.SQLdb;
import com.example.w22borg.data.EmployeeModel;
import com.example.w22borg.data.ShiftModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddEmployeeToShiftAllDay extends AppCompatActivity {

    Button btn_viewAll, btn_open, btn_close, btn_active, btn_inactive, confirm, cancel;
    SwitchCompat sw_busyDay;
    ListView lv_empList, workingEmployeesInShiftAllDay;
    LinearLayout linearLayout, linearLayout2, confirmation_selection;
    EmployeeModel selectedEmployee;
    ShiftModel shiftModel;

    public int shiftType, numEmpsAllowed = 2;
    public String shiftID;
    public LocalDate shiftDate;
    public List<EmployeeModel> shiftEmployees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee_to_shift_all_day);

        Toolbar toolbar = findViewById(R.id.actionbar_addEmployeeToShift);
        setSupportActionBar(toolbar);

        initWidgets();

        Intent prevIntent = getIntent();
        shiftID = String.valueOf(prevIntent.getIntExtra("ID", 0));
        shiftDate = LocalDate.parse(prevIntent.getStringExtra("LocalDate"));
        shiftType = prevIntent.getIntExtra("shiftType", 0);
        shiftEmployees = (List<EmployeeModel>) prevIntent.getSerializableExtra("employeeList");

        Objects.requireNonNull(getSupportActionBar()).setTitle(CalendarUtils.monthDayFromDate(CalendarUtils.selectedDate));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get list of all employees
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftAllDay.this);

        //ShiftModel shiftModel = SQLdb.getShiftType0(CalendarUtils.selectedDate);
        //ArrayList<EmployeeModel> shiftEmployees = new ArrayList<>(shiftModel.getEmployeeModels());

        ArrayAdapter<EmployeeModel> shiftModelAdapterOpen = new ArrayAdapter<>(AddEmployeeToShiftAllDay.this, android.R.layout.simple_list_item_1, shiftEmployees);
        workingEmployeesInShiftAllDay.setAdapter(shiftModelAdapterOpen);

        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        //Toast.makeText(AddEmployeeToShiftAllDay.this, dayOfWeek, Toast.LENGTH_SHORT).show();
        List<EmployeeModel> empList = SQLdb.getEmpAvailListAD(dayOfWeek);
        empList.removeAll(shiftEmployees);
        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftAllDay.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        //SQLdb.close();

        if (shiftEmployees.size() > 2) {
            sw_busyDay.setChecked(true);
            numEmpsAllowed = 3;
        }

        lv_empList.setOnItemClickListener((parent, view, position, id) -> {
            if (workingEmployeesInShiftAllDay.getAdapter().getCount() < numEmpsAllowed) {
            selectedEmployee = (EmployeeModel) parent.getItemAtPosition(position);
            shiftEmployees.add(selectedEmployee);
            empList.remove(selectedEmployee);
            ArrayAdapter<EmployeeModel> workingEmployeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftAllDay.this, android.R.layout.simple_list_item_1, shiftEmployees);
            workingEmployeesInShiftAllDay.setAdapter(workingEmployeeArrayAdaptor);

            Toast.makeText(AddEmployeeToShiftAllDay.this, "Added " + selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName() + " To Shift", Toast.LENGTH_SHORT).show();}
        });

        workingEmployeesInShiftAllDay.setOnItemClickListener((parent, view, position, id) -> {
            selectedEmployee = (EmployeeModel) parent.getItemAtPosition(position);
            shiftEmployees.remove(selectedEmployee);
            empList.add(selectedEmployee);
            ArrayAdapter<EmployeeModel> workingEmployeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftAllDay.this, android.R.layout.simple_list_item_1, shiftEmployees);
            workingEmployeesInShiftAllDay.setAdapter(workingEmployeeArrayAdaptor);

            Toast.makeText(AddEmployeeToShiftAllDay.this, "Removed " + selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName() + " from Shift", Toast.LENGTH_SHORT).show();
        });
        SQLdb.close();
    }

    private void initWidgets() {
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout2 = findViewById(R.id.linearLayout2);
        confirmation_selection = findViewById(R.id.confirmation_selection);

        sw_busyDay = findViewById(R.id.sw_busyDay);

        lv_empList = findViewById(R.id.lv_empList);
        workingEmployeesInShiftAllDay = findViewById(R.id.workingEmployeesInShiftAllDay);

        btn_viewAll = findViewById(R.id.btn_viewAll_ads);
        btn_open = findViewById(R.id.btn_open_ads);
        btn_close = findViewById(R.id.btn_close_ads);
        btn_active = findViewById(R.id.btn_active_ads);
        btn_inactive = findViewById(R.id.btn_inactive_ads);

        confirm = findViewById(R.id.confirm_ads);
        cancel = findViewById(R.id.cancel_ads);
    }

    public void switchIsBusyDay(View view) {
        if (sw_busyDay.isChecked()){
            numEmpsAllowed = 3;
        } else { numEmpsAllowed = 2; }
    }

    public void buttonPressViewAll(View view) {
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftAllDay.this);
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        List<EmployeeModel> empList = SQLdb.getEmpAvailListAD(dayOfWeek);

        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftAllDay.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(AddEmployeeToShiftAllDay.this, "Showing All Employees", Toast.LENGTH_SHORT).show();
        SQLdb.close();
    }

    public void buttonPressOpenTrained(View view) {
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftAllDay.this);
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        String searchParam = "open";
        List<EmployeeModel> empList = SQLdb.getEmpAvailSearchListAD(searchParam, dayOfWeek);

        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftAllDay.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(AddEmployeeToShiftAllDay.this, "Showing Openers", Toast.LENGTH_SHORT).show();
        SQLdb.close();
    }

    public void buttonPressCloseTrained(View view) {
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftAllDay.this);
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        String searchParam = "close";
        List<EmployeeModel> empList = SQLdb.getEmpAvailSearchListAD(searchParam, dayOfWeek);

        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftAllDay.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(AddEmployeeToShiftAllDay.this, "Showing Closers", Toast.LENGTH_SHORT).show();
        SQLdb.close();
    }

    public void buttonPressActive(View view) {
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftAllDay.this);
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        String searchParam = "active";
        List<EmployeeModel> empList = SQLdb.getEmpAvailSearchListAD(searchParam, dayOfWeek);

        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftAllDay.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(AddEmployeeToShiftAllDay.this, "Showing Active Employees", Toast.LENGTH_SHORT).show();
        SQLdb.close();
    }

    public void buttonPressInactive(View view) {
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftAllDay.this);
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        String searchParam = "inactive";
        List<EmployeeModel> empList = SQLdb.getEmpAvailSearchListAD(searchParam, dayOfWeek);

        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftAllDay.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(AddEmployeeToShiftAllDay.this, "Showing Inactive Employees", Toast.LENGTH_SHORT).show();
        SQLdb.close();
    }

    public void AddToDatabase(View view) {
        SQLdb database = new SQLdb(AddEmployeeToShiftAllDay.this);
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
        Intent i = new Intent(AddEmployeeToShiftAllDay.this, WeekViewActivity.class);
        startActivity(i);
    }


    public void ConfirmEmployeeShiftChangesOpen(View view) {
        int countCanOpen = 0, countCanClose = 0;
        for (EmployeeModel emp : shiftEmployees) {
            if (emp.isCanOpenStore()) {
                countCanOpen++;
            }
            if (emp.isCanCloseStore()) {
                countCanClose++;
            }
        }
        System.out.println("Count can_open/can_close: " + countCanOpen + ", " + countCanClose);
        if (countCanOpen < 1 || countCanClose < 1) {
            // creating alert dialog popup for confirming shift with no openers
            final AlertDialog.Builder builder = new AlertDialog.Builder(AddEmployeeToShiftAllDay.this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_unqualified, viewGroup, false);

            Button cancelShift_bttn = dialogView.findViewById(R.id.cancelShift_bttn);
            Button confirmShift_bttn = dialogView.findViewById(R.id.confirmShift_bttn);
            TextView unqualified_txt = dialogView.findViewById(R.id.unqualified_txt);
            TextView unqualifiedMsg_txt = dialogView.findViewById(R.id.unqualifiedMsg_txt);

            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();

            if (countCanOpen < 1 && countCanClose > 0) {
                unqualified_txt.setText("No Openers");
                unqualifiedMsg_txt.setText("WARNING\nAre you sure you want to confirm this shift? There are no qualified openers.");
            } else if (countCanClose < 1 && countCanOpen > 0) {
                unqualified_txt.setText("No Closers");
                unqualifiedMsg_txt.setText("WARNING\nAre you sure you want to confirm this shift? There are no qualified closers.");
            } else {
                unqualified_txt.setText("No Openers or Closers");
                unqualifiedMsg_txt.setText("WARNING\nAre you sure you want to confirm this shift? There are no qualified openers or closers.");
            }

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

    public void RemoveEmployeeShiftChangesOpen(View view) {
        startActivity(new Intent(AddEmployeeToShiftAllDay.this, WeekViewActivity.class));
    }
}
