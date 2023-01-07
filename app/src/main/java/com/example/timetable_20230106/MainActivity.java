package com.example.timetable_20230106;

import com.example.timetable_20230106.databinding.ActivityMainBinding;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private ActivityMainBinding binding;
    TimetableView Timetable;
    WebView webView;
    FloatingActionButton btn_addTimetable;
    Handler mHandler;
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
                System.out.println(idx);
                Toast.makeText(MainActivity.this, schedules.get(0).getClassTitle(),Toast.LENGTH_SHORT).show();
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
}