package com.example.myapplication.ui.home;

import static android.widget.Toast.makeText;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.webcrawler;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    TimetableView Timetable;
    WebView webView;
    FloatingActionButton btn_addTimetable;
    Handler mHandler;
    String URL = "https://everytime.kr/@9R3YWPXb3olBt7goJkJu";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Timetable = binding.timetable;
        webView = binding.webView;
        btn_addTimetable = binding.btnAddTimetable;
        btn_addTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setIcon(R.mipmap.ic_launcher);
                ad.setTitle("url입력");
                ad.setMessage("에타 시간표 공유 url을 넣어주세요");
                final EditText et = new EditText(getActivity());
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
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}