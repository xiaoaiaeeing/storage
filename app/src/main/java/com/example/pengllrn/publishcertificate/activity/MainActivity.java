package com.example.pengllrn.publishcertificate.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.base.BaseNfcActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseNfcActivity implements View.OnClickListener {
    /**
     * TextView选择框
     */
    private TextView mSelectTv;//品牌
    private TextView mtextview;//单双证
    private TextView mPublishNum;//发证数量

    /**
     * popup窗口里的ListView
     */
    private ListView mTypeLv;//品牌
    private ListView mNumLV;//单双证
    private ListView mPublishNumLv;

    /**
     * popup窗口
     */
    private PopupWindow typeSelectPopup;
    private PopupWindow myPopup;
    private PopupWindow mPublishNumPopup;

    /**
     * 模拟的品牌数据和单双证数据
     */
    private List<String> testData1;
    private List<String> testData2;
    private List<Integer> testData3;

    /**
     * 数据适配器
     */
    private ArrayAdapter<String> testDataAdapter;
    private ArrayAdapter<String> testDataAdapter1;
    private ArrayAdapter<Integer> testDataAdapter2;

    /**
     * ImageView
     */
    private ImageView imageView1;   //发证
    private ImageView imageView2;   //复制
    private ImageView imageView3;   //复制比对
    private ImageView imageView4;   //初始化
    private ImageView imageView5;   //aid写入

    /**
     * 保存随机数
     */
    private String passnum = "11111111";

    private Intent intent;

    /**
     * 保存品牌和单双证
     */
    private String logo;//品牌
    private String mtype;//单双证
    private int publishNum = 0;//发证次数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSelectTv.setText("品牌");
        mtextview.setText("发证类型");
        mPublishNum.setText("发证数量");
        logo = "";
        mtype = "";
        publishNum = 0;
        SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", logo);
        editor.putString("Type", mtype);
        editor.putInt("publishNum",publishNum);
        editor.apply();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        mSelectTv = (TextView) findViewById(R.id.tv_select_input);
        mtextview = (TextView) findViewById(R.id.tv_select_num);
        mPublishNum = (TextView) findViewById(R.id.tv_publish_num);
        imageView1 = (ImageView) findViewById(R.id.my_fa);
        imageView2 = (ImageView) findViewById(R.id.my_copy);
        imageView3 = (ImageView) findViewById(R.id.my_compare);
        imageView4 = (ImageView) findViewById(R.id.my_initialization);
//        imageView5 = (ImageView) findViewById(R.id.center_view);
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        mSelectTv.setOnClickListener(this);
        mtextview.setOnClickListener(this);
        mPublishNum.setOnClickListener(this);
        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        imageView4.setOnClickListener(this);
//        imageView5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_select_input:
                // 点击控件后显示popup窗口
                initSelectPopup();
                // 使用isShowing()检查popup窗口是否在显示状态
                if (typeSelectPopup != null && !typeSelectPopup.isShowing()) {
                    typeSelectPopup.showAsDropDown(mSelectTv, 0, 10);
                }
                break;
            case R.id.tv_select_num:
                // 点击控件后显示popup窗口
                initSelectPopup1();
                // 使用isShowing()检查popup窗口是否在显示状态
                if (myPopup != null && !myPopup.isShowing()) {
                    myPopup.showAsDropDown(mtextview, 0, 10);
                }
                break;
            case R.id.tv_publish_num:
                //点击控件后显示popup窗口
                iniSelectPopup2();
                // 使用isShowing()检查popup窗口是否在显示状态
                if (mPublishNumPopup != null && !mPublishNumPopup.isShowing()) {
                    mPublishNumPopup.showAsDropDown(mPublishNum, 0, 10);
                }
                break;
            case R.id.my_fa:
                SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Name", logo);
                editor.putString("Type", mtype);
                editor.putInt("publishNum",publishNum);
                editor.apply();
                intent = new Intent(MainActivity.this, WriteTextActivity.class);
                startActivity(intent);
                break;
            case R.id.my_copy:
                intent = new Intent(MainActivity.this, CopyActivity.class);
                startActivity(intent);
                break;
            case R.id.my_compare:
                intent = new Intent(MainActivity.this,CompareActivity.class);
                startActivity(intent);
                break;
            case R.id.my_initialization:
                intent = new Intent(MainActivity.this,InitializationActivity.class);
                startActivity(intent);
                break;
//            case R.id.center_view:
//                intent = new Intent(MainActivity.this,AidPublishActivity.class);
//                startActivity(intent);
//                break;
            default:
                break;
        }
    }

    /**
     * 初始化popup窗口1
     */
    private void initSelectPopup() {
        mTypeLv = new ListView(this);
        testData1 = new ArrayList<String>();
        testData1.add("五粮春");
        testData1.add("剑南春");
        testData1.add("洋河酒");
        testData1.add("茅台酒");
        testData1.add("泸州老窖");
        testData1.add("古井贡酒");
        testData1.add("杜康酒");
        testData1.add("郎酒");
        testData1.add("玉溪烟");
        testData1.add("黄鹤楼烟");
        testData1.add("黄山徽商烟");
        testData1.add("九寨沟烟");
        testData1.add("阿玛尼服饰");
        testData1.add("abcam");
        // 设置适配器
        testDataAdapter = new ArrayAdapter<String>(this, R.layout.popup_text_item, testData1);
        mTypeLv.setAdapter(testDataAdapter);

        // 设置ListView点击事件监听
        mTypeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 在这里获取item数据
                String value = testData1.get(position);
                logo = value;
                // 把选择的数据展示对应的TextView上
                mSelectTv.setText(value);
                // 选择完后关闭popup窗口
                typeSelectPopup.dismiss();
            }
        });
        typeSelectPopup = new PopupWindow(mTypeLv, mSelectTv.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
        // 取得popup窗口的背景图片
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_corner);
        typeSelectPopup.setBackgroundDrawable(drawable);
        typeSelectPopup.setFocusable(true);
        typeSelectPopup.setOutsideTouchable(true);
        typeSelectPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 关闭popup窗口
                typeSelectPopup.dismiss();
            }
        });
    }

    /**
     * 初始化popwindow2
     */
    private void initSelectPopup1() {
        mNumLV = new ListView(this);
        testData2 = new ArrayList<String>();
        testData2.add("单证");
        testData2.add("双证");
        //设置适配器
        testDataAdapter1 = new ArrayAdapter<String>(this, R.layout.popup_text_item, testData2);
        mNumLV.setAdapter(testDataAdapter1);

        //设置ListView的点击事件
        mNumLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 在这里获取item数据
                String value = testData2.get(position);
                // 把选择的数据展示对应的TextView上
                mtextview.setText(value);
                mtype = value;
                // 选择完后关闭popup窗口
                myPopup.dismiss();
            }
        });

        myPopup = new PopupWindow(mNumLV, mtextview.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_corner);
        myPopup.setBackgroundDrawable(drawable);
        myPopup.setFocusable(true);
        myPopup.setOutsideTouchable(true);
        myPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //关闭popup窗口
                myPopup.dismiss();
            }
        });
    }

    /**
     * 初始化popwindow3
     */
    private void iniSelectPopup2() {
        mPublishNumLv = new ListView(this);
        testData3 = new ArrayList<Integer>();
        testData3.add(1);
        testData3.add(2);
        testData3.add(3);
        testData3.add(4);
        testData3.add(5);
        //设置适配器
        testDataAdapter2 = new ArrayAdapter<Integer>(this, R.layout.popup_text_item, testData3);
        mPublishNumLv.setAdapter(testDataAdapter2);

        //设置ListView点击事件
        mPublishNumLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 在这里获取item数据
                int value = testData3.get(position);
                // 把选择的数据展示对应的TextView上
                mPublishNum.setText(value + "");
                publishNum = value;
                // 选择完后关闭popup窗口
                mPublishNumPopup.dismiss();
            }
        });
        mPublishNumPopup = new PopupWindow(mPublishNumLv, mPublishNum.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_corner);
        mPublishNumPopup.setBackgroundDrawable(drawable);
        mPublishNumPopup.setFocusable(true);
        mPublishNumPopup.setOutsideTouchable(true);
        mPublishNumPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //关闭popup窗口
                mPublishNumPopup.dismiss();
            }
        });
    }
}
