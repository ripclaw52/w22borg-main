package com.example.w22borg.calendar;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.w22borg.R;
import com.example.w22borg.data.ShiftModel;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private final ArrayList<LocalDate> days;
    private final ArrayList<ShiftModel> shiftModels;
    public final View parentView;
    public final TextView dayOfMonth, lineTV, cellDayShift1, cellDayShift2;
    private final CalendarAdapter.OnItemListener onItemListener;
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<LocalDate> days, ArrayList<ShiftModel> shiftModels)
    {
        super(itemView);
        parentView = itemView.findViewById(R.id.parentView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        lineTV = itemView.findViewById(R.id.lineTV);
        cellDayShift1 = itemView.findViewById(R.id.cellDayShift1);
        cellDayShift2 = itemView.findViewById(R.id.cellDayShift2);

        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);

        this.days = days;
        this.shiftModels = shiftModels;
    }

    @Override
    public void onClick(View view)
    {
        onItemListener.onItemClick(getAdapterPosition(), days.get(getAdapterPosition()));
    }
}