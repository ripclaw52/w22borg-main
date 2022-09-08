package com.example.w22borg;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import com.example.w22borg.calendar.CalendarActivity;
import com.example.w22borg.calendar.WeekViewActivity;
import com.example.w22borg.data.EmployeeModel;
import com.example.w22borg.view.SelectEmployee;
import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    TextView date, close_box, open_box, full_box, warning1, warning2;
    ListView open_lst, close_lst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        open_lst = findViewById(R.id.open_list);
        close_lst = findViewById(R.id.closing_list);
        warning1 = findViewById(R.id.warningOP);
        warning2 = findViewById(R.id.warningCl);

        date = findViewById(R.id.date_box);
        Calendar calendar = Calendar.getInstance();
        String currentDate = LocalDate.now().toString();
        String day = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());


        date.setText(day);

        //replace action with toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("BEMS");

        drawer = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.open_nav_drawer, R.string.close_nav_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.to_home);
        
        if (day.contains("Sunday") || day.contains("Saturday")) {
            //hide closing textbox and list
            open_box = findViewById(R.id.open_box);
            open_box.setVisibility(View.INVISIBLE);
            close_box = findViewById(R.id.close_box);
            close_box.setVisibility(View.INVISIBLE);
            close_lst.setVisibility(View.INVISIBLE);
            full_box = findViewById(R.id.full_box);
            full_box.setVisibility(View.VISIBLE);

            SQLdb db = new SQLdb(MainActivity.this);
            List<EmployeeModel> oplist = db.getOPlist(currentDate);
            ArrayAdapter OPArrayAdapt = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, oplist);
            open_lst.setAdapter(OPArrayAdapt);
            if (open_lst.getAdapter().getCount() < 2){
                warning1.setVisibility(View.VISIBLE);
            }
            db.close();
        }
        else {
            //Get list of opening employee
            SQLdb db = new SQLdb(MainActivity.this);
            List<EmployeeModel> oplist = db.getOPlist(currentDate);
            ArrayAdapter OPArrayAdapt = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, oplist);
            open_lst.setAdapter(OPArrayAdapt);
            if (open_lst.getAdapter().getCount() < 2){
                warning1.setVisibility(View.VISIBLE);
            }

            //Get list for closing
            List<EmployeeModel> closelist = db.getCLlist(currentDate);
            ArrayAdapter CLArrayAdapt = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, closelist);
            close_lst.setAdapter(CLArrayAdapt);
            if (close_lst.getAdapter().getCount() < 2){
                warning2.setVisibility(View.VISIBLE);
            }
            db.close();
        }

        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.to_emp) {
            startActivity(new Intent(MainActivity.this, SelectEmployee.class));
        }
        else if (id == R.id.to_sche) {
            startActivity(new Intent(MainActivity.this, CalendarActivity.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
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

    private boolean checkPermission() {
        // checking of permissions
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 200);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length > 0) {

                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

}