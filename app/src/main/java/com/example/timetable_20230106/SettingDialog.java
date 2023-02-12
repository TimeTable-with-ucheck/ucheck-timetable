package com.example.timetable_20230106;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;

import java.util.ArrayList;

public class SettingDialog {

    View setting_dialog;
    Context context;
    String string;
    Switch switch_setting;
    Button btn_remove, btn_ok;
    TextView tv_title;


    public SettingDialog(Context context) {
        this.context = context;
    }

    public void showMenu(ArrayList<Schedule> schedules, AlarmService alarmService, TimetableView Timetable,int idx) {

        Dialog dialog = new Dialog(this.context);
        AlarmData alarmData = alarmService.findAlarmData(schedules);

//        dialog.setTitle(alarmData.getClassTitle());
        dialog.setContentView(R.layout.setting_dialog);
        tv_title = dialog.findViewById(R.id.tv_title);
        tv_title.setText("  "+alarmData.getClassTitle());
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.switch_setting = dialog.findViewById(R.id.switch_setting);
        this.switch_setting.setChecked(alarmData.getIsOn());
        this.switch_setting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    alarmData.setIsOn(b);
            }
        });
//        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                alarmService.patchAlarm(alarmData);
//            }
//        });
        this.btn_ok = dialog.findViewById(R.id.btn_ok);
        this.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarmService.patchAlarm(alarmData);
                dialog.dismiss();
            }
        });
        btn_remove = dialog.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timetable.remove(idx);
                alarmService.unRegist(alarmData);
                alarmService.removeAlarmData(alarmData);
                dialog.dismiss();
            }
        });



        dialog.show();
    }

}
