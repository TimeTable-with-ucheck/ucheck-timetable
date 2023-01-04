package com.example.test;

import static android.widget.Toast.makeText;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TimetableView Timetable;
    Button btn_input;
    EditText et_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timetable = findViewById(R.id.timetable);
        btn_input = findViewById(R.id.btn_input);
        et_input = findViewById(R.id.et_input);
        btn_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSingleSchedule(Timetable,et_input.getText().toString(),getResources().getAssets());
                et_input.setText("");
            }
        });
        Timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {

                makeText(getApplicationContext(), "옹",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSingleSchedule(TimetableView Timetable, String ClassId, AssetManager assetManager){
        ScheduleSearch scheduleSearch = new ScheduleSearch(assetManager);
        ArrayList<Schedule> schedules = Timetable.getAllSchedulesInStickers();
        JSONObject scheduleJson = scheduleSearch.search(ClassId);
        if(scheduleJson == null){
            makeText(getApplicationContext(), "그딴거 없어 임마",Toast.LENGTH_SHORT).show();
            return;
        }
        String[] timeNplace = scheduleJson.optString("시간및강의실").split(",");
        for(String TNP : timeNplace ){
            Schedule schedule = new Schedule();
            schedule.setClassTitle(scheduleJson.optString("교과목명")); // sets subject
            schedule.setClassPlace(TNP.split("\\(")[1].replace(")","")); // sets place
            schedule.setProfessorName(scheduleJson.optString("담당교수")); // sets professor
            schedule.setDay(getDay(TNP.trim().substring(0,1)));
            String[] time = TNP.trim().substring(1,12).split("-");
            schedule.setStartTime(new Time(Integer.parseInt(time[0].split(":")[0]), Integer.parseInt(time[0].split(":")[1]))); // sets the beginning of class time (hour,minute)
            schedule.setEndTime(new Time(Integer.parseInt(time[1].split(":")[0]),Integer.parseInt(time[1].split(":")[1]))); // sets the end of class time (hour,minute)
            schedules.add(schedule);
        }
        Timetable.add(schedules);
    }

    private int getDay(String day){
        System.out.println("시발 한글 날자는 바로 "+day+"요일 이야");
        switch (day){
            case "월": return 0;
            case "화": return 1;
            case "수": return 2;
            case "목": return 3;
            case "금": return 4;
            default: return -1;
        }
    }
}