package com.example.w22borg.calendar;

import static com.example.w22borg.calendar.CalendarUtils.daysInWeekArray;
import static com.example.w22borg.calendar.CalendarUtils.monthYearFromDate;
import static com.example.w22borg.calendar.CalendarUtils.selectedDate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.w22borg.MainActivity;
import com.example.w22borg.R;
import com.example.w22borg.SQLdb;
import com.example.w22borg.data.EmployeeModel;
import com.example.w22borg.data.ShiftModel;
import com.example.w22borg.view.EditingEmployee;
import com.example.w22borg.view.SelectEmployee;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener
{
    private TextView monthYearText, textViewShiftType1, textViewShiftType2, warningsTxt;
    private RecyclerView calendarRecyclerView;
    private ListView employeeListView1, employeeListView2;
    private LinearLayout shiftLayout1, shiftLayoutTitle1, shiftLayout2, shiftLayoutTitle2;

    private LocalDate weekStart;

    public FloatingActionButton addEmployeeToShift1, addEmployeeToShift2;

    public ShiftModel openShiftModel, closeShiftModel, allDayShiftModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_activity_week_view);

        Toolbar toolbar = findViewById(R.id.actionbar_CalendarWeekActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Week");

        //set home button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initWidgets();

        setWeekView();
    }

    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);

        shiftLayout1 = findViewById(R.id.shiftLayout1);
        shiftLayout2 = findViewById(R.id.shiftLayout2);

        shiftLayoutTitle1 = findViewById(R.id.shiftLayoutTitle1);
        shiftLayoutTitle2 = findViewById(R.id.shiftLayoutTitle2);

        warningsTxt = findViewById(R.id.warningsTxt);

        employeeListView1 = findViewById(R.id.employeeListView1);
        employeeListView2 = findViewById(R.id.employeeListView2);

        textViewShiftType1 = findViewById(R.id.textViewShiftType1);
        textViewShiftType2 = findViewById(R.id.textViewShiftType2);

        addEmployeeToShift1 = findViewById(R.id.addingShiftActionButton1);
        addEmployeeToShift2 = findViewById(R.id.addingShiftActionButton2);
    }

    @SuppressLint("SetTextI18n")
    private void setWeekView()
    {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(selectedDate);

        //testStringTextView.setText(selectedDate.getDayOfWeek().toString());
        SQLdb database = new SQLdb(WeekViewActivity.this);
        ArrayList<ShiftModel> shiftsInWeek = database.getSelectedWeekShift(CalendarUtils.selectedDate);
        ArrayList<String> empsWorkingInWeek0 = database.getEmployeeIDsForShifts(days.get(0), days.get(days.size()-1), 0);
        ArrayList<String> empsWorkingInWeek1 = database.getEmployeeIDsForShifts(days.get(0), days.get(days.size()-1), 1);

        // get weekstart (Sunday)
        weekStart = shiftsInWeek.get(0).getLocalDate();

        //CalendarAdapter calendarAdapter = new CalendarAdapter(days, shiftsInWeek,this, this);
        CalendarAdapter calendarAdapter = new CalendarAdapter(days, shiftsInWeek, empsWorkingInWeek0, empsWorkingInWeek1,this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        warningsTxt.setText("Issues with shifts. Press to view.");

        setEventAdapter();
        database.close();
    }

    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setEventAdapter();
    }

    private void setEventAdapter()
    {
        boolean selectedDateStatus = CalendarUtils.isWeekEnd(CalendarUtils.selectedDate);

        if (selectedDateStatus) {
            textViewShiftType1.setText("All Day Shift");
            shiftLayout2.setVisibility(View.INVISIBLE);
        } else {
            shiftLayout2.setVisibility(View.VISIBLE);
            textViewShiftType1.setText("Open Shift");
            textViewShiftType2.setText("Close Shift");
        }
        SQLdb db = new SQLdb(WeekViewActivity.this);
        List<EmployeeModel> oplist = db.getOPlist(selectedDate.toString());
        ArrayAdapter OPArrayAdapt = new ArrayAdapter(WeekViewActivity.this, android.R.layout.simple_list_item_1, oplist);
        employeeListView1.setAdapter(OPArrayAdapt);

        String warningsStr = "";
        // check that each worker has at least one shift this week
        String checkRequire1 = db.checkOneShiftPerWeekStr(weekStart);
        if(!checkRequire1.isEmpty()) {
            warningsStr += "Workers with no shifts this week:\n" + checkRequire1 + "\n";
        }

        // check that each shift has at least 2 workers
        List<String> checkRequire2 = db.checkShiftsForMinWorkers(weekStart);
        String checkRequire2Str = checkRequire2.stream()
                .map(n -> String.valueOf(n))
                .collect(Collectors.joining(", "));
        if(!checkRequire2.isEmpty()) {
            warningsStr += "Shifts needing more workers:" + checkRequire2Str + "\n";
        }

        if(warningsStr.isEmpty()) {
            warningsTxt.setText("Valid work week");
            warningsTxt.setTextColor(Color.BLACK);
            warningsStr = "No issues with shifts";
            warningsTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
        } else {
            warningsTxt.setTextColor(Color.RED);
            warningsTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warning, 0, 0, 0);
        }

        List<EmployeeModel> cllist = db.getCLlist(selectedDate.toString());
        ArrayAdapter CLArrayAdapt = new ArrayAdapter(WeekViewActivity.this, android.R.layout.simple_list_item_1, cllist);
        employeeListView2.setAdapter(CLArrayAdapt);
        db.close();

        String finalWarningsStr = warningsStr;
        warningsTxt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                // creating alert dialog popup for employee delete confirmation
                final AlertDialog.Builder builder = new AlertDialog.Builder(WeekViewActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.popup_validate_week, viewGroup, false);

                TextView validateWeek_txt = dialogView.findViewById(R.id.validateWeek_txt);
                TextView validWeekMsg_txt = dialogView.findViewById(R.id.validWeekMsg_txt);
                Button validWeekOk_bttn = dialogView.findViewById(R.id.validWeekOk_bttn);

                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();

                validateWeek_txt.setText("Validate Week of " + weekStart.toString());
                validWeekMsg_txt.setText(finalWarningsStr);

                validWeekOk_bttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });

        SQLdb database = new SQLdb(WeekViewActivity.this);
        if (CalendarUtils.isWeekEnd(selectedDate)) {
            ShiftModel allDayShiftModel = database.getShiftType0(CalendarUtils.selectedDate);
            addEmployeeToShift1.setOnClickListener((view) -> {
                Intent allDayShiftEmployee = new Intent(WeekViewActivity.this, AddEmployeeToShiftAllDay.class);
                allDayShiftEmployee.putExtra("ID", allDayShiftModel.getId());
                allDayShiftEmployee.putExtra("LocalDate", String.valueOf(allDayShiftModel.getLocalDate()));
                allDayShiftEmployee.putExtra("shiftType", allDayShiftModel.getShiftType());
                allDayShiftEmployee.putExtra("employeeList", (Serializable) oplist);
                database.close();
                startActivity(allDayShiftEmployee);
            });
        } else {
            ShiftModel openShiftModel = database.getShiftType0(CalendarUtils.selectedDate);
            ShiftModel closeShiftModel = database.getShiftType1(CalendarUtils.selectedDate);
            addEmployeeToShift1.setOnClickListener((view) -> {
                Intent openShiftEmployee = new Intent(WeekViewActivity.this, AddEmployeeToShiftOpen.class);
                openShiftEmployee.putExtra("ID", openShiftModel.getId());
                openShiftEmployee.putExtra("LocalDate", String.valueOf(openShiftModel.getLocalDate()));
                openShiftEmployee.putExtra("shiftType", openShiftModel.getShiftType());
                openShiftEmployee.putExtra("employeeList", (Serializable) oplist);
                //pass closing list
                openShiftEmployee.putExtra("shiftType2", closeShiftModel.getShiftType());
                openShiftEmployee.putExtra("employeeList2", (Serializable) cllist);
                database.close();
                startActivity(openShiftEmployee);
            });
            //ShiftModel closeShiftModel = database.getShiftType1(CalendarUtils.selectedDate);
            addEmployeeToShift2.setOnClickListener((view) -> {
                Intent closeShiftEmployee = new Intent(WeekViewActivity.this, AddEmployeeToShiftClose.class);
                closeShiftEmployee.putExtra("ID", closeShiftModel.getId());
                closeShiftEmployee.putExtra("LocalDate", String.valueOf(closeShiftModel.getLocalDate()));
                closeShiftEmployee.putExtra("shiftType", closeShiftModel.getShiftType());
                closeShiftEmployee.putExtra("employeeList", (Serializable) cllist);
                //pass opening list
                closeShiftEmployee.putExtra("shiftType2", closeShiftModel.getShiftType());
                closeShiftEmployee.putExtra("employeeList2", (Serializable) oplist);
                database.close();
                startActivity(closeShiftEmployee);
            });
        }
        database.close();
    }

    public void dailyAction(View view)
    {
        //
    }
}