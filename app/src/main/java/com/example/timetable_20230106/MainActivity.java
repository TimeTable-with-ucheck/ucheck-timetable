package com.example.timetable_20230106;

import com.example.timetable_20230106.databinding.ActivityMainBinding;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    TimetableView Timetable;
    WebView webView;
    FloatingActionButton btn_addTimetable;
    Handler mHandler;

    private AlarmManager alarmManager;
    private int hour, minute;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        btn_addTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = urlPopUp();
                ad.show();
            }
        });
        Timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                Toast.makeText(MainActivity.this, schedules.get(0).getClassTitle(),Toast.LENGTH_SHORT).show();
                NotificationReceiver nr = new NotificationReceiver();
                Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
                Time time = schedules.get(0).getStartTime();
                intent.putExtra("weekday", schedules.get(0).getDay());
                intent.putExtra("hour",time.getHour());
                intent.putExtra("minute",time.getMinute());
                intent.putExtra("title",schedules.get(0).getClassTitle());
                nr.onReceive(MainActivity.this,intent);
               }
        });
    }

    private AlertDialog.Builder urlPopUp(){
        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
        ad.setIcon(R.mipmap.ic_launcher);
        ad.setTitle("url입력");
        ad.setMessage("에타 시간표 공유 url을 넣어주세요");
        final EditText et = new EditText(MainActivity.this);
        ad.setView(et);
        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String result = et.getText().toString();
                ProgressDialog Loading = new ProgressDialog(MainActivity.this);
                Loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                Loading.setMessage("시간표 불러오는중");
                Loading.show();
                mHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        super.handleMessage(msg);
                       if(((ArrayList<Schedule>)msg.obj).size()>0) {
                           ArrayList<ArrayList<Schedule>> Scheduless = convertScheduleList((ArrayList<Schedule>)msg.obj);
                           Timetable.removeAll();
                           for (ArrayList<Schedule> schedules : Scheduless) {
                               Timetable.add(schedules);
                           }
                           Toast.makeText(MainActivity.this, "성공!", Toast.LENGTH_SHORT).show();
                           saveState();
                       }else {
                           Toast.makeText(MainActivity.this, "불러오기 실패", Toast.LENGTH_SHORT).show();
                       }
                        Loading.dismiss();
                    }
                };
                webcrawler wc = new webcrawler(result,webView,mHandler);
                wc.init();
                wc.getTable();
            }
        });
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return ad;
    }

    private void init() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Timetable = findViewById(R.id.timetable);
        webView = binding.webView;
        btn_addTimetable = binding.btnAddTimetable;
        alarmManager= (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    protected void onResume(){
        super.onResume();
        restoreState();
    }

    @Override
    protected void onPause(){
        super.onPause();
        saveState();
       // resetState();
    }
    private void saveState(){
        SharedPreferences preferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("timetable",Timetable.createSaveData());
        System.out.println("저장한단디야 -> "+Timetable.createSaveData());
        editor.commit();
    }
    private void restoreState(){
        SharedPreferences preferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        if ((preferences != null) && (preferences.contains("timetable"))) {
            Timetable.load(preferences.getString("timetable",""));
            System.out.println("꺼내온단디야 -> "+preferences.getString("timetable",""));
        }
    }
    private void resetState(){
        SharedPreferences preferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        System.out.println("reset");
        editor.commit();
    }

    private ArrayList<ArrayList<Schedule>> convertScheduleList(ArrayList<Schedule> schedules){
        ArrayList<ArrayList<Schedule>> scheduless = new ArrayList<ArrayList<Schedule>>();
        for(Schedule schedule : schedules){
            regist(schedule);
            int i = getIdx(scheduless,schedule);
            if(i!=-1) scheduless.get(i).add(schedule);
            else {
               ArrayList<Schedule> temp = new ArrayList<Schedule>();
               temp.add(schedule);
               scheduless.add(temp);
            }
        }
        return scheduless;
    }

    private int getIdx(ArrayList<ArrayList<Schedule>> list,Schedule schedule){
        int i = 0;
        for (ArrayList<Schedule> schedules : list){
           if(schedules.get(0).getClassTitle().equals(schedule.getClassTitle()))return i;
            i++;
        }
        return -1;
    }
    public void regist(Schedule schedule) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Time time = schedule.getStartTime();
            hour=time.getHour();
            minute=time.getMinute()-10;
        }else{
            Toast.makeText(this, "버전을 확인해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("weekday", schedule.getDay());
        intent.putExtra("hour",hour);
        intent.putExtra("minute",minute);
        intent.putExtra("title",schedule.getClassTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 123, intent, PendingIntent.FLAG_IMMUTABLE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        System.out.println("알람 설정:-> "+schedule.getClassTitle()+"설정 시간: "+calendar.getTimeInMillis()+" 설정 요일"+(schedule.getDay()+2));
        System.out.println("알람 설정:-> 현제 시간"+System.currentTimeMillis()+" 지금 날짜: "+calendar.DAY_OF_WEEK);
        long intervalDay = 24 * 60 * 60 * 1000;// 24시간
        long selectTime=calendar.getTimeInMillis();
        long currenTime=System.currentTimeMillis();
        if(currenTime>selectTime){
            selectTime += intervalDay;
        }
        // 10초 뒤에 시작해서 매일 같은 시간에 반복하기
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, selectTime, intervalDay, pendingIntent);

    }// regist()..


}