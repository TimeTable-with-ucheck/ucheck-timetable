package com.example.timetable_20230106;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

public class AlarmService {
    private Context context;
    private AlarmManager alarmManager;
    private ArrayList<AlarmData> alarmDataList;
    NotificationReceiver notificationReceiver;
    public AlarmService(Context context){
        this.context  = context;
        alarmManager= (AlarmManager) this.context.getSystemService(this.context.ALARM_SERVICE);
        alarmDataList = new ArrayList<>();
    }
    /**
     * 알람 메니저에 알람 등록
     * @param alarmData
     */
    private void regist(AlarmData alarmData) {
        Log.d("123","regist on!!!!!!!!!!!!!~~~~~~~~~");
        long intervalDay = 24 * 60 * 60 * 1000;// 24시간
        int size = alarmData.getSize();
        int[] id = alarmData.getId();
        int[] weekDay = alarmData.getDay();
        Log.d("week : " , ""+weekDay[0]);
        String title = alarmData.getClassTitle();
        Time[] selectTimes = alarmData.getTime();
        for(int i = 0; i<size;i++) {
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("weekday", convertDayNumb(weekDay[i]));
            intent.putExtra("title", title);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id[i],intent, PendingIntent.FLAG_IMMUTABLE);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, convertDayNumb(weekDay[i]));
            if(selectTimes[i].getMinute() >= 10) {
                calendar.set(Calendar.HOUR_OF_DAY, selectTimes[i].getHour());
                calendar.set(Calendar.MINUTE, selectTimes[i].getMinute()-10);
            } else if(selectTimes[i].getMinute() == 5){
                calendar.set(Calendar.HOUR_OF_DAY, selectTimes[i].getHour()-1);
                calendar.set(Calendar.MINUTE, 55);
            } else {
                calendar.set(Calendar.HOUR_OF_DAY, selectTimes[i].getHour()-1);
                calendar.set(Calendar.MINUTE, 50);
            }
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            System.out.println("알람 설정:-> " + alarmData.getClassTitle() + "설정 시간: " + calendar.getTimeInMillis() + " 설정 요일" + convertDayNumb(alarmData.getDay()[i]));
            long selectTime = calendar.getTimeInMillis();
            Log.d("awdawd", "time : " + selectTime);
            long currentTime = System.currentTimeMillis();
            if (currentTime > selectTime) {
                selectTime += intervalDay;
            }
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, selectTime, AlarmManager.INTERVAL_DAY,pendingIntent);
            //
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, selectTime , pendingIntent);
            System.out.println("regist!: title: "+alarmData.getClassTitle()+" id: "+id[i]+ "day : " + convertDayNumb(alarmData.getDay()[i]));
        }
//        Log.d("d", "time : " + calendar.get)
    }

    public void patchAlarm(AlarmData alarmData){
        Calendar calendar = Calendar.getInstance();
        System.out.println("지금 시간: "+System.currentTimeMillis()+" 지금날짜: "+calendar.get(Calendar.DAY_OF_WEEK));
        if(alarmData.getIsOn()) regist(alarmData);
        else unRegist(alarmData);
    }

    /**
     * 알람 메니저에서 등록된 알람 제거
     * @param alarmData
     */
    public void unRegist(AlarmData alarmData) {
        int[] weekDay = alarmData.getDay();
        String title = alarmData.getClassTitle();
        int[] id = alarmData.getId();
        for(int i = 0; i<alarmData.getSize(); i++) {
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("weekday", weekDay[i]);
            intent.putExtra("title", title);
            try {
                System.out.println("unregist: title:"+alarmData.getClassTitle()+" id: "+id[i]);
                PendingIntent temp = PendingIntent.getBroadcast(context,id[i], intent,PendingIntent.FLAG_IMMUTABLE);
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

    public void addAlarmData(ArrayList<Schedule> schedules, Context context) {
        AlarmData alarmData = new AlarmData(schedules, context);
        this.alarmDataList.add(alarmData);
        patchAlarm(alarmData);
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

    public void reRegistAll(){
        if(alarmDataList != null){
            for(AlarmData alarmData : alarmDataList){
                patchAlarm(alarmData);
            }
        }
    }

    public void alarmTest(){
//        noti();
        Intent intent = new Intent(this.context, NotificationReceiver.class);
        intent.putExtra("weekday",Calendar.DAY_OF_WEEK-2);
        intent.putExtra("title","테스트");
        Calendar calendar = Calendar.getInstance();
        Toast.makeText(context,calendar.getTime().toString(),Toast.LENGTH_SHORT);
        System.out.println("-----------------------------"+calendar.getTime().toString());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalTime now = LocalTime.now();
        calendar.set(Calendar.HOUR_OF_DAY,now.getHour());
            calendar.set(Calendar.MINUTE, ((now.getMinute())%60+1));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            System.out.println(now.getHour()+ "시  "+ ((now.getMinute()%60)+1)+"분");
//        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 12345, intent , PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis() ,pendingIntent);
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() , pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        System.out.println("테스트 알람 등록"+" 현제 시간: "+System.currentTimeMillis()+" - 설정 시간: "+ calendar.getTimeInMillis() );

    }
    public void noti(){
        this.notificationReceiver = new NotificationReceiver();
        Intent intent = new Intent();
        intent.putExtra("weekday",Calendar.DAY_OF_WEEK-2);
        intent.putExtra("title","테스트 ");
//        notificationReceiver.onReceive(context,intent);
    }
    public void removeAlarmData(AlarmData alarmData){
        for(AlarmData alarmData1: alarmDataList){
            if(alarmData1.isTitleEqual(alarmData.getClassTitle())) alarmDataList.remove(alarmData1);
            return;
        }
    }

    public int convertDayNumb(int numb) {
    if(numb == 6) numb = 1;
     else numb = numb+2;
        return numb;
    }
}
