package com.example.timetable_20230106;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class SettingDialog {

    View setting_dialog;
    Context context;
    Switch switch_setting;


    public SettingDialog(Context context) {
        this.context = context;
    }

    public void showMenu() {
        AlertDialog.Builder dialog = new AlertDialog.Builder( this.context );

        dialog.setTitle("Setting");

        setting_dialog = (View) View.inflate( this.context , R.layout.setting_dialog, null);
        dialog.setView(setting_dialog);

        dialog.setPositiveButton("input" ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        class visibilitySwitchListener implements CompoundButton.OnCheckedChangeListener{
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked) {

                                } else {

                                }
                            }
                        }

                    }

                });

        dialog.show();
    }
}
