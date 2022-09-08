package com.example.w22borg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.w22borg.calendar.CalendarUtils;
import com.example.w22borg.data.EmployeeModel;
import com.example.w22borg.data.ShiftModel;
import com.example.w22borg.view.EditAvailability;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SQLdb extends SQLiteOpenHelper {
    //Constant for employee table
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String EMAIL = "email";
    public static final String START_DATE = "start_date";
    public static final String TOTAL_SHIFT = "total_shift";
    public static final String CAN_OPEN = "can_open";
    public static final String CAN_CLOSE = "can_close";
    public static final String IS_ACTIVE = "is_active";
    public static final String MONDAY = "mon";
    public static final String TUESDAY = "tue";
    public static final String WEDNESDAY = "wed";
    public static final String THURSDAY = "thu";
    public static final String FRIDAY = "fri";
    public static final String SATURDAY = "sat";
    public static final String SUNDAY = "sun";
    public static final String searchName = "John";

    public static final String EMPLOYEES_TABLE = "EMPLOYEES";

    public static final String DATE = "date";
    public static final String SHIFT_TYPE = "shift_type";
    public static final String EMP_1 = "emp_1";
    public static final String EMP_2 = "emp_2";
    public static final String EMP_3 = "emp_3";

    public static final String SHIFT_TABLE = "SHIFTS";

    //constructor
    public SQLdb(@Nullable Context context) {
        super(context, "TeamApp.db", null , 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String employeeTable = "CREATE TABLE " + EMPLOYEES_TABLE +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FIRST_NAME + " TEXT NOT NULL, " +
                LAST_NAME + " TEXT NOT NULL, " +
                PHONE_NUMBER + " TEXT NOT NULL, " +
                EMAIL + " TEXT NOT NULL, " +
                START_DATE + " TEXT NOT NULL," +
                TOTAL_SHIFT + " INTEGER, " +
                CAN_OPEN + " TEXT DEFAULT '0', " +
                CAN_CLOSE + " TEXT DEFAULT '0', " +
                IS_ACTIVE + " INTEGER DEFAULT 0, " +
                MONDAY + " INTEGER DEFAULT 3, " +
                TUESDAY + " INTEGER DEFAULT 3, " +
                WEDNESDAY + " INTEGER DEFAULT 3, " +
                THURSDAY + " INTEGER DEFAULT 3, " +
                FRIDAY + " INTEGER DEFAULT 3, " +
                SATURDAY + " INTEGER DEFAULT 3, " +
                SUNDAY + " INTEGER DEFAULT 3);";
        // 0 = open/close, 1 = open, 2 = close, 3 = unavailable

        String shiftTable = "CREATE TABLE " + SHIFT_TABLE +
                "( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATE + " TEXT NOT NULL, " +
                SHIFT_TYPE + " INTEGER NOT NULL, " +
                EMP_1 + " INTEGER, " +
                EMP_2 + " INTEGER, " +
                EMP_3 + " INTEGER, " +
                "UNIQUE("+DATE+", "+SHIFT_TYPE+"));";
        String[] statement = new String[]{employeeTable, shiftTable};
        for (String sql : statement)                //read through sql commands
            db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public boolean addShift (ShiftModel shiftModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DATE, shiftModel.getLocalDate().toString());
        cv.put(SHIFT_TYPE, shiftModel.getShiftType());

        try {
            ArrayList<EmployeeModel> arrayList = shiftModel.getEmployeeModels();

            switch (arrayList.size()) {
                case 1:
                    cv.put(EMP_1, arrayList.get(0).getId());
                    break;
                case 2:
                    cv.put(EMP_1, arrayList.get(0).getId());
                    cv.put(EMP_2, arrayList.get(1).getId());
                    break;
                case 3:
                    cv.put(EMP_1, arrayList.get(0).getId());
                    cv.put(EMP_2, arrayList.get(1).getId());
                    cv.put(EMP_3, arrayList.get(2).getId());
                    break;
            }
        } catch (Exception e) {}
        try {
            long insert = db.insertOrThrow(SHIFT_TABLE, null, cv);
            db.close();
            cv.clear();
            return insert != -1;
        } catch (SQLiteConstraintException sqLiteConstraintException) {
            //System.out.println("Shift already exists in table!");
            db.close();
            cv.clear();
            return false;
        }
    }

    public void editShift (String id, String date, Integer shift_type, Integer emp_1, Integer emp_2, Integer emp_3) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DATE, date);
        cv.put(SHIFT_TYPE, shift_type);
        cv.put(EMP_1, emp_1);
        cv.put(EMP_2, emp_2);
        cv.put(EMP_3, emp_3);

        db.update(SHIFT_TABLE, cv, "id=?", new String[]{id});
        cv.clear();
        db.close();
    }

    // for open and all-day shifts
    public ShiftModel getShiftType0(@Nullable LocalDate localDate) {
        String selectedDate = Objects.requireNonNull(localDate).toString();
        ArrayList<EmployeeModel> tempList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + SHIFT_TABLE + " WHERE (" + DATE + " = '"+selectedDate+"' ) AND (" + SHIFT_TYPE + " = " + 0 + ")";

        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor != null && cursor.moveToFirst()) {
            int shiftID = cursor.getInt(0);
            LocalDate shiftDate = LocalDate.parse(cursor.getString(1));
            int shiftTYpe = cursor.getInt(2);
            try {
                Integer employeeID1 = cursor.getInt(3);
                Integer employeeID2 = cursor.getInt(4);
                Integer employeeID3 = cursor.getInt(5);

                tempList.add(new EmployeeModel(retrieveEmployee(employeeID1)));
                tempList.add(new EmployeeModel(retrieveEmployee(employeeID2)));
                tempList.add(new EmployeeModel(retrieveEmployee(employeeID3)));

                ShiftModel shiftModel = new ShiftModel(shiftID, shiftDate, shiftTYpe, tempList);

                cursor.close();
                db.close();

                return shiftModel;
            } catch (Exception e) {
                ShiftModel shiftModel = new ShiftModel(shiftID, shiftDate, shiftTYpe);

                cursor.close();
                db.close();

                return shiftModel;
            }
        }
        return null;
    }

    // for close shift
    public ShiftModel getShiftType1(@Nullable LocalDate localDate) {
        String selectedDate = Objects.requireNonNull(localDate).toString();
        ArrayList<EmployeeModel> tempList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        assert localDate != null;
        String queryString = "SELECT * FROM " + SHIFT_TABLE + " WHERE (" + DATE + " = '"+selectedDate+"' ) AND (" + SHIFT_TYPE + "=" + 1 + ")";
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor != null && cursor.moveToFirst()) {
            int shiftID = cursor.getInt(0);
            LocalDate shiftDate = LocalDate.parse(cursor.getString(1));
            int shiftTYpe = cursor.getInt(2);
            try {
                Integer employeeID1 = cursor.getInt(3);
                Integer employeeID2 = cursor.getInt(4);
                Integer employeeID3 = cursor.getInt(5);

                tempList.add(new EmployeeModel(retrieveEmployee(employeeID1)));
                tempList.add(new EmployeeModel(retrieveEmployee(employeeID2)));
                tempList.add(new EmployeeModel(retrieveEmployee(employeeID3)));

                ShiftModel shiftModel = new ShiftModel(shiftID, shiftDate, shiftTYpe, tempList);

                cursor.close();
                db.close();

                return shiftModel;
            } catch (Exception e) {
                ShiftModel shiftModel = new ShiftModel(shiftID, shiftDate, shiftTYpe);

                cursor.close();
                db.close();

                return shiftModel;
            }
        }
        return null;
    }

    // get weekShift from CalendarUtils.selectedDate
    public ArrayList<ShiftModel> getSelectedWeekShift(LocalDate localDate) {
        ArrayList<ShiftModel> weekShiftArray = new ArrayList<>();
        ArrayList<LocalDate> tempComparisonWeekArray = CalendarUtils.daysInWeekArray(localDate);

        for (LocalDate currentSelectedDate: tempComparisonWeekArray) {
            if (CalendarUtils.isWeekEnd(currentSelectedDate)) {
                weekShiftArray.add(getShiftType0(currentSelectedDate));
            } else {
                weekShiftArray.add(getShiftType0(currentSelectedDate));
                weekShiftArray.add(getShiftType1(currentSelectedDate));
            }
        }
        return weekShiftArray;
    }

    // gets month shift lists from CalendarUtils.selectedDate
    public ArrayList<ShiftModel> getSelectedMonthShift() {
        ArrayList<ShiftModel> monthShiftArray = new ArrayList<>();
        ArrayList<LocalDate> tempComparisonWeekArray = CalendarUtils.daysInMonthArray();

        for (LocalDate currentSelectedDate: tempComparisonWeekArray) {
            if (CalendarUtils.isWeekEnd(currentSelectedDate)) {
                monthShiftArray.add(getShiftType0(currentSelectedDate));
            } else {
                monthShiftArray.add(getShiftType0(currentSelectedDate));
                monthShiftArray.add(getShiftType1(currentSelectedDate));
            }
        }
        return monthShiftArray;
    }

    public boolean addEmployee (EmployeeModel employee) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(FIRST_NAME, employee.getFirstName());
        cv.put(LAST_NAME, employee.getLastName());
        cv.put(PHONE_NUMBER, employee.getPhoneNumber());
        cv.put(EMAIL, employee.getEmail());
        cv.put(START_DATE, employee.getDateOnStartingJob());
        cv.put(TOTAL_SHIFT, employee.getTotalWorkingShiftsSinceStartDate());
        cv.put(CAN_OPEN, employee.isCanOpenStore());
        cv.put(CAN_CLOSE, employee.isCanCloseStore());
        cv.put(MONDAY, employee.getMon());
        cv.put(TUESDAY, employee.getTue());
        cv.put(WEDNESDAY, employee.getWed());
        cv.put(THURSDAY, employee.getThu());
        cv.put(FRIDAY, employee.getFri());
        cv.put(SATURDAY, employee.getSat());
        cv.put(SUNDAY, employee.getSun());
        long insert = db.insert(EMPLOYEES_TABLE, null, cv);
        return insert != -1;
    }

    public void editEmployee (String id, String fname, String lname, String phoneNo, String email,
                                 String startDate, String canOpen, String canClose, int active) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(FIRST_NAME, fname);
        cv.put(LAST_NAME, lname);
        cv.put(PHONE_NUMBER, phoneNo);
        cv.put(EMAIL, email);
        cv.put(START_DATE, startDate);
        cv.put(CAN_OPEN, canOpen);
        cv.put(CAN_CLOSE, canClose);
        cv.put(IS_ACTIVE, active);

        db.update(EMPLOYEES_TABLE, cv, "id=?", new String[]{id});
        db.close();
    }

    public void editEmpAvail (String id, int Mon, int Tue, int Wed, int Thu,
                              int Fri, int Sat, int Sun) {
        // change weekend of unavailable to database value
        if(Sat == 1){ Sat = 3;}
        if(Sun == 1){ Sun = 3;}
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(MONDAY, Mon);
        cv.put(TUESDAY, Tue);
        cv.put(WEDNESDAY, Wed);
        cv.put(THURSDAY, Thu);
        cv.put(FRIDAY, Fri);
        cv.put(SATURDAY, Sat);
        cv.put(SUNDAY, Sun);

        db.update(EMPLOYEES_TABLE, cv, "id=?", new String[]{id});
        db.close();
    }

    // create and return a list of employees with no search filters
    public List<EmployeeModel> getEmpList() {
        List<EmployeeModel> returnList = new ArrayList<>();

        // get data from the database
        String queryString = "SELECT * FROM " + EMPLOYEES_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            // loop through the cursor and create new employee objects. Put them in returnList.
            do {
                int empID = cursor.getInt(0);
                String empFirstName = cursor.getString(1);
                String empLastName = cursor.getString(2);
                String empPhoneNum = cursor.getString(3);
                String empEmail = cursor.getString(4);
                String empHireDate = cursor.getString(5);
                int empTotalShift = cursor.getInt(6);
                String empCanOpen = cursor.getString(7);
                String empCanClose = cursor.getString(8);
                int empActive = cursor.getInt(9);
                int monday = cursor.getInt(10);
                int tuesday = cursor.getInt(11);
                int wednesday = cursor.getInt(12);
                int thursday = cursor.getInt(13);
                int friday = cursor.getInt(14);
                int saturday = cursor.getInt(15);
                int sunday = cursor.getInt(16);

                boolean empOpen = convertToBoolean(empCanOpen);
                boolean empClose = convertToBoolean(empCanClose);


                EmployeeModel newEmployee = new EmployeeModel(empID, empFirstName, empLastName,
                        empEmail, empPhoneNum, empHireDate, empTotalShift, empOpen, empClose, empActive,
                        monday, tuesday, wednesday, thursday, friday, saturday, sunday) ;
                returnList.add(newEmployee);

            } while(cursor.moveToNext());
        }
        // close the cursor and database
        cursor.close();
        db.close();
        return returnList;

    }

    public ArrayList<String> getEmployeeIDsForShifts(LocalDate dayStart, LocalDate dayEnd, int shift_type) {
        ArrayList<String> retList = new ArrayList<>();

        String queryString = "select * from " + SHIFT_TABLE +
                " where date between '" + dayStart.toString() + "' and '" + dayEnd.toString() +
                "' and shift_type = " + shift_type;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        List<Integer> weekendPositions = Arrays.asList(1,2,3,4,5,8,9,10,11,12,15,16,17,18,19,22,23,24,25,26,29,30,31,32,33,36,37,38,39,40);
        String idsStr;
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                idsStr = "";
                int emp1 = cursor.getInt(3);
                if (emp1 > 0){ idsStr += emp1; }
                int emp2 = cursor.getInt(4);
                if (emp2 > 0){ idsStr += "," + emp2; }
                int emp3 = cursor.getInt(5);
                if (emp3 > 0){ idsStr += "," + emp3; }

                retList.add(idsStr);
                if (shift_type == 1) {
                    retList.add(weekendPositions.get(i++), idsStr);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return retList;
    }

    public String getEmployeeNamesForShifts(LocalDate date, int shift_type) {
        String retStr = "";

        String queryString = "select e.ID, first_name, last_name from " + EMPLOYEES_TABLE +
                " e where e.ID in (select emp_1 from " + SHIFT_TABLE + " where date = '" +
                date.toString() + "' and shift_type = " + shift_type + ") or e.ID in " +
                "(select emp_2 from " + SHIFT_TABLE + " where date = '" + date.toString() +
                "' and shift_type = " + shift_type + ") or e.ID in (select emp_3 from " + SHIFT_TABLE +
                " where date = '" + date.toString() + "' and shift_type = " + shift_type + ")";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                String empFname = cursor.getString(1);
                String empLname = cursor.getString(2);
                int empID = cursor.getInt(0);

                retStr += empFname.charAt(0) + "." + empLname + "[" + empID + "]  ";
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return retStr;
    }

    // create string with employee names and IDs who have no shifts in given week
    public String checkOneShiftPerWeekStr(LocalDate weekStart) {
        String retStr = "";
        String weekStartStr, weekEndStr;

        //convert date string to int for database query
        weekEndStr = (weekStart.plusDays(6)).toString();
        weekStartStr = weekStart.toString();

        //get data from the database
        String queryString = "select distinct * from " + EMPLOYEES_TABLE +
                " e where is_active = 0 and ID not in (select e.ID from " + SHIFT_TABLE +
                " s where (e.ID = s.emp_1 or e.ID = s.emp_2 or e.ID = s.emp_3)" +
                " and date between '" + weekStartStr + "' and '" + weekEndStr + "')";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                int empID = cursor.getInt(0);
                String empFirstName = cursor.getString(1);
                String empLastName = cursor.getString(2);

                retStr += "\t" + empFirstName + " " + empLastName + " (ID: " + empID + ")\n";
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return retStr;
    }

    // create string with days of week with not enough workers
    public List<String> checkShiftsForMinWorkers(LocalDate weekStart) {
        List<String> retList = new ArrayList<>();
        String weekStartStr, weekEndStr;

        //convert date string to int for database query
        weekEndStr = (weekStart.plusDays(6)).toString();
        weekStartStr = weekStart.toString();

        //get data from the database
        String queryString = "select * from " + SHIFT_TABLE +
                " where emp_2 is null " +
                " and date between '" + weekStartStr +"' and '" + weekEndStr + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            do {
                String shiftDateStr = cursor.getString(1);
                int shiftType = cursor.getInt(2);

                LocalDate shiftDate = LocalDate.parse(shiftDateStr);
                String shiftDayOfWeek = shiftDate.getDayOfWeek().toString();
                String shiftTypeStr = "";
                if(!shiftDayOfWeek.equals("SUNDAY") && !shiftDayOfWeek.equals("SATURDAY")) {
                    if (shiftType == 0) {
                        shiftTypeStr = "(O)";
                    } else {
                        shiftTypeStr = "(C)";
                    }
                }
                if(count++ % 4 == 0) {
                    retList.add("\n\t" + shiftDayOfWeek.substring(0,3) + shiftTypeStr);
                } else { retList.add(shiftDayOfWeek.substring(0,3) + shiftTypeStr); }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return retList;
    }

    // create employee list for all day shifts that are available
    public List<EmployeeModel> getEmpAvailListAD(String dayOfWeek) {
        List<EmployeeModel> returnList = new ArrayList<>();
        String daySelected = null;
        if(dayOfWeek.equals("Saturday")) {daySelected = "sat";}
        if(dayOfWeek.equals("Sunday")) {daySelected = "sun";}

        // get data from the database
        String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE " + daySelected + " = 0 " +
                "AND " + IS_ACTIVE + " = 0";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            // loop through the cursor and create new employee objects. Put them in returnList.
            do {
                int empID = cursor.getInt(0);
                String empFirstName = cursor.getString(1);
                String empLastName = cursor.getString(2);
                String empPhoneNum = cursor.getString(3);
                String empEmail = cursor.getString(4);
                String empHireDate = cursor.getString(5);
                int empTotalShift = cursor.getInt(6);
                String empCanOpen = cursor.getString(7);
                String empCanClose = cursor.getString(8);
                int empActive = cursor.getInt(9);
                int monday = cursor.getInt(10);
                int tuesday = cursor.getInt(11);
                int wednesday = cursor.getInt(12);
                int thursday = cursor.getInt(13);
                int friday = cursor.getInt(14);
                int saturday = cursor.getInt(15);
                int sunday = cursor.getInt(16);

                boolean empOpen = convertToBoolean(empCanOpen);
                boolean empClose = convertToBoolean(empCanClose);


                EmployeeModel newEmployee = new EmployeeModel(empID, empFirstName, empLastName,
                        empEmail, empPhoneNum, empHireDate, empTotalShift, empOpen, empClose, empActive,
                        monday, tuesday, wednesday, thursday, friday, saturday, sunday) ;
                returnList.add(newEmployee);

            } while(cursor.moveToNext());
        }

        // close the cursor and database
        cursor.close();
        db.close();
        return returnList;

    }

    // return employee list for all day shifts depending on search button selected
    public List<EmployeeModel> getEmpAvailSearchListAD(String searchParam, String dayOfWeek) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        List<EmployeeModel> returnList = new ArrayList<>();
        String daySelected = null;
        if(dayOfWeek.equals("Saturday")) {daySelected = "sat";}
        if(dayOfWeek.equals("Sunday")) {daySelected = "sun";}

        // open button selected
        if (searchParam.equals("open")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  CAN_OPEN = " + 1 +
                    " AND " + daySelected + " = 0 " + "AND " + IS_ACTIVE + " = 0";;
            cursor = db.rawQuery(queryString, null);
        }

        // close button selected
        if (searchParam.equals("close")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  CAN_CLOSE = " + 1 +
                    " AND " + daySelected + " = 0 AND " + IS_ACTIVE + " = 0";
            cursor = db.rawQuery(queryString, null);
        }

        // active button selected
        if (searchParam.equals("active")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  IS_ACTIVE = " + 0 +
                    " AND " + daySelected + " = 0";
            cursor = db.rawQuery(queryString, null);
        }

        // inactive button selected
        if (searchParam.equals("inactive")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  IS_ACTIVE = " + -1 +
                    " AND " + daySelected + " = 0";
            cursor = db.rawQuery(queryString, null);
        }


        assert cursor != null;
        if (cursor.moveToFirst()) {
            // loop through the cursor and create new employee objects. Put them in returnList.
            do {
                int empID = cursor.getInt(0);
                String empFirstName = cursor.getString(1);
                String empLastName = cursor.getString(2);
                String empPhoneNum = cursor.getString(3);
                String empEmail = cursor.getString(4);
                String empHireDate = cursor.getString(5);
                int empTotalShift = cursor.getInt(6);
                String empCanOpen = cursor.getString(7);
                String empCanClose = cursor.getString(8);
                int empActive = cursor.getInt(9);
                int monday = cursor.getInt(10);
                int tuesday = cursor.getInt(11);
                int wednesday = cursor.getInt(12);
                int thursday = cursor.getInt(13);
                int friday = cursor.getInt(14);
                int saturday = cursor.getInt(15);
                int sunday = cursor.getInt(16);

                boolean empOpen = convertToBoolean(empCanOpen);
                boolean empClose = convertToBoolean(empCanClose);


                EmployeeModel newEmployee = new EmployeeModel(empID, empFirstName, empLastName,
                        empEmail, empPhoneNum, empHireDate, empTotalShift, empOpen, empClose, empActive,
                        monday, tuesday, wednesday, thursday, friday, saturday, sunday) ;
                returnList.add(newEmployee);

            } while(cursor.moveToNext());
        }
        // close the cursor and database
        cursor.close();
        db.close();
        return returnList;

    }

    // create employee list for opening availability
    public List<EmployeeModel> getEmpAvailListOP(String dayOfWeek) {
        List<EmployeeModel> returnList = new ArrayList<>();
        String daySelected = null;
        if(dayOfWeek.equals("Monday")) {daySelected = "mon";}
        if(dayOfWeek.equals("Tuesday")) {daySelected = "tue";}
        if(dayOfWeek.equals("Wednesday")) {daySelected = "wed";}
        if(dayOfWeek.equals("Thursday")) {daySelected = "thu";}
        if(dayOfWeek.equals("Friday")) {daySelected = "fri";}

        // get data from the database
        // weekday opening availability
        String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE (" + daySelected + " = 0 OR "
                + daySelected + " = 1) AND "  + IS_ACTIVE + " = 0";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            // loop through the cursor and create new employee objects. Put them in returnList.
            do {
                int empID = cursor.getInt(0);
                String empFirstName = cursor.getString(1);
                String empLastName = cursor.getString(2);
                String empPhoneNum = cursor.getString(3);
                String empEmail = cursor.getString(4);
                String empHireDate = cursor.getString(5);
                int empTotalShift = cursor.getInt(6);
                String empCanOpen = cursor.getString(7);
                String empCanClose = cursor.getString(8);
                int empActive = cursor.getInt(9);
                int monday = cursor.getInt(10);
                int tuesday = cursor.getInt(11);
                int wednesday = cursor.getInt(12);
                int thursday = cursor.getInt(13);
                int friday = cursor.getInt(14);
                int saturday = cursor.getInt(15);
                int sunday = cursor.getInt(16);

                boolean empOpen = convertToBoolean(empCanOpen);
                boolean empClose = convertToBoolean(empCanClose);


                EmployeeModel newEmployee = new EmployeeModel(empID, empFirstName, empLastName,
                        empEmail, empPhoneNum, empHireDate, empTotalShift, empOpen, empClose, empActive,
                        monday, tuesday, wednesday, thursday, friday, saturday, sunday) ;
                returnList.add(newEmployee);

            } while(cursor.moveToNext());
        }
        // close the cursor and database
        cursor.close();
        db.close();
        return returnList;
    }

    // return employee list for all day shifts depending on search button selected
    public List<EmployeeModel> getEmpAvailSearchListOP(String searchParam, String dayOfWeek) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        List<EmployeeModel> returnList = new ArrayList<>();
        String daySelected = null;
        if(dayOfWeek.equals("Monday")) {daySelected = "mon";}
        if(dayOfWeek.equals("Tuesday")) {daySelected = "tue";}
        if(dayOfWeek.equals("Wednesday")) {daySelected = "wed";}
        if(dayOfWeek.equals("Thursday")) {daySelected = "thu";}
        if(dayOfWeek.equals("Friday")) {daySelected = "fri";}

        // open button selected
        if (searchParam.equals("open")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  CAN_OPEN = " + 1 +
                    " AND (" + daySelected + " = 0 OR " + daySelected + " = 1) AND "  + IS_ACTIVE + " = 0";
            cursor = db.rawQuery(queryString, null);
        }

        // close button selected
        if (searchParam.equals("close")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  CAN_CLOSE = " + 1 +
                    " AND (" + daySelected + " = 0 OR " + daySelected + " = 1) AND "  + IS_ACTIVE + " = 0";
            cursor = db.rawQuery(queryString, null);
        }

        // active button selected
        if (searchParam.equals("active")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  IS_ACTIVE = " + 0 +
                    " AND (" + daySelected + " = 0 OR " + daySelected + " = 1)";
            cursor = db.rawQuery(queryString, null);
        }

        // inactive button selected
        if (searchParam.equals("inactive")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  IS_ACTIVE = " + -1 +
                    " AND (" + daySelected + " = 0 OR " + daySelected + " = 1)";
            cursor = db.rawQuery(queryString, null);
        }

        assert cursor != null;
        if (cursor.moveToFirst()) {
            // loop through the cursor and create new employee objects. Put them in returnList.
            do {
                int empID = cursor.getInt(0);
                String empFirstName = cursor.getString(1);
                String empLastName = cursor.getString(2);
                String empPhoneNum = cursor.getString(3);
                String empEmail = cursor.getString(4);
                String empHireDate = cursor.getString(5);
                int empTotalShift = cursor.getInt(6);
                String empCanOpen = cursor.getString(7);
                String empCanClose = cursor.getString(8);
                int empActive = cursor.getInt(9);
                int monday = cursor.getInt(10);
                int tuesday = cursor.getInt(11);
                int wednesday = cursor.getInt(12);
                int thursday = cursor.getInt(13);
                int friday = cursor.getInt(14);
                int saturday = cursor.getInt(15);
                int sunday = cursor.getInt(16);

                boolean empOpen = convertToBoolean(empCanOpen);
                boolean empClose = convertToBoolean(empCanClose);


                EmployeeModel newEmployee = new EmployeeModel(empID, empFirstName, empLastName,
                        empEmail, empPhoneNum, empHireDate, empTotalShift, empOpen, empClose, empActive,
                        monday, tuesday, wednesday, thursday, friday, saturday, sunday) ;
                returnList.add(newEmployee);

            } while(cursor.moveToNext());
        }
        // close the cursor and database
        cursor.close();
        db.close();
        return returnList;

    }

    // create employee list for closing availability
    public List<EmployeeModel> getEmpAvailListCL(String dayOfWeek) {
        List<EmployeeModel> returnList = new ArrayList<>();
        String daySelected = null;
        if(dayOfWeek.equals("Monday")) {daySelected = "mon";}
        if(dayOfWeek.equals("Tuesday")) {daySelected = "tue";}
        if(dayOfWeek.equals("Wednesday")) {daySelected = "wed";}
        if(dayOfWeek.equals("Thursday")) {daySelected = "thu";}
        if(dayOfWeek.equals("Friday")) {daySelected = "fri";}

        // get data from the database
        // weekday closing availability
        String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE (" + daySelected + " = 0 OR "
                + daySelected + " = 2) AND "  + IS_ACTIVE + " = 0";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            // loop through the cursor and create new employee objects. Put them in returnList.
            do {
                int empID = cursor.getInt(0);
                String empFirstName = cursor.getString(1);
                String empLastName = cursor.getString(2);
                String empPhoneNum = cursor.getString(3);
                String empEmail = cursor.getString(4);
                String empHireDate = cursor.getString(5);
                int empTotalShift = cursor.getInt(6);
                String empCanOpen = cursor.getString(7);
                String empCanClose = cursor.getString(8);
                int empActive = cursor.getInt(9);
                int monday = cursor.getInt(10);
                int tuesday = cursor.getInt(11);
                int wednesday = cursor.getInt(12);
                int thursday = cursor.getInt(13);
                int friday = cursor.getInt(14);
                int saturday = cursor.getInt(15);
                int sunday = cursor.getInt(16);

                boolean empOpen = convertToBoolean(empCanOpen);
                boolean empClose = convertToBoolean(empCanClose);


                EmployeeModel newEmployee = new EmployeeModel(empID, empFirstName, empLastName,
                        empEmail, empPhoneNum, empHireDate, empTotalShift, empOpen, empClose, empActive,
                        monday, tuesday, wednesday, thursday, friday, saturday, sunday) ;
                returnList.add(newEmployee);

            } while(cursor.moveToNext());
        }
        // close the cursor and database
        cursor.close();
        db.close();
        return returnList;
    }


    // return employee list for closing shifts depending on search button selected
    public List<EmployeeModel> getEmpAvailSearchListCL(String searchParam, String dayOfWeek) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        List<EmployeeModel> returnList = new ArrayList<>();
        String daySelected = null;
        if(dayOfWeek.equals("Monday")) {daySelected = "mon";}
        if(dayOfWeek.equals("Tuesday")) {daySelected = "tue";}
        if(dayOfWeek.equals("Wednesday")) {daySelected = "wed";}
        if(dayOfWeek.equals("Thursday")) {daySelected = "thu";}
        if(dayOfWeek.equals("Friday")) {daySelected = "fri";}

        // open button selected
        if (searchParam.equals("open")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  CAN_OPEN = " + 1 +
                    " AND (" + daySelected + " = 0 OR " + daySelected + " = 2) AND "  + IS_ACTIVE + " = 0";
            cursor = db.rawQuery(queryString, null);
        }

        // close button selected
        if (searchParam.equals("close")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  CAN_CLOSE = " + 1 +
                    " AND (" + daySelected + " = 0 OR " + daySelected + " = 2) AND "  + IS_ACTIVE + " = 0";
            cursor = db.rawQuery(queryString, null);
        }

        // active button selected
        if (searchParam.equals("active")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  IS_ACTIVE = " + 0 +
                    " AND (" + daySelected + " = 0 OR " + daySelected + " = 2)";
            cursor = db.rawQuery(queryString, null);
        }

        // inactive button selected
        if (searchParam.equals("inactive")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  IS_ACTIVE = " + -1 +
                    " AND (" + daySelected + " = 0 OR " + daySelected + " = 2)";
            cursor = db.rawQuery(queryString, null);
        }

        assert cursor != null;
        if (cursor.moveToFirst()) {
            // loop through the cursor and create new employee objects. Put them in returnList.
            do {
                int empID = cursor.getInt(0);
                String empFirstName = cursor.getString(1);
                String empLastName = cursor.getString(2);
                String empPhoneNum = cursor.getString(3);
                String empEmail = cursor.getString(4);
                String empHireDate = cursor.getString(5);
                int empTotalShift = cursor.getInt(6);
                String empCanOpen = cursor.getString(7);
                String empCanClose = cursor.getString(8);
                int empActive = cursor.getInt(9);
                int monday = cursor.getInt(10);
                int tuesday = cursor.getInt(11);
                int wednesday = cursor.getInt(12);
                int thursday = cursor.getInt(13);
                int friday = cursor.getInt(14);
                int saturday = cursor.getInt(15);
                int sunday = cursor.getInt(16);

                boolean empOpen = convertToBoolean(empCanOpen);
                boolean empClose = convertToBoolean(empCanClose);


                EmployeeModel newEmployee = new EmployeeModel(empID, empFirstName, empLastName,
                        empEmail, empPhoneNum, empHireDate, empTotalShift, empOpen, empClose, empActive,
                        monday, tuesday, wednesday, thursday, friday, saturday, sunday) ;
                returnList.add(newEmployee);

            } while(cursor.moveToNext());
        }
        // close the cursor and database
        cursor.close();
        db.close();
        return returnList;

    }

    // create and return a list of employees with search filters
    public List<EmployeeModel> getEmpSearchList(String searchParam) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        List<EmployeeModel> returnList = new ArrayList<>();

        // open button selected
        if (searchParam.equals("open")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  CAN_OPEN = " + 1;
            cursor = db.rawQuery(queryString, null);
        }

        // close button selected
        if (searchParam.equals("close")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  CAN_CLOSE = " + 1;
            cursor = db.rawQuery(queryString, null);
        }

        // active button selected
        if (searchParam.equals("active")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  IS_ACTIVE = " + 0;
            cursor = db.rawQuery(queryString, null);
        }

        // inactive button selected
        if (searchParam.equals("inactive")) {

            String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  IS_ACTIVE = " + -1;
            cursor = db.rawQuery(queryString, null);
        }


        assert cursor != null;
        if (cursor.moveToFirst()) {
            // loop through the cursor and create new employee objects. Put them in returnList.
            do {
                int empID = cursor.getInt(0);
                String empFirstName = cursor.getString(1);
                String empLastName = cursor.getString(2);
                String empPhoneNum = cursor.getString(3);
                String empEmail = cursor.getString(4);
                String empHireDate = cursor.getString(5);
                int empTotalShift = cursor.getInt(6);
                String empCanOpen = cursor.getString(7);
                String empCanClose = cursor.getString(8);
                int empActive = cursor.getInt(9);
                int monday = cursor.getInt(10);
                int tuesday = cursor.getInt(11);
                int wednesday = cursor.getInt(12);
                int thursday = cursor.getInt(13);
                int friday = cursor.getInt(14);
                int saturday = cursor.getInt(15);
                int sunday = cursor.getInt(16);

                boolean empOpen = convertToBoolean(empCanOpen);
                boolean empClose = convertToBoolean(empCanClose);


                EmployeeModel newEmployee = new EmployeeModel(empID, empFirstName, empLastName,
                        empEmail, empPhoneNum, empHireDate, empTotalShift, empOpen, empClose, empActive,
                        monday, tuesday, wednesday, thursday, friday, saturday, sunday) ;
                returnList.add(newEmployee);

            } while(cursor.moveToNext());
        }
        // close the cursor and database
        cursor.close();
        db.close();
        return returnList;

    }

    public EmployeeModel retrieveEmployee(int employeeID) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();

        String queryString = "SELECT * FROM " + EMPLOYEES_TABLE + " WHERE  ID = " + employeeID;
        cursor = db.rawQuery(queryString, null);

        int empID = cursor.getInt(0);
        String empFirstName = cursor.getString(1);
        String empLastName = cursor.getString(2);
        String empPhoneNum = cursor.getString(3);
        String empEmail = cursor.getString(4);
        String empHireDate = cursor.getString(5);
        int empTotalShift = cursor.getInt(6);
        String empCanOpen = cursor.getString(7);
        String empCanClose = cursor.getString(8);
        int empActive = cursor.getInt(9);
        int monday = cursor.getInt(10);
        int tuesday = cursor.getInt(11);
        int wednesday = cursor.getInt(12);
        int thursday = cursor.getInt(13);
        int friday = cursor.getInt(14);
        int saturday = cursor.getInt(15);
        int sunday = cursor.getInt(16);

        boolean empOpen = convertToBoolean(empCanOpen);
        boolean empClose = convertToBoolean(empCanClose);


        EmployeeModel newEmployee = new EmployeeModel(
                empID,
                empFirstName,
                empLastName,
                empEmail,
                empPhoneNum,
                empHireDate,
                empTotalShift,
                empOpen,
                empClose,
                empActive,
                monday,
                tuesday,
                wednesday,
                thursday,
                friday,
                saturday,
                sunday
        );

        cursor.close();
        db.close();

        return newEmployee;
    }

    public void deleteEmployee(String empID){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(EMPLOYEES_TABLE, "id=?", new String[]{empID});
        db.close();

    }
    private boolean convertToBoolean(String value) {
        boolean returnValue = false;
        if ("1".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) ||
                "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value))
            returnValue = true;
        return returnValue;
    }

    //get list of opening employee takes in localDates
    public List<EmployeeModel> getOPlist(String localDate) {
        List<EmployeeModel> returnList = new ArrayList<>();
        // get data from the database
        String queryString = "SELECT * FROM " + EMPLOYEES_TABLE +
                " WHERE ID IN (SELECT emp_1 FROM SHIFTS WHERE date = '" + localDate +
                "' AND shift_type = 0) or ID IN (SELECT emp_2 FROM SHIFTS WHERE date = '" + localDate +
                "' AND shift_type = 0) or ID in (SELECT emp_3 FROM SHIFTS WHERE date = '" + localDate +
                "' AND shift_type = 0);";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            // loop through the cursor and create new employee objects. Put them in returnList.
            do {
                int empID = cursor.getInt(0);
                String empFirstName = cursor.getString(1);
                String empLastName = cursor.getString(2);
                String empPhoneNum = cursor.getString(3);
                String empEmail = cursor.getString(4);
                String empHireDate = cursor.getString(5);
                int empTotalShift = cursor.getInt(6);
                String empCanOpen = cursor.getString(7);
                String empCanClose = cursor.getString(8);
                int empActive = cursor.getInt(9);
                int monday = cursor.getInt(10);
                int tuesday = cursor.getInt(11);
                int wednesday = cursor.getInt(12);
                int thursday = cursor.getInt(13);
                int friday = cursor.getInt(14);
                int saturday = cursor.getInt(15);
                int sunday = cursor.getInt(16);

                boolean empOpen = convertToBoolean(empCanOpen);
                boolean empClose = convertToBoolean(empCanClose);


                EmployeeModel newEmployee = new EmployeeModel(empID, empFirstName, empLastName,
                        empEmail, empPhoneNum, empHireDate, empTotalShift, empOpen, empClose, empActive,
                        monday, tuesday, wednesday, thursday, friday, saturday, sunday) ;
                returnList.add(newEmployee);

            } while(cursor.moveToNext());
        }
        // close the cursor and database
        cursor.close();
        db.close();
        return returnList;
    }

    public List<EmployeeModel> getCLlist(String localDate) {
        List<EmployeeModel> returnList = new ArrayList<>();
        // get data from the database
        String queryString = "SELECT * FROM " + EMPLOYEES_TABLE +
                " WHERE ID IN (SELECT emp_1 FROM SHIFTS WHERE date = '" + localDate +
                "' AND shift_type = 1) or ID IN (SELECT emp_2 FROM SHIFTS WHERE date = '" + localDate +
                "' AND shift_type = 1) or ID in (SELECT emp_3 FROM SHIFTS WHERE date = '" + localDate +
                "' AND shift_type = 1);";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            // loop through the cursor and create new employee objects. Put them in returnList.
            do {
                int empID = cursor.getInt(0);
                String empFirstName = cursor.getString(1);
                String empLastName = cursor.getString(2);
                String empPhoneNum = cursor.getString(3);
                String empEmail = cursor.getString(4);
                String empHireDate = cursor.getString(5);
                int empTotalShift = cursor.getInt(6);
                String empCanOpen = cursor.getString(7);
                String empCanClose = cursor.getString(8);
                int empActive = cursor.getInt(9);
                int monday = cursor.getInt(10);
                int tuesday = cursor.getInt(11);
                int wednesday = cursor.getInt(12);
                int thursday = cursor.getInt(13);
                int friday = cursor.getInt(14);
                int saturday = cursor.getInt(15);
                int sunday = cursor.getInt(16);

                boolean empOpen = convertToBoolean(empCanOpen);
                boolean empClose = convertToBoolean(empCanClose);


                EmployeeModel newEmployee = new EmployeeModel(empID, empFirstName, empLastName,
                        empEmail, empPhoneNum, empHireDate, empTotalShift, empOpen, empClose, empActive,
                        monday, tuesday, wednesday, thursday, friday, saturday, sunday) ;
                returnList.add(newEmployee);

            } while(cursor.moveToNext());
        }
        // close the cursor and database
        cursor.close();
        db.close();
        return returnList;
    }

}
