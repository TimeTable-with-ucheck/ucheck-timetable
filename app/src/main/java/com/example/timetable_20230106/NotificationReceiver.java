package com.example.timetable_20230106;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {
    private String TAG = this.getClass().getSimpleName();

    NotificationManager manager;
    NotificationCompat.Builder builder;

    //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";

    //수신되는 인텐트 - The Intent being received.
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(TAG+ "onReceive 알람이 들어옴!!");
        int day = intent.getIntExtra("weekday", -1)+2;
        String title = intent.getStringExtra("title");
        System.out.println("입력된 날자 = "+day+" 현재 날자 ="+Calendar.DAY_OF_WEEK);
        System.out.println("이름->" +title);
        if(Calendar.DAY_OF_WEEK != day)return;
        builder = null;

        //푸시 알림을 보내기위해 시스템에 권한을 요청하여 생성
        manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        //안드로이드 오레오 버전 대응
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        //알림창 제목
        builder.setContentTitle("유체크 알람"); //회의명노출
        builder.setContentText(title+" 수업 출석 가능 시간입니다");
        //알림창 아이콘
        builder.setSmallIcon(com.google.android.material.R.drawable.notification_template_icon_bg);
        //알림창 터치시 자동 삭제
        builder.setAutoCancel(true);

        //푸시알림 빌드
        Notification notification = builder.build();

        //NotificationManager를 이용하여 푸시 알림 보내기
        manager.notify(1,notification);
    }
}