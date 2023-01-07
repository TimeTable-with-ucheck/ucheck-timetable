package com.example.timetable_20230106;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class scheduleModifyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Popup의 Title을 제거
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
    }
}
