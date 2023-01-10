package com.example.timetable_20230106;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.tlaabs.timetableview.Time;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;

public class ReceiverBoot extends BroadcastReceiver {
    AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("reboot");
        SharedPreferences preferences = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        if ((preferences != null) && (preferences.contains("alarmDataList"))) {
            String alarmDataListString = preferences.getString("alarmDataList", null);
            if (alarmDataListString != null) {
                Gson gson = new Gson();
                ArrayList<AlarmData> alarmDataList = gson.fromJson(alarmDataListString, new TypeToken<ArrayList<AlarmData>>() {
                }.getType());
                registAllAlarmData(alarmDataList,context);
            } else {
                System.out.println("in reboot list is null");
            }
        }
    }

    public void registAllAlarmData(ArrayList<AlarmData> alarmDataList,Context context) {
        if (alarmDataList != null) {
            for (AlarmData alarmData : alarmDataList) {
                if (alarmData.getIsOn()) regist(alarmData,context);
            }
        }
    }

    public void regist(AlarmData alarmData,Context context) {
        long intervalDay = 24 * 60 * 60 * 1000;// 24시간
        int size = alarmData.getSize();
        int[] id = alarmData.getId();
        Intent[] intent = alarmData.getIntent();
        Time[] selectTimes = alarmData.getTime();
        for(int i = 0; i<size;i++) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id[i],  intent[i], PendingIntent.FLAG_IMMUTABLE);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, selectTimes[i].getHour());
            calendar.set(Calendar.MINUTE, selectTimes[i].getMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            System.out.println("알람 설정:-> " + alarmData.getClassTitle() + "설정 시간: " + calendar.getTimeInMillis() + " 설정 요일" + (alarmData.getDay()[i] + 2));
            long selectTime = calendar.getTimeInMillis();
            long currentTime = System.currentTimeMillis();
            if (currentTime > selectTime) {
                selectTime += intervalDay;
            }
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, selectTime, AlarmManager.INTERVAL_DAY,pendingIntent);
            System.out.println("regist!: "+alarmData.getClassTitle());
        }
    }

}