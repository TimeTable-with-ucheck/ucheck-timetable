package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.webcrawling;
import com.github.tlaabs.timetableview.TimetableView;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    TimetableView Timetable;
    WebView webView;
    String URL = "https://everytime.kr/@9R3YWPXb3olBt7goJkJu";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Timetable = binding.timetable;
        webView = binding.webView;
        webcrawling wc = new webcrawling(URL, webView);
        wc.init();
        wc.getTable();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}