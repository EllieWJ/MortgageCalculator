package com.swufestu.mortgagecalculator;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RateThread implements Runnable{
    private  static final String TAG="RateThread";

    private Handler handler;
    public RateThread(Handler handler){
        this.handler=handler;
    }

    @Override
    public void run() {
        URL url=null;
        float one_r=0f;
        float five_r=0f;
        Bundle bundle=new Bundle();
        try {
            Document doc= Jsoup.connect("https://www.bankofchina.com/fimarkets/lilv/fd32/201310/t20131031_2591219.html?keywords=LPR").get();
            //Document doc= Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
            Log.i(TAG, "run: title="+doc.title());

            Elements tables=doc.getElementsByTag("table");
            Element table=tables.first();

            Elements trs=table.getElementsByTag("tr");
            trs.remove(0);

            Element first_tr=trs.first();
            Elements tds=first_tr.getElementsByTag("td");

            String onerate=tds.get(1).text();
            String fiverate=tds.get(2).text();
            Log.i(TAG, "run: 一年期="+onerate);
            Log.i(TAG, "run: 五年期以上="+fiverate);

            onerate = onerate.replace("%","");
            one_r = Float.valueOf(onerate) / 100;
            fiverate = fiverate.replace("%","");
            five_r = Float.valueOf(fiverate) / 100;


            bundle.putFloat("r1",one_r);
            bundle.putFloat("r2",five_r);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message msg=handler.obtainMessage(6,bundle);
        handler.sendMessage(msg);
        Log.i(TAG, "run: 消息已发送");

    }
}
