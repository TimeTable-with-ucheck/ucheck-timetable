package com.example.test;

import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TimetableView Timetable;
    WebView webView;
    String URL = "https://everytime.kr/@Vvn84eIpVtMRAHaFXRym";
    MainHandler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timetable = findViewById(R.id.timetable);
        webView = (WebView) findViewById(R.id.webView);
        handler = new MainHandler();
        new webcrawling(URL, webView, handler).getTable();
    }
    class MainHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            // 3번, 핸들러 처리

            Bundle bundle = msg.getData();
            ArrayList<?> value = bundle.getParcelableArrayList("list");
            Timetable.add((ArrayList<Schedule>) value);
        }
    }
}