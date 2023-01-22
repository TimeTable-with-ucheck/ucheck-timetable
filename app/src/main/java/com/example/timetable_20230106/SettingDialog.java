package com.example.timetable_20230106;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
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
    Button btn_remove;


    public SettingDialog(Context context) {
        this.context = context;
    }

    public void showMenu(ArrayList<Schedule> schedules, AlarmService alarmService, TimetableView Timetable) {

        AlertDialog.Builder dialog = new AlertDialog.Builder( this.context );
        AlarmData alarmData = alarmService.findAlarmData(schedules);

        dialog.setTitle(alarmData.getClassTitle());
        this.setting_dialog = (View) View.inflate( this.context , R.layout.setting_dialog, null);
        dialog.setView(this.setting_dialog);
        this.switch_setting = setting_dialog.findViewById(R.id.switch_setting);
        this.switch_setting.setChecked(alarmData.getIsOn());
        this.switch_setting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    alarmData.setIsOn(b);

            }
        });
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alarmService.patchAlarm(alarmData);
            }
        });

        btn_remove = setting_dialog.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timetable.remove(0);
            }
        });



        dialog.show();
    }

}
