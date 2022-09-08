package com.example.w22borg.calendar;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.example.w22borg.calendar.CalendarUtils.daysInMonthArray;
import static com.example.w22borg.calendar.CalendarUtils.isWeekEnd;
import static com.example.w22borg.calendar.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.w22borg.MainActivity;
import com.example.w22borg.R;
import com.example.w22borg.SQLdb;
import com.example.w22borg.data.ShiftModel;
import com.example.w22borg.view.AddEmployee;
import com.example.w22borg.view.SelectEmployee;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener,
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_calendar_view);

        Toolbar toolbar = findViewById(R.id.actionbar_CalendarActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Calendar");

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.open_nav_drawer, R.string.close_nav_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.to_sche);

        initWidgets();
        CalendarUtils.selectedDate = LocalDate.now();

        initializeMonthlyCalendarShift();
        setMonthView();
    }

    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    public void initializeMonthlyCalendarShift()
    {
        SQLdb database = new SQLdb(CalendarActivity.this);

        ArrayList<LocalDate> daysInMonth = daysInMonthArray();

        for (LocalDate localDate: daysInMonth) {
            if (CalendarUtils.isWeekEnd(localDate)) {
                try {
                    ShiftModel shiftModelAllDay = new ShiftModel(1, localDate, 0);
                    boolean success = database.addShift(Objects.requireNonNull(shiftModelAllDay));
                } catch (SQLiteConstraintException sqLiteConstraintException) {
                    System.out.println(localDate.toString() + " Already exists for weekend shift!");
                } catch (Exception e) {
                    System.out.println("Something else went wrong!");
                }
            } else {
                try {
                    ShiftModel shiftModelOpen = new ShiftModel(1, localDate, 0);
                    boolean success = database.addShift(Objects.requireNonNull(shiftModelOpen));
                } catch (SQLiteConstraintException sqLiteConstraintException) {
                    System.out.println(localDate.toString() + " Already exists for open shift!");
                } catch (Exception e) {
                    System.out.println("Something else went wrong!");
                }

                try {
                    ShiftModel shiftModelClose = new ShiftModel(1, localDate, 1);
                    boolean success1 = database.addShift(Objects.requireNonNull(shiftModelClose));
                } catch (SQLiteConstraintException sqLiteConstraintException) {
                    System.out.println(localDate.toString() + " Already exists for close shift!");
                } catch (Exception e) {
                    System.out.println("Something else went wrong!");
                }
            }
        }
        database.close();
    }

    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();

        SQLdb database = new SQLdb(CalendarActivity.this);
        ArrayList<ShiftModel> shiftsInMonth = database.getSelectedMonthShift();
        ArrayList<String> empsWorkingInMonth0 = database.getEmployeeIDsForShifts(daysInMonth.get(0), daysInMonth.get(daysInMonth.size()-1), 0);
        ArrayList<String> empsWorkingInMonth1 = database.getEmployeeIDsForShifts(daysInMonth.get(0), daysInMonth.get(daysInMonth.size()-1), 1);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, shiftsInMonth, empsWorkingInMonth0, empsWorkingInMonth1, this);
        //CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, shiftsInMonth, this, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        database.close();
    }

    public void previousMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
        initializeMonthlyCalendarShift();
    }

    public void nextMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
        initializeMonthlyCalendarShift();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        if(date != null)
        {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

    public void weeklyAction(View view)
    {
        startActivity(new Intent(this, WeekViewActivity.class));
    }

    public void generatePDF(View view) {
        // creating an object variable for PDF document
        PdfDocument pdfDocument = new PdfDocument();

        Paint title = new Paint();
        Paint line = new Paint();
        line.setStrokeWidth((float)1.75);
        Paint shift = new Paint();

        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(792, 1120, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        SQLdb database = new SQLdb(CalendarActivity.this);
        YearMonth yearMonth = YearMonth.from(CalendarUtils.selectedDate);
        LocalDate firstOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1);

        // creating a variable for canvas from our page of PDF.
        Canvas canvas = myPage.getCanvas();
        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setTextSize(35);
        canvas.drawText(monthYearFromDate(CalendarUtils.selectedDate) + " Worker Schedule", 396, 100, title);

        shift.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        shift.setTextSize(15);

        canvas.drawText("DATE", 30, 155, shift);
        canvas.drawLine(75, 135, 75, 165 + yearMonth.lengthOfMonth() * 30, line);
        canvas.drawText("OPENING/ALL DAY SHIFT", 80, 155, shift);
        canvas.drawLine(417, 135, 417, 165 + yearMonth.lengthOfMonth() * 30, line);
        canvas.drawText("CLOSING SHIFT", 423, 155, shift);

        shift.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        for (int day = 1; day < yearMonth.lengthOfMonth() + 1; day++) {
            canvas.drawLine(25, 135 + day * 30, 767, 135 + day * 30, line);
            canvas.drawText(String.format("%02d", day), 45, 155 + day * 30, shift);
            canvas.drawText(database.getEmployeeNamesForShifts(
                    CalendarUtils.selectedDate.withDayOfMonth(day), 0),
                    80, 155 + day * 30, shift);
            canvas.drawText(database.getEmployeeNamesForShifts(
                    CalendarUtils.selectedDate.withDayOfMonth(day), 1),
                    423, 155 + day * 30, shift);
        }


        pdfDocument.finishPage(myPage);

        File file = new File(getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOCUMENTS), monthYearFromDate(CalendarUtils.selectedDate) + " Schedule.pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));

            Toast.makeText(CalendarActivity.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(CalendarActivity.this, "Unsuccessful PDF generation.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        pdfDocument.close();
    }

    /*
    public void shiftPopUp(View view)
    {
        //Toast.makeText(getApplicationContext(), "I did a thing!", Toast.LENGTH_SHORT).show();
    }
    */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.to_home) {
            startActivity(new Intent(CalendarActivity.this, MainActivity.class));
        }
        else if (id == R.id.to_emp) {
            startActivity(new Intent(CalendarActivity.this, SelectEmployee.class));
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