package com.example.timetable_20230106;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;

import java.util.ArrayList;

public class AddSchedule {

    Context context;
    ArrayList<Schedule> schedules;
    View add_schedule;
    EditText set_name, set_professor, set_place, set_startTime, set_endTime, set_day;
    int day;
    Button btn_add;
    Time startTime, endTime;
    Schedule newSchedule;




    public AddSchedule(Context context, ArrayList<Schedule> schedules){
        this.context = context;
        this.schedules = schedules;
    }


    public void addSchedule() {
        add_schedule = (View) View.inflate( this.context , R.layout.add_schedule, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder( this.context );
        dialog.setView(add_schedule);
        dialog.show();

        set_name = add_schedule.findViewById(R.id.set_name);
        set_professor = add_schedule.findViewById(R.id.set_professor);
        set_place = add_schedule.findViewById(R.id.set_place);
        set_day = add_schedule.findViewById(R.id.set_day);
        set_startTime = add_schedule.findViewById(R.id.set_startTime);
        set_endTime = add_schedule.findViewById(R.id.set_endTime);

        btn_add = add_schedule.findViewById(R.id.btn_add);








        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newSchedule = new Schedule();

                day = getDay(set_day.getText().toString());
                String[] detailStartTime = set_startTime.getText().toString().split(":");
                String[] detailEndTime = set_endTime.getText().toString().split(":");
                startTime = new Time(Integer.parseInt(detailStartTime[0]), Integer.parseInt(detailStartTime[1]));
                endTime = new Time(Integer.parseInt(detailEndTime[0]), Integer.parseInt(detailEndTime[1]));

                newSchedule.setClassTitle(set_name.getText().toString());
                newSchedule.setProfessorName(set_professor.getText().toString());
                newSchedule.setClassPlace(set_place.getText().toString());
                newSchedule.setDay(day);
                newSchedule.setStartTime(startTime);
                newSchedule.setEndTime(endTime);

                Toast.makeText(context, "추가 완료!", Toast.LENGTH_SHORT);
//                dialog.show().dismiss();


            }
        });

//        return this.newSchedule;
    }

    private int getDay(String day){
        switch (day){
            case "월": return 0;
            case "화": return 1;
            case "수": return 2;
            case "목": return 3;
            case "금": return 4;
            case "토": return 5;
            case "일": return 6;
            default: return 9;
        }
    }

}
