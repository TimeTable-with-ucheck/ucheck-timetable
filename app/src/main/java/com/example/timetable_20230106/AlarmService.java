package com.example.timetable_20230106;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmService {
    private Context context;
    private AlarmManager alarmManager;
    private ArrayList<AlarmData> alarmDataList;
    public AlarmService(Context context){
        this.context  = context;
        alarmManager= (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmDataList = new ArrayList<>();
    }
    /**
     * 알람 메니저에 알람 등록
     * @param alarmData
     */
    private void regist(AlarmData alarmData) {
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
            System.out.println("regist!: title: "+alarmData.getClassTitle()+" id: "+id[i]);
        }
    }

    public void patchAlarm(AlarmData alarmData){
        if(alarmData.getIsOn())regist(alarmData);
        else unRegist(alarmData);
    }

    /**
     * 알람 메니저에서 등록된 알람 제거
     * @param alarmData
     */
    private void unRegist(AlarmData alarmData) {
        Intent[] intents = alarmData.getIntent();
        int[] id = alarmData.getId();
        for(int i = 0; i<alarmData.getSize(); i++) {
            try {
                System.out.println("unregist: title:"+alarmData.getClassTitle()+" id: "+id[i]);
                PendingIntent temp = PendingIntent.getBroadcast(context,id[i], intents[i],PendingIntent.FLAG_IMMUTABLE);
                alarmManager.cancel(temp);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 알람 on
     * @param schedules
     */
    public void alarmOn(ArrayList<Schedule> schedules){
        AlarmData alarmData = findAlarmData(schedules);
        alarmData.setIsOn(true);
        patchAlarm(alarmData);
    }

    /**
     * 알람 off
     * @param schedules
     */
    public void alarmOff(ArrayList<Schedule> schedules){
        AlarmData alarmData = findAlarmData(schedules);
        alarmData.setIsOn(false);
        patchAlarm(alarmData);
    }

    /**
     * 저장된 알람, 등록된 알람 모두 삭제
     */
    public void clearAlarm(){
        if(alarmDataList !=null &&this.alarmDataList.size()>0) {
            for (AlarmData alarmData : this.alarmDataList) {
                unRegist(alarmData);
                alarmDataList = null;
            }
        }
    }
    /**
     * 제목이 같은 알람은 하나의 알람 데이터로 생성,
     * 전역적으로 선언되어 있는 알람 데이터 리스트에 저장한다.
     * @param scheduless
     */
    public void createAlarmData(ArrayList<ArrayList<Schedule>> scheduless){
        alarmDataList = new ArrayList<>();
        for(ArrayList<Schedule> schedules : scheduless) {
            AlarmData temp = new AlarmData(schedules, context);
            alarmDataList.add(temp);
            regist(temp);
        }
    }

    public AlarmData findAlarmData(ArrayList<Schedule> schedules){
        for(AlarmData alarmData : alarmDataList){
            if(alarmData.isTitleEqual(schedules.get(0).getClassTitle()))return alarmData;
        }
        return null;
    }
    public ArrayList<AlarmData> getAlarmDataList(){
        return alarmDataList;
    }
    public void setAlarmDataList(ArrayList<AlarmData> alarmDataList){
        this.alarmDataList = alarmDataList;
    }
}
