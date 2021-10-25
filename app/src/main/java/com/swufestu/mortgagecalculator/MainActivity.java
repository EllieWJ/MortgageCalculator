package com.swufestu.mortgagecalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG="MainActivity";
    ViewPager2 viewPager;  //viewpager控制所有fragment
    private LinearLayout lls,llg,llz; //导航
    private ImageView ivs,ivg,ivz;  //导航图
    private ImageView ivcurrent;  //当前
    //存放Fargment
    private float one_r;
    private float five_r;
    private List<Fragment> fragmentList;
    Handler handler;
    Bundle bundle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPager();  //初始化viewPager
        initTabView();
        /**
        handler=new Handler(Looper.myLooper()){
            public void handleMessage(@NonNull Message msg){
                Log.i(TAG, "handleMessage: 收到消息");
                if(msg.what==6){
                    Bundle bdl=(Bundle)msg.obj;
                    one_r=bdl.getFloat("r1");
                    five_r=bdl.getFloat("r2");

                    Log.i(TAG, "handleMessage:one_r="+one_r);
                    Log.i(TAG, "handleMessage:five_r="+five_r);

                    Toast.makeText(MainActivity.this, "数据已更新", Toast.LENGTH_SHORT).show();

                }
                super.handleMessage(msg);
            }
        };
         **/
        /**
        //Activity利用bundle传值给fragment
        if(one_r!=0f){
            bundle = new Bundle();
            bundle.putFloat("mainr1",one_r);
            bundle.putFloat("mainr2",five_r);
            Log.i(TAG, "main获得的数据已经准备好");
            for(int i=0;i<100;i++){
                fragmentList.get(0).setArguments(bundle);
            }

        }

        RateThread rt=new RateThread(handler);
        Thread t=new Thread(rt);
        t.start();**/




    }

    private void initTabView() {  //处理导航更新
        //初始化
        lls=findViewById(R.id.id_tab_s);
        lls.setOnClickListener(this);
        llg=findViewById(R.id.id_tab_g);
        llg.setOnClickListener(this);
        llz=findViewById(R.id.id_tab_z);
        llz.setOnClickListener(this);

        ivs=findViewById(R.id.tab_iv_s);
        ivg=findViewById(R.id.tab_iv_g);
        ivz=findViewById(R.id.tab_iv_z);

        //设置最左页面为初始页面
        ivs.setSelected(true);
        ivcurrent=ivs;

    }

    private void initPager() {//适配viewpager
        //找到viewPager，实例化
        viewPager=findViewById(R.id.id_viewpager);
        //实例化List
        fragmentList=new ArrayList<Fragment>();
        Log.i(TAG, "main开始实例化fragment");
        fragmentList.add(new SFragment());
        fragmentList.add(new GFragment());
        fragmentList.add(new ZFragment());
        //实例化适配器
        MyFragmentPagerAdapter pagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(),getLifecycle(),fragmentList);
        //为viewPager加载适配器
        viewPager.setAdapter(pagerAdapter);//adapter是适配fragment
        //设置滑动监听接口
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                changeTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }


    private void changeTab(int position) {
        ivcurrent.setSelected(false);
        switch (position){
            case R.id.id_tab_s:
                viewPager.setCurrentItem(0);
            case 0:
                ivs.setSelected(true);
                ivcurrent=ivs;
                break;
            case R.id.id_tab_g:
                viewPager.setCurrentItem(1);
            case 1:
                ivg.setSelected(true);
                ivcurrent=ivg;
                break;
            case R.id.id_tab_z:
                viewPager.setCurrentItem(2);
            case 2:
                ivz.setSelected(true);
                ivcurrent=ivz;
                break;
        }
    }

    @Override
    public void onClick(View view) {
        changeTab(view.getId());
    }
}