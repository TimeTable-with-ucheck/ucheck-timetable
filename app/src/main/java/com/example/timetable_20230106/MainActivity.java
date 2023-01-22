package com.example.timetable_20230106;

import com.example.timetable_20230106.databinding.ActivityMainBinding;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    TimetableView Timetable;
    WebView webView;
    FloatingActionButton btn_addTimetable;
    Handler mHandler;
    Gson gson;
    AlarmService alarmService;
    ArrayList<Schedule> temp;
    private static final String packageName = "com.libeka.attendance.ucheckplusstud";
    AddSchedule addSchedule;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setTitle("TimeTable with UCheck");

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
              //  createNotification(schedules);
                SettingDialog settingDialog = new SettingDialog(MainActivity.this);
                settingDialog.showMenu(schedules,alarmService);
                alarmService.alarmTest();
                Toast.makeText(MainActivity.this,"hour:"+schedules.get(0).getStartTime().getHour() +" min :"+schedules.get(0).getStartTime().getMinute(),Toast.LENGTH_SHORT).show();
               }

            /**
             * 알람 강제 발생 메소드
             * @param schedules
             */
            private void createNotification(ArrayList<Schedule> schedules) {
                NotificationReceiver nr = new NotificationReceiver();
                Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
                Time time = schedules.get(0).getStartTime();
                intent.putExtra("weekday", Calendar.DAY_OF_WEEK-2);
                intent.putExtra("hour",time.getHour());
                intent.putExtra("minute",time.getMinute());
                intent.putExtra("title", schedules.get(0).getClassTitle());
                nr.onReceive(MainActivity.this,intent);
            }
        });



    }


    /**
     * url 입력창
     * @return
     */
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
                        System.out.println(((ArrayList<Schedule>)msg.obj));
                       if(((ArrayList<Schedule>)msg.obj).size()>0) {
                           ArrayList< ArrayList<Schedule>> Scheduless = convertScheduleList((ArrayList<Schedule>)msg.obj);
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

    /**
     * onCreate생성시 선언할 것들
     */
    private void init() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Timetable = findViewById(R.id.timetable);
        webView = binding.webView;
        btn_addTimetable = binding.btnAddTimetable;
        alarmService = new AlarmService(this);
        gson = new Gson();
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
        editor.putString("alarmDataList",  gson.toJson(alarmService.getAlarmDataList()));
        System.out.println("Timetable-save -> "+Timetable.createSaveData());
        System.out.println("dataList-save -> "+gson.toJson(alarmService.getAlarmDataList()));
        editor.commit();
    }
    private void restoreState(){
        SharedPreferences preferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        if ((preferences != null) && (preferences.contains("timetable"))) {
            Timetable.load(preferences.getString("timetable",""));
            String alarmDataListString = preferences.getString("alarmDataList",null);
            if(alarmDataListString!= null) {

                alarmService.setAlarmDataList(gson.fromJson(alarmDataListString, new TypeToken<ArrayList<AlarmData>>() {
                }.getType()));
            }else{
                System.out.println("list is null");
            }
            System.out.println("Timetable- -> "+preferences.getString("timetable",""));
            System.out.println("dataList-save -> "+alarmDataListString);
        }
    }
    //테스트용
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
            int i = getIdx(scheduless,schedule);
            if(i!=-1) scheduless.get(i).add(schedule);
            else {
               this.temp = new ArrayList<Schedule>();
               temp.add(schedule);
               scheduless.add(temp);
            }
        }
        alarmService.clearAlarm();
        alarmService.createAlarmData(scheduless);
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

    //title bar에 버튼 추가

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //title bar action

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.btn_add_schedule:
                    this.addSchedule = new AddSchedule(MainActivity.this);
                    this.addSchedule.addNewSchedule(this.Timetable);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}