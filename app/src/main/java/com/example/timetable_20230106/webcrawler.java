package com.example.timetable_20230106;

import static java.lang.Thread.sleep;

import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class webcrawler {
    private String URL;
    private WebView webView;
    private boolean isInit;
    private boolean isDataGet;
    private boolean isSendData;
    private boolean isFinished;
    private  Handler mHandler;
    private ArrayList<Schedule> schedules;
    private int counter;


    public webcrawler(String url, WebView webView, Handler mHandler ){
        this.URL = url;
        this.webView = webView;
        this.isInit =false;
        this.isDataGet = false;
        this.isSendData = false;
        this.schedules= new ArrayList<>();
        this.mHandler = mHandler;
        this.counter = 0;
        this.isFinished = false;
    }

    public void init(){
        webView.getSettings().setJavaScriptEnabled(true); //Javascript를 사용하도록 설정
        webView.addJavascriptInterface(new MyJavascriptInterface(), "Android");
        webView.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public void onLoadResource(WebView view, String url) {
                        if (!isDataGet) {
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);");
                            System.out.println("counter -> "+counter);

                            }
                        if(isDataGet&& !isSendData){
                            System.out.println("out");
                            Message message =  Message.obtain();
                            message.obj = schedules;
                            mHandler.sendMessage(message);
                            isSendData = true;
                        }
                        counter++;
                    }
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        if(!isSendData){
                            Message message =  Message.obtain();
                            message.obj = schedules;
                            mHandler.sendMessage(message);
                            isSendData = true;
                        }
                    }
                });
        isInit = true;

    }
    public void getTable(){
        if(isInit)
            webView.loadUrl(URL);
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
        @JavascriptInterface
        public void getHtml(String html) { //위 자바스크립트가 호출되면 여기로 html이 반환됨
            if(!isDataGet) {
                Document doc = Jsoup.parse(html);
                Elements timetable = doc.getElementsByClass("tablebody");
                Elements td = timetable.select("td");
                if(td.size()>1){
                    isDataGet = true;
                    for (int i = 0; i < td.size()-2; i++) {
                        Element cols = td.get(i);
                        for(Element e : cols.select("div div")){
                           Schedule schedule= addSchedule(e,i);
                           if(schedule != null)
                            schedules.add(schedule);
                       }
                    }
                }
            }
        }
        private Schedule addSchedule(Element elements, int day){
            if(elements.text().length()>1) {
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

