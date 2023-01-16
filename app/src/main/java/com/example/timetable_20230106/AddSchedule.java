package com.example.timetable_20230106;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


//강의 직접 추가하는 기능


public class AddSchedule {

    Context context;

    View add_schedule;
    EditText set_name, set_professor, set_place;

    Button btn_add;

    TextView  set_day, set_startTime ,set_endTime;
    ListView list_time, list_day;
    int startHour, startMin, endHour, endMin;
    List<String> list, list1;

    String time = "";
    String day = "";





    public AddSchedule(Context context){
        this.context = context;

    }

//커스텀 다이얼로그로 만들었어요
    @SuppressLint("ResourceType")
    public void addNewSchedule() {

        Dialog dialog = new Dialog(this.context);

        dialog.setContentView(R.layout.add_schedule_dialog);
        dialog.show();

        this.set_day = dialog.findViewById(R.id.set_day);

        this.set_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog listDialog = new Dialog(context);
                setDay(listDialog);
//                set_day.setText("           " + day +"요일");
                day = "";
            }
        });

        this.set_startTime = dialog.findViewById(R.id.set_starTime);
        
        this.set_startTime.setOnClickListener(new View.OnClickListener() {      // 시작 시간 textView 누르면 실행되는 메소드
            @Override
            public void onClick(View view) {
                Dialog listDialog = new Dialog(context);
                setTime(listDialog, 0); //listView 보여주는 메소드 호출


                time = "";

            }
        });
        this.set_endTime = dialog.findViewById(R.id.set_endTime);
        this.set_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog listDialog = new Dialog(context);
                setTime(listDialog, 1);

                time = "";
            }
        });
        this.btn_add = dialog.findViewById(R.id.btn_add);
        this.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "successfully added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
    private int getDay(String day){
        switch (day){
            case "월": return 0;
            case "화": return 1;
            case "수": return 2;
            case "목": return 3;
            case "금": return 4;
            case "토": return 5;
            case "일": return 6;
            default: return 9;
        }
    }
    public String setDay(Dialog listDialog) {
        listDialog.setContentView(R.layout.set_list_dialog);
        listDialog.show();
        list_day = listDialog.findViewById(R.id.list_time);
        list = new ArrayList<String>();
        list.add("월");
        list.add("화");
        list.add("수");
        list.add("목");
        list.add("금");
        list.add("토");
        list.add("일");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list);
        list_day.setAdapter(adapter);
        list_day.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                day = list.get(i);
                listDialog.dismiss();
                set_day.setText("           " + day +"요일");
                day = "";
            }
        });
        return day;
    }
    public String setTime(Dialog listDialog, int numb) {
        listDialog.setContentView(R.layout.set_list_dialog);
        listDialog.show();
        list_time = listDialog.findViewById(R.id.list_time);
        list = new ArrayList<String>();
        list.add("09");
        list.add("10");
        list.add("11");
        list.add("12");
        list.add("13");
        list.add("14");
        list.add("15");
        list.add("16");
        list.add("17");
        list.add("18");
        list.add("19");
        list.add("20");
        list.add("21");
        list.add("22");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list);
        list_time.setAdapter(adapter);
        list_time.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                time = list.get(i).toString();
                listDialog.dismiss();
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.set_list_dialog);
                dialog.show();
                list_time = dialog.findViewById(R.id.list_time);
                list1 = new ArrayList<String>();
                list1.add("00");
                list1.add("05");
                list1.add("10");
                list1.add("15");
                list1.add("20");
                list1.add("25");
                list1.add("30");
                list1.add("35");
                list1.add("40");
                list1.add("45");
                list1.add("50");
                list1.add("55");
                list1.add("60");
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list1);
                list_time.setAdapter(adapter1);
                
                list_time.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        time = time + " : " +list1.get(i).toString();
                        dialog.dismiss();
                        if(numb == 0) {
                            set_startTime.setText(time);
                            time = "";
                        } else {
                            set_endTime.setText(time);
                            time = "";
                        }
                    }
                });
            }
        });
        return time;
    }
}