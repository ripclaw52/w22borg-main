package com.example.w22borg.calendar;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.w22borg.MainActivity;
import com.example.w22borg.R;
import com.example.w22borg.SQLdb;
import com.example.w22borg.data.EmployeeModel;
import com.example.w22borg.data.ShiftModel;
import com.example.w22borg.view.AddEmployee;
import com.example.w22borg.view.SelectEmployee;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<LocalDate> days;
    private final ArrayList<ShiftModel> shiftModels;
    private final ArrayList<String> empsShift0;
    private final ArrayList<String> empsShift1;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<LocalDate> days, ArrayList<ShiftModel> shiftModels, ArrayList<String> empsShift0, ArrayList<String> empsShift1, OnItemListener onItemListener)
    {
        this.days = days;
        this.shiftModels = shiftModels;
        this.empsShift0 = empsShift0;
        this.empsShift1 = empsShift1;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.c_calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        if(days.size() > 15) //month view
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        else //week view
            layoutParams.height = (int) parent.getHeight();

        return new CalendarViewHolder(view, onItemListener, days, shiftModels);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        final LocalDate date = days.get(position);
        //final ShiftModel shiftType0 = CalendarUtils.findShiftModelFromDateAndType(shiftModels, date, 0);
        //final ShiftModel shiftType1 = CalendarUtils.findShiftModelFromDateAndType(shiftModels, date, 1);

        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
        try {
            if (CalendarUtils.isWeekEnd(date)) {
                holder.cellDayShift1.setText(empsShift0.get(position));
                holder.cellDayShift1.setTextColor(0xff505050);
            } else {
                holder.cellDayShift1.setText(empsShift0.get(position));
                holder.cellDayShift2.setText(empsShift1.get(position));
            }

            if (date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY)
                    || date.getDayOfWeek().equals(DayOfWeek.TUESDAY) || date.getDayOfWeek().equals(DayOfWeek.THURSDAY))
            {
                holder.parentView.setBackgroundColor(Color.argb(50, 125, 125, 125));
            }
        }
        catch (Exception e) {

        }
        if(!date.getMonth().equals(CalendarUtils.selectedDate.getMonth())) {
            holder.dayOfMonth.setTextColor(Color.argb(200, 125, 125, 125));
            if (CalendarUtils.isWeekEnd(date)) {
                holder.cellDayShift1.setTextColor(0x80505050);
            } else {
                holder.cellDayShift1.setTextColor(Color.argb(80, 255, 125, 0));
            }
            holder.cellDayShift2.setTextColor(Color.argb(80, 0, 0, 255));
            holder.lineTV.setTextColor(Color.argb(200, 125, 125, 125));
        }

        if(date.equals(CalendarUtils.selectedDate)) {
            holder.parentView.setBackgroundColor(Color.argb(125, 75, 200, 255));
            holder.dayOfMonth.setTextColor(R.attr.colorOnPrimarySurface);
            if (CalendarUtils.isWeekEnd(date)) {
                holder.cellDayShift1.setTextColor(R.attr.colorOnPrimarySurface);
            } else {
                holder.cellDayShift1.setTextColor(Color.argb(125, 255, 125, 0));
            }
            holder.cellDayShift2.setTextColor(Color.argb(125, 0,0, 255));
            holder.lineTV.setTextColor(R.attr.colorOnPrimarySurface);
        }
    }

    @Override
    public int getItemCount()
    {
        return days.size();
    }

    public interface OnItemListener
    {
        void onItemClick(int position, LocalDate date);
    }
}