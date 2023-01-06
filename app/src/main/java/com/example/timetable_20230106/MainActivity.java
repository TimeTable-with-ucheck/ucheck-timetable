package com.example.timetable_20230106;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.timetable_20230106.databinding.ActivityMainBinding;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    TimetableView Timetable;
    WebView webView;
    FloatingActionButton btn_addTimetable;
    Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View root = binding.getRoot();
        Timetable = binding.timetable;
        webView = binding.webView;
        btn_addTimetable = binding.btnAddTimetable;
        btn_addTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        mHandler = new Handler(){
                            @Override
                            public void handleMessage(Message msg){
                                System.out.println("get! 드디어!");
                                super.handleMessage(msg);
                                Timetable.removeAll();
                                Timetable.add((ArrayList<Schedule>)msg.obj);
                                saveState();

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
                ad.show();
            }
        });
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
    }
    private void saveState(){
        SharedPreferences preferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("timetable",binding.timetable.createSaveData());
        System.out.println("저장한단디야 -> "+binding.timetable.createSaveData());
        editor.commit();
    }
    private void restoreState(){
        SharedPreferences preferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        if ((preferences != null) && (preferences.contains("timetable"))) {
            binding.timetable.load(preferences.getString("timetable",""));
            System.out.println("꺼내온단디야 -> "+preferences.getString("timetable",""));
        }
    }
}