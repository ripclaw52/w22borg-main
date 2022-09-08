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

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddEmployeeToShiftClose extends AppCompatActivity {

    Button btn_viewAll, btn_open, btn_close, btn_active, btn_inactive, confirm, cancel;
    SwitchCompat sw_busyDay;
    ListView lv_empList, workingEmployeesInShiftClose;
    LinearLayout linearLayout, linearLayout2, confirmation_selection;
    EmployeeModel selectedEmployee;
    ShiftModel shiftModel;

    public int shiftType, opSType;
    public int numEmpsAllowed = 2;
    public String shiftID, opID;
    public LocalDate shiftDate, opDate;
    public List<EmployeeModel> shiftEmployees, opEmployees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee_to_shift_close);

        Toolbar toolbar = findViewById(R.id.actionbar_addEmployeeToShift);
        setSupportActionBar(toolbar);

        initWidgets();

        Intent prevIntent = getIntent();
        shiftID = String.valueOf(prevIntent.getIntExtra("ID", 0));
        shiftDate = LocalDate.parse(prevIntent.getStringExtra("LocalDate"));
        shiftType = prevIntent.getIntExtra("shiftType", 1);
        shiftEmployees = (List<EmployeeModel>) prevIntent.getSerializableExtra("employeeList");

        opID = String.valueOf(prevIntent.getIntExtra("ID", 0));
        opDate = LocalDate.parse(prevIntent.getStringExtra("LocalDate"));
        opSType = prevIntent.getIntExtra("shiftType2", 0);
        opEmployees = (List<EmployeeModel>) prevIntent.getSerializableExtra("employeeList2");
        Objects.requireNonNull(getSupportActionBar()).setTitle(CalendarUtils.monthDayFromDate(CalendarUtils.selectedDate));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get list of all employees
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftClose.this);

        //ShiftModel shiftModel = SQLdb.getShiftType1(CalendarUtils.selectedDate);
        //ArrayList<EmployeeModel> shiftEmployees = new ArrayList<>(shiftModel.getEmployeeModels());

        ArrayAdapter<EmployeeModel> shiftModelAdapterOpen = new ArrayAdapter<>(AddEmployeeToShiftClose.this, android.R.layout.simple_list_item_1, shiftEmployees);
        workingEmployeesInShiftClose.setAdapter(shiftModelAdapterOpen);

        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        List<EmployeeModel> empList = SQLdb.getEmpAvailListCL(dayOfWeek);
        empList.removeAll(shiftEmployees);
        empList.removeAll(opEmployees);
        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftClose.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);

        //SQLdb.close();

        if (shiftEmployees.size() > 2) {
            sw_busyDay.setChecked(true);
            numEmpsAllowed = 3;
        }

        lv_empList.setOnItemClickListener((parent, view, position, id) -> {
            if (workingEmployeesInShiftClose.getAdapter().getCount() < numEmpsAllowed) {
            selectedEmployee = (EmployeeModel) parent.getItemAtPosition(position);
            shiftEmployees.add(selectedEmployee);
            empList.remove(selectedEmployee);
            ArrayAdapter<EmployeeModel> workingEmployeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftClose.this, android.R.layout.simple_list_item_1, shiftEmployees);
            workingEmployeesInShiftClose.setAdapter(workingEmployeeArrayAdaptor);

            Toast.makeText(AddEmployeeToShiftClose.this, "Added " + selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName() + " To Shift", Toast.LENGTH_SHORT).show();}
        });

        workingEmployeesInShiftClose.setOnItemClickListener((parent, view, position, id) -> {
            selectedEmployee = (EmployeeModel) parent.getItemAtPosition(position);
            shiftEmployees.remove(selectedEmployee);
            empList.add(selectedEmployee);
            ArrayAdapter<EmployeeModel> workingEmployeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftClose.this, android.R.layout.simple_list_item_1, shiftEmployees);
            workingEmployeesInShiftClose.setAdapter(workingEmployeeArrayAdaptor);

            Toast.makeText(AddEmployeeToShiftClose.this, "Removed " + selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName() + " from Shift", Toast.LENGTH_SHORT).show();
        });
        SQLdb.close();
    }

    private void initWidgets() {
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout2 = findViewById(R.id.linearLayout2);
        confirmation_selection = findViewById(R.id.confirmation_selection);

        sw_busyDay = findViewById(R.id.sw_busyDay);

        lv_empList = findViewById(R.id.lv_empList);
        workingEmployeesInShiftClose = findViewById(R.id.workingEmployeesInShiftClose);

        btn_viewAll = findViewById(R.id.btn_viewAll_cs);
        btn_open = findViewById(R.id.btn_open_cs);
        btn_close = findViewById(R.id.btn_close_cs);
        btn_active = findViewById(R.id.btn_active_cs);
        btn_inactive = findViewById(R.id.btn_inactive_cs);

        confirm = findViewById(R.id.confirm_cs);
        cancel = findViewById(R.id.cancel_cs);
    }

    public void switchIsBusyDay(View view) {
        if (sw_busyDay.isChecked()){
            numEmpsAllowed = 3;
        } else { numEmpsAllowed = 2; }
    }

    public void buttonPressViewAll(View view) {
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftClose.this);
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        List<EmployeeModel> empList = SQLdb.getEmpAvailListCL(dayOfWeek);

        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftClose.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(AddEmployeeToShiftClose.this, "Showing All Employees", Toast.LENGTH_SHORT).show();
        SQLdb.close();
    }

    public void buttonPressOpenTrained(View view) {
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftClose.this);
        String searchParam = "open";
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        List<EmployeeModel> empList = SQLdb.getEmpAvailSearchListCL(searchParam, dayOfWeek);

        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftClose.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(AddEmployeeToShiftClose.this, "Showing Openers", Toast.LENGTH_SHORT).show();
        SQLdb.close();
    }

    public void buttonPressCloseTrained(View view) {
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftClose.this);
        String searchParam = "close";
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        List<EmployeeModel> empList = SQLdb.getEmpAvailSearchListCL(searchParam, dayOfWeek);

        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftClose.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(AddEmployeeToShiftClose.this, "Showing Closers", Toast.LENGTH_SHORT).show();
        SQLdb.close();
    }

    public void buttonPressActive(View view) {
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftClose.this);
        String searchParam = "active";
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        List<EmployeeModel> empList = SQLdb.getEmpAvailSearchListCL(searchParam, dayOfWeek);

        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftClose.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(AddEmployeeToShiftClose.this, "Showing Active Employees", Toast.LENGTH_SHORT).show();
        SQLdb.close();
    }

    public void buttonPressInactive(View view) {
        SQLdb SQLdb = new SQLdb(AddEmployeeToShiftClose.this);
        String searchParam = "inactive";
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        List<EmployeeModel> empList = SQLdb.getEmpAvailSearchListCL(searchParam, dayOfWeek);

        ArrayAdapter<EmployeeModel> employeeArrayAdaptor = new ArrayAdapter<>(AddEmployeeToShiftClose.this, android.R.layout.simple_list_item_1, empList);
        lv_empList.setAdapter(employeeArrayAdaptor);
        Toast.makeText(AddEmployeeToShiftClose.this, "Showing Inactive Employees", Toast.LENGTH_SHORT).show();
        SQLdb.close();
    }

    public void AddToDatabase(View view) {
        SQLdb database = new SQLdb(AddEmployeeToShiftClose.this);
        ShiftModel shiftModel = database.getShiftType1(CalendarUtils.selectedDate);

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
        Intent i = new Intent(AddEmployeeToShiftClose.this, WeekViewActivity.class);
        startActivity(i);
    }


    public void ConfirmEmployeeShiftChangesOpen(View view) {
        int countCanClose = 0;
        for (EmployeeModel emp : shiftEmployees) {
            if (emp.isCanCloseStore()) {
                countCanClose++;
            }
        }
        System.out.println("Count can_close: " + countCanClose);
        if (countCanClose < 1) {
            // creating alert dialog popup for confirming shift with no openers
            final AlertDialog.Builder builder = new AlertDialog.Builder(AddEmployeeToShiftClose.this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_unqualified, viewGroup, false);

            Button cancelShift_bttn = dialogView.findViewById(R.id.cancelShift_bttn);
            Button confirmShift_bttn = dialogView.findViewById(R.id.confirmShift_bttn);
            TextView unqualified_txt = dialogView.findViewById(R.id.unqualified_txt);
            TextView unqualifiedMsg_txt = dialogView.findViewById(R.id.unqualifiedMsg_txt);

            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();

            unqualified_txt.setText("No Closers");
            unqualifiedMsg_txt.setText("WARNING\nAre you sure you want to confirm this shift? There are no qualified closers.");

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
        startActivity(new Intent(AddEmployeeToShiftClose.this, WeekViewActivity.class));
    }
}