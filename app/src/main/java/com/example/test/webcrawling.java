package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class webcrawling{
    private String URL;
    private WebView webView;
    private Handler handler;
    private boolean isInit;


    public webcrawling(String url, WebView webView , Handler handler){
        this.URL = url;
        this.webView = webView;
        this.handler = handler;
        this.isInit =false;
    }

    public void init(){
        webView.getSettings().setJavaScriptEnabled(true); //Javascript를 사용하도록 설정
        webView.addJavascriptInterface(new MyJavascriptInterface(), "Android");
        webView.loadUrl(URL);
        isInit = true;

    }
    public void getTable(){
        if(!isInit)this.init();
            webView.setWebViewClient(
                    new WebViewClient() {
                        @Override
                        public void onLoadResource(WebView view, String url){
                            view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);");
                        }
                    });
        }
    private String getDay(int day){
        switch (day){
            case 0: return "월";
            case 1: return "화";
            case 2: return "수";
            case 3: return "목";
            case 4: return "금";
            default: return "뭔데";
        }
    }

    public class MyJavascriptInterface {
        boolean getData = false;
        @JavascriptInterface
        public void getHtml(String html) { //위 자바스크립트가 호출되면 여기로 html이 반환됨
            if(!getData) {
                Document doc = Jsoup.parse(html);
                Elements timetable = doc.getElementsByClass("tablebody");
                Elements td = timetable.select("td");
                System.out.println("---------------------------------");
                if(td.size()>1){
                    ArrayList<Schedule> schedules = new ArrayList<>();
                    getData = true;
                    for (int i = 0; i < td.size()-2; i++) {
                        System.out.println(getDay(i) + " ->");
                        Element cols = td.get(i);
                        for(Element e : cols.select("div div")){
                            schedules.add(addSchedule(e,i));
                       }
                    }
                }
            }

        }
        private Schedule addSchedule(Element elements, int day){
            if(elements.text().length()>1) {
                printTest(elements,day);
                Schedule schedule = new Schedule();
                schedule.setClassTitle(elements.select("h3").text());
                schedule.setClassPlace(elements.select("p span").text()); // sets place
                schedule.setProfessorName(elements.select("p em").text()); // sets professor
                schedule.setDay(day);
                String style = elements.select("div").attr("style");
                int time = onlyInt(style.split(";")[1]) / 60;
                int min = onlyInt(style.split(";")[1]) % 60;
                int timeLong = (onlyInt(style.split(";")[1]) + onlyInt(style.split(";")[0])) / 60;
                int minLong = (onlyInt(style.split(";")[1]) + onlyInt(style.split(";")[0])) % 60 - 1;
                schedule.setStartTime(new Time(time, min)); // sets the beginning of class time (hour,minute)
                schedule.setEndTime(new Time(timeLong, minLong)); // sets the end of class time (hour,minute)
                return schedule;
            }
           return null;
        }
        private void printTest(Element elements, int day){
            if(elements.text().length()>1) {
                System.out.println("day: " + day);
                System.out.println(elements.select("h3").text());
                System.out.println(elements.select("p span").text());
                System.out.println(elements.select("em").text());
                String style = elements.select("div").attr("style");

                int time = onlyInt(style.split(";")[1])/60;
                int min = onlyInt(style.split(";")[1])%60;
                int timeLong = (onlyInt(style.split(";")[1])+onlyInt(style.split(";")[0]))/60;
                int minLong =  (onlyInt(style.split(";")[1])+onlyInt(style.split(";")[0]))%60 -1;

                System.out.println("시작 시간: "+time+"시 "+min+"분  강의 끝: "+timeLong+"시간 "+minLong+" 분");
            }


        }
        private int onlyInt(String string){
           return Integer.parseInt(string.replaceAll("[^0-9]", ""));
        }
    }
}

