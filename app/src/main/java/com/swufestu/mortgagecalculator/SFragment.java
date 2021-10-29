package com.swufestu.mortgagecalculator;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SFragment extends Fragment{
    private static final String TAG="SFragment";
    //填写界面元素
    View view;
    Float one_r;
    Float five_r;
    TextView rateoutput1,peroutput1,yearoutput1;
    TextView test,outputtotal,chooseinfo5_way;
    EditText editinfo1_total,editinfo2_fper,editinfo3_year,editinfo4_rate,editinfo5_way;
    Button btn;
    //选项界面
    TextView onechoice;
    private ListView choicelist;
    ArrayList<HashMap<String,String>> choicelistItems;
    //ArrayList<Item> rlist;
    //结果展示界面元素
    TextView r_firstpayment,rblank,r_totalpayment,r_totalinterest,r_totalyear;
    TextView r_list1,r_list2,r_list3,r_list4,r_list5;
    List<Double> leftloan,permonthtotal,permonthcapital,permonthinterest;
    //ArrayList<Item> listItems;
    ArrayList<String> leftloanstr,permonthtotalstr,permonthcapitalstr,permonthintereststr;
    ArrayList<String> index;
    ArrayList<HashMap<String,String>> listItems;
    private ListView listrr;

    double totalpayments=0.0;  //总还款
    double totalinterest=0.0;  //总还款利息
    double info3_year=0.0;

    DecimalFormat df=new DecimalFormat("#.0");


//onCreate()和onCreateView()都是在activity调用setContentView()时被调用。
// 也就是activity尚未完成onCreate()，因此不应该在此加入与activity的view相关的代码。
// 而后面的生命周期这是在activity完成onCreate()之后调用的。
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate:");
/**调用子线程请求网络行不通
        handler=new Handler(Looper.myLooper()){
            public void handleMessage(@NonNull Message msg){
                Log.i(TAG, "handleMessage: 收到消息");
                if(msg.what==6){
                    Bundle bdl=(Bundle)msg.obj;
                    one_r=bdl.getFloat("r1");
                    five_r=bdl.getFloat("r2");

                    Log.i(TAG, "handleMessage:one_r="+one_r);
                    Log.i(TAG, "handleMessage:five_r="+five_r);

                  //  Toast.makeText(SFragment.this, "数据已更新", Toast.LENGTH_SHORT).show();

                }
                super.handleMessage(msg);
            }
        };
        RateThread rt=new RateThread(handler);
        Thread t=new Thread(rt);
        t.start();**/
        //尝试使用主线程请求网络
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        URL url=null;
        Document doc= null;
        try {
            doc = Jsoup.connect("https://www.bankofchina.com/fimarkets/lilv/fd32/201310/t20131031_2591219.html?keywords=LPR").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView:one_r="+one_r);
        view= inflater.inflate(R.layout.fragment_s, container, false);
        //initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated:one_r="+one_r);
        initView(view);
    }

    private void initView(View view) {
        Log.i(TAG, "initView:one_r="+one_r);
        rateoutput1=view.findViewById(R.id.textViewsr);
        rateoutput1.setText("最新LPR：一年期"+String.valueOf(one_r*100)+"%"+"，五年期"+String.valueOf(five_r*100)+"%");
        peroutput1=view.findViewById(R.id.s_pertip);
        peroutput1.setText(null);
        yearoutput1=view.findViewById(R.id.s_yeartip);
        yearoutput1.setText(null);


        editinfo1_total=view.findViewById(R.id.s_info1_total);
        editinfo2_fper=view.findViewById(R.id.s_info2_fper);
        editinfo3_year=view.findViewById(R.id.s_info3_year);
        editinfo4_rate=view.findViewById(R.id.s_info4_rate);
        chooseinfo5_way=view.findViewById(R.id.s_info5_way);
        btn=view.findViewById(R.id.button2);
        test=view.findViewById(R.id.textViewtest);
        outputtotal=view.findViewById(R.id.s_output1_total);

    }

    //在fragment不能直接进行点击事件，需要放到onActivityCreated中
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated:");
        Log.i(TAG, "onActivityCreated:test:"+test);
        Log.i(TAG, "onActivityCreated:r_firstpayment"+r_firstpayment);
        Log.i(TAG, "onActivityCreated:totalpayments"+totalpayments);

        //editinfo2_fper文本变化监听事件
        editinfo2_fper.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String total=editinfo1_total.getText().toString();
                String fper=editinfo2_fper.getText().toString();
                Log.i(TAG, "afterTextChanged:"+total);
                Log.i(TAG, "afterTextChanged:"+fper);
                double result=0.0;
                if((total.length()!=0)&&(fper.length()!=0)){
                    Log.i(TAG, "afterTextChanged:判断都不为空，进行计算");
                    double info1_total=Double.parseDouble(total);
                    double info2_fper=Double.parseDouble(fper);   //首付比例
                    result=info1_total *10000*(1-info2_fper/100);  //贷款金额
                    Log.i(TAG, "afterTextChanged:info2_fper:"+info2_fper);
                    if(info2_fper<30){
                        peroutput1.setText("商业贷款首付比例不得低于30%");
                    }else{
                        peroutput1.setText(null);
                    }
                    outputtotal.setText(df.format(result));
                }
                if(fper.length()==0){
                    outputtotal.setText(null);
                    peroutput1.setText(null);
                }

            }
        });
        //editinfo3_year文本变化监听事件
        editinfo3_year.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String year=editinfo3_year.getText().toString();
                Log.i(TAG, "afterTextChanged:年限"+year);

                if(year.length()!=0){
                    Log.i(TAG, "afterTextChanged:年限不为空");
                    double t=Double.parseDouble(year);
                    if(t>30){
                        yearoutput1.setText("房贷年限不得超过30年");
                    }
                    if(t<30&&year.contains(".")){
                        t=(int)t;
                        yearoutput1.setText("数值需为整数，将使用"+String.valueOf(t)+"年进行计算");
                    }
                }else{
                    yearoutput1.setText(null);
                }

            }
        });
        //chooseinfo5_way点击事件
        chooseinfo5_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choicelistItems=new ArrayList<HashMap<String,String>>();
                HashMap<String,String> map1=new HashMap<String,String>();
                map1.put("way","等额本息");
                choicelistItems.add(map1);
                HashMap<String,String> map2=new HashMap<String,String>();
                map2.put("way","等额本金");
                choicelistItems.add(map2);

                showWayChoice();
            }
        });
        //button点击事件
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String total=editinfo1_total.getText().toString();
                String fper=editinfo2_fper.getText().toString();
                String year=editinfo3_year.getText().toString();
                String rate=editinfo4_rate.getText().toString();
                String info5_way=chooseinfo5_way.getText().toString();

                double info1_total=Double.parseDouble(total);
                double info2_fper=Double.parseDouble(fper);   //首付比例

                info3_year=Integer.parseInt(year)*12;   //期数
                double info4_rate=Double.parseDouble(rate)/100;  //年利率
                /** 使用float计算使得计算数值有误差
                float info1_total=Float.parseFloat(total);
                float info2_fper=Float.parseFloat(fper);   //首付比例
                float info3_year=Float.parseFloat(year)*12;   //期数
                float info4_rate=Float.parseFloat(rate)/100;  //年利率  **/

                leftloan=new ArrayList<Double>();
                permonthtotal=new ArrayList<Double>();
                permonthcapital=new ArrayList<Double>();
                permonthinterest=new ArrayList<Double>();

                leftloanstr=new ArrayList<String>();
                permonthtotalstr=new ArrayList<String>();
                permonthcapitalstr=new ArrayList<String>();
                permonthintereststr=new ArrayList<String>();
                index=new ArrayList<String>();
                listItems =new ArrayList<HashMap<String,String>>();


                double totalloan=info1_total *10000*(1-info2_fper/100);  //贷款金额
                for(int i=1;i<=info3_year;i++){
                    index.add(String.valueOf(i));
                }

                //等额本息
                if("等额本息".equals(info5_way)){
                    double temp=0.0;  //月供总额
                    temp=calculatePMT(info4_rate,info3_year,totalloan);
                    totalpayments=temp*info3_year;
                    totalinterest=totalpayments-totalloan;

                    if(null==leftloan||leftloan.size()==0){ //首期
                        leftloan.add(totalloan);
                        permonthtotal.add(temp);
                        permonthinterest.add(leftloan.get(0)*info4_rate/12);
                        permonthcapital.add(permonthtotal.get(0)-permonthinterest.get(0));

                        leftloanstr.add(df.format(leftloan.get(0)));
                        permonthtotalstr.add(df.format(permonthtotal.get(0)));
                        permonthintereststr.add(df.format(permonthinterest.get(0)));
                        permonthcapitalstr.add(df.format(permonthcapital.get(0)));
                    }
                    for(int i=1;i<info3_year;i++){
                        leftloan.add(leftloan.get(i-1)-permonthcapital.get(i-1));
                        permonthtotal.add(temp);
                        permonthinterest.add(leftloan.get(i)*info4_rate/12);
                        permonthcapital.add(permonthtotal.get(i)-permonthinterest.get(i));

                        leftloanstr.add(df.format(leftloan.get(i)));
                        permonthtotalstr.add(df.format(permonthtotal.get(i)));
                        permonthintereststr.add(df.format(permonthinterest.get(i)));
                        permonthcapitalstr.add(df.format(permonthcapital.get(i)));
                    }


                    //test.setText(String.valueOf(totalpayments)+"    "+String.valueOf(leftloan.get(0))+" "+String.valueOf(permonthtotal.get(0))+" "+String.valueOf(permonthcapital.get(0))+" "+String.valueOf(permonthinterest.get(0)));

                }else{
                    //等额本金
                    double temp=0.0;  //月供本金
                    temp=totalloan/info3_year;

                    if(null==leftloan||leftloan.size()==0){//首期
                        leftloan.add(totalloan);
                        permonthcapital.add(temp);
                        permonthinterest.add(leftloan.get(0)*info4_rate/12);
                        permonthtotal.add(permonthcapital.get(0)+permonthinterest.get(0));

                        leftloanstr.add(df.format(leftloan.get(0)));
                        permonthtotalstr.add(df.format(permonthtotal.get(0)));
                        permonthintereststr.add(df.format(permonthinterest.get(0)));
                        permonthcapitalstr.add(df.format(permonthcapital.get(0)));
                    }

                    for(int i=1;i<info3_year;i++){
                        leftloan.add(leftloan.get(i-1)-permonthcapital.get(i-1));
                        permonthcapital.add(temp);
                        permonthinterest.add(leftloan.get(i)*info4_rate/12);
                        permonthtotal.add(permonthcapital.get(i)+permonthinterest.get(i));

                        leftloanstr.add(df.format(leftloan.get(i)));
                        permonthtotalstr.add(df.format(permonthtotal.get(i)));
                        permonthintereststr.add(df.format(permonthinterest.get(i)));
                        permonthcapitalstr.add(df.format(permonthcapital.get(i)));
                    }

                    for(int i=0;i<info3_year;i++){
                        totalpayments+=permonthtotal.get(i);
                    }
                    totalinterest=totalpayments-totalloan;

                    //test.setText(String.valueOf(totalpayments)+"    "+String.valueOf(leftloan.get(0))+" "+String.valueOf(permonthtotal.get(0))+" "+String.valueOf(permonthcapital.get(0))+" "+String.valueOf(permonthinterest.get(0)));

                }

                Log.i(TAG, "准备进入showDialog()函数");
                Log.i(TAG, "onActivityCreated:r_firstpayment"+r_firstpayment);
                Log.i(TAG, "onActivityCreated:totalpayments"+totalpayments);
                for(int i=0;i<info3_year;i++){
                    HashMap<String,String> map=new HashMap<String,String>();
                    map.put("index",index.get(i));//标题文字
                    map.put("leftloanstr",leftloanstr.get(i));//详情描述
                    map.put("permonthtotalstr",permonthtotalstr.get(i));
                    map.put("blank",null);
                    map.put("permonthcapitalstr",permonthcapitalstr.get(i));
                    map.put("permonthintereststr",permonthintereststr.get(i));

                    listItems.add(map);
                }
                //点击按钮弹出对话框
                showDialog();

                //test.setText(total+"    "+fper+"    "+year+"    "+rate+"    "+info5_way);
                Toast.makeText(getActivity(), "填写成功", Toast.LENGTH_LONG).show();
            }
        });
    }
    //初始化并弹出自定义选择贷款方式dialog
    private void showWayChoice() {
        Log.i(TAG, "showWayChoice:");
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.choice_list, null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(layout).create();

        Log.i(TAG, "showWayChoice:layout"+layout);

        onechoice=layout.findViewById(R.id.one_choice);
        choicelist=layout.findViewById(R.id.choice_list);

        SimpleAdapter listItemAdapter=new SimpleAdapter(getActivity(),
                choicelistItems,   //listItems 数据源
                R.layout.choice_item,  //ListItem的XML布局实现
                new String[]{"way"},
                new int[]{R.id.one_choice}
        );
        Log.i(TAG, "showWayChoice:choicelist"+choicelist);

        choicelist.setAdapter(listItemAdapter);
        dialog.show();
        choicelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,int position, long id) {
                String requiredvalue= ((TextView) view.findViewById(R.id.one_choice)).getText().toString();
                Log.i(TAG, "showWayChoice:choicelist"+requiredvalue);
                chooseinfo5_way.setText(requiredvalue);
                dialog.dismiss();
            }
        });


    }

    //初始化并弹出自定义结果dialog
    private void showDialog() {
        Log.i(TAG, "showDialog:");
        Log.i(TAG, "onActivityCreated:r_firstpayment"+r_firstpayment);
        Log.i(TAG, "onActivityCreated:totalpayments"+totalpayments);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.result_dialog_layout, null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(layout).create();

        Log.i(TAG, "onActivityCreated:r_firstpayment"+r_firstpayment);
        Log.i(TAG, "onActivityCreated:totalpayments"+totalpayments);
        //注意：此时获取控件id需要从View layout中获取，如果仍然从view中获取，则为空
        // （即使view定义为全局变量，也还是为空，还不知道原因）
        r_firstpayment=layout.findViewById(R.id.result_firstpayment);
        r_totalpayment=layout.findViewById(R.id.result_totalpayment);
        r_totalinterest=layout.findViewById(R.id.result_totalinterest);
        r_totalyear=layout.findViewById(R.id.result_totalyear);

        r_list1=layout.findViewById(R.id.result_list1);
        r_list2=layout.findViewById(R.id.result_list2);
        r_list3=layout.findViewById(R.id.result_list3);
        rblank=layout.findViewById(R.id.blank);
        r_list4=layout.findViewById(R.id.result_list4);
        r_list5=layout.findViewById(R.id.result_list5);
        listrr=layout.findViewById(R.id.mylistrr);

        r_firstpayment.setText(df.format(permonthtotal.get(0)));
        r_totalpayment.setText(df.format(totalpayments));
        r_totalinterest.setText(df.format(totalinterest));
        r_totalyear.setText(String.valueOf((int)info3_year));


        //生成适配器的Item和动态数组对应的元素
        SimpleAdapter listItemAdapter=new SimpleAdapter(getActivity(),
                listItems,   //listItems 数据源
                R.layout.list_item,  //ListItem的XML布局实现
                new String[]{"index","leftloanstr","permonthtotalstr","blank","permonthcapitalstr","permonthintereststr"},
                new int[]{R.id.result_list1,R.id.result_list2,R.id.result_list3,R.id.blank,R.id.result_list4,R.id.result_list5}
        );
        listrr.setAdapter(listItemAdapter);

        dialog.show();

        //dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/4*3), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    //计算等额本息中PMT数值
    private double calculatePMT(double rate,double term,double financeAmount) {  //年利率，期数，贷款金额

        double rr = rate/12;
        double denominator =(Math.pow((1 + rr), term) - 1);
        return (rr + (rr/denominator)) * financeAmount;

    }

}