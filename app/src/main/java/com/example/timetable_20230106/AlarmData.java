package com.example.timetable_20230106;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class AlarmData {
    private String title;
    private Time[] time;
    private int[] days;
    private int[] id;
    private Intent[] intent;
    private boolean isOn;

    public AlarmData(ArrayList<Schedule> schedules, Context context){
        int hour;
        int minute;
        time = new Time[schedules.size()];
        days = new int[schedules.size()];
        id = new int[schedules.size()];
        intent = new Intent[schedules.size()];
        Random random = new Random();
        for(int i = 0; i<schedules.size();i++) {
            int id =random.nextInt(400);
            Schedule schedule = schedules.get(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent[i] = new Intent(context, NotificationReceiver.class);
                intent[i].putExtra("weekday", schedule.getDay());
                intent[i].putExtra("title", schedule.getClassTitle());
                this.time[i] = schedule.getStartTime();
                this.title = schedule.getClassTitle();
                this.days[i] = schedule.getDay();
                this.id[i] = id;
                this.isOn = true;
            } else {
                Toast.makeText(context, "버전을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
    public int getSize(){
        return this.time.length;
    }
    public void setIsOn(boolean isOn){
        this.isOn = isOn;
    }
    public boolean getIsOn(){
        return this.isOn;
    }
    public boolean isTitleEqual(String title){
        return this.title.equals(title);
    }
    public String getClassTitle(){
        return this.title;
    }
    public int[] getDay(){
        return this.days;
    }
    public Time[] getTime(){
        return this.time;
    }
    public String GetString(){
       String out = title+"- ison: "+isOn;
       for(int i = 0; i<time.length;i++){
           out = out+"시간: "+time[i].getHour()+" 분: "+time[i].getMinute()+" 일: "+days[i];
       }
       return out;
    }
    public int[] getId(){
        return id;
    }
    public Intent[] getIntent(){
        return intent;
    }

}

