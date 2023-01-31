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
   AlarmService alarmService;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("재부팅 절차");
        alarmService = new AlarmService(context);
        SharedPreferences preferences = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        if ((preferences != null) && (preferences.contains("alarmDataList"))) {
            String alarmDataListString = preferences.getString("alarmDataList", null);
            if (alarmDataListString != null) {
                Gson gson = new Gson();
                ArrayList<AlarmData> alarmDataList = gson.fromJson(alarmDataListString, new TypeToken<ArrayList<AlarmData>>() {
                }.getType());
                alarmService.setAlarmDataList(alarmDataList);
                alarmService.reRegistAll();
            } else {
                System.out.println("in reboot list is null");
            }
        }
    }



}