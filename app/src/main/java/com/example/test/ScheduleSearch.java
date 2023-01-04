package com.example.test;

import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ScheduleSearch {
    AssetManager assetManager;
    public ScheduleSearch(AssetManager assetManager){
        this.assetManager = assetManager;
    }


    public JSONObject search(String subjectNum) {
        System.out.println(subjectNum+" in search");

        try {
            InputStream inputStream = assetManager.open("ScheduleData/schedule.json");
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            StringBuffer buffer= new StringBuffer();
            String line = reader.readLine();
            while(line != null){
                buffer.append(line+"\n");
                line = reader.readLine();
            }
            JSONArray jsonArray = new JSONArray(buffer.toString());
            System.out.println("드디어 제이슨 어레이는 길이 "+jsonArray.length()+"에 도달했다");
            for(int i = 0 ; i<jsonArray.length();i++){
                JSONObject element = (JSONObject) jsonArray.opt(i);
                System.out.println("log--"+element.optString("강좌번호"));
                if(element.optString("강좌번호").equals(subjectNum)) {
                    System.out.println("마참내");
                    return element;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("제기랄");
        return null;
    }
}
