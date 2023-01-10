package com.example.timetable_20230106;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingDialog {

    View setting_dialog;
    Context context;
    String string;
    Switch switch_setting;


    public SettingDialog(Context context) {
        this.context = context;
    }

    public void showMenu(AlarmData alarmData) {
        AlertDialog.Builder dialog = new AlertDialog.Builder( this.context );
        dialog.setTitle(alarmData.getClassTitle());
        setting_dialog = (View) View.inflate( this.context , R.layout.setting_dialog, null);
        dialog.setView(setting_dialog);
        switch_setting = setting_dialog.findViewById(R.id.switch_setting);
        switch_setting.setChecked(alarmData.getIsOn());
        switch_setting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    alarmData.setIsOn(b);
            }
        });
        dialog.setPositiveButton("ok" ,null);
        dialog.show();
    }

}
