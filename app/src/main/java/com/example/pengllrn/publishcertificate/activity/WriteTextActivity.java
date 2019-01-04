package com.example.pengllrn.publishcertificate.activity;

import android.content.Intent;
import android.content.SharedPreferences;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;

import android.os.Bundle;
import android.widget.Toast;

import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.base.BaseNfcActivity;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by pengllrn on 2019/1/4.
 */

public class WriteTextActivity extends BaseNfcActivity {
    private String mText = "";  //保存证书
    private Intent intent;
    private String result = "";//uid
    private String temp = "";
    private String mlogo;   //保存品牌
    private String mtype;   //保存随机产生的四位随机数
    private String num_zouyun = "";  //保存发送给服务器的证书
    private String uid_zouyun = ""; //保存发送给服务器的uid；
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_text);
    }
    @Override
    public void onNewIntent(Intent intent) {
        getNum();
        if (mText == null)
            return;
        //获取Tag对象
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        analysisTag(detectedTag);

        //写非NDEF格式的数据
        String[] techList = detectedTag.getTechList();
        boolean haveMifareUltralight = false;
        for (String tech : techList) {
            if (tech.indexOf("MifareUltralight") >= 0) {
                haveMifareUltralight = true;
                break;
            }
        }

        if (!haveMifareUltralight) {
            Toast.makeText(this, "不支持MifareUltralight数据格式", Toast.LENGTH_SHORT).show();
            return;
        }
        writeTag(detectedTag, mText);

        SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
        mlogo = pref.getString("Name", "");
        mtype = pref.getString("Type", "");

        /** 得到发给服务器的证书号*/
        for(int i = 0; i < 4; i++){
            char a = mText.charAt(i);
            String mstr = "3" + a;
            num_zouyun = num_zouyun + mstr;
        }

        /** 得到发给服务器的uid*/
        for(int i = 0; i < 6; i++){
            char a = result.charAt(i);
            uid_zouyun = uid_zouyun + a;
        }
        for (int i = 8; i < 16; i++){
            char a = result.charAt(i);
            uid_zouyun = uid_zouyun + a;
        }


        System.out.println("拼接过后的证书为：" + num_zouyun);
        System.out.println("发给服务器的uid为：" + uid_zouyun);
        System.out.println("品牌为：" + mlogo + "   " + "发证类型为：" + mtype + "    " + "证书为：" + num_zouyun
                + " " + "uid为：" + uid_zouyun + "    " + "状态位为：" + temp);
//        num_zouyun = "";
//        uid_zouyun = "";
        if((mlogo != "") && (mtype != "") && (num_zouyun.length() == 8) && (uid_zouyun.length() == 14))
        {
            sendRequestWithOkHttp();
            Toast.makeText(WriteTextActivity.this, "发证成功", Toast.LENGTH_SHORT).show();
        }else{
            System.out.println("发证失败");
            Toast.makeText(WriteTextActivity.this, "发证失败", Toast.LENGTH_SHORT).show();
        }

        num_zouyun = "";
        uid_zouyun = "";

    }

    /** 写非ndef格式数据
     *
     */
    public void writeTag(Tag tag, String num) {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
            ultralight.connect();
            ultralight.writePage(22, num.getBytes(Charset.forName("US-ASCII")));

            System.out.println("非ndef数据写入成功");
        } catch (Exception e) {
            System.out.println("非ndef写入异常");
        } finally {
            try {
                ultralight.close();
            } catch (Exception e) {
            }
        }
    }




    /** 读uid和状态位*/
    private void analysisTag(Tag tag) {

        if (tag != null) {
            //mView.restUI();
            MifareUltralight mifare = MifareUltralight.get(tag);
            try {
                mifare.connect();
                byte[] payload = mifare.readPages(0);
                for (int j = 0; j < 8; j++) {
                    temp = Integer.toHexString(payload[j] & 0xFF);
                    if (temp.length() == 1) {
                        temp = "0" + temp;
                    }
                    result += temp;
                }

                temp = "";
                int move = 0x80;
                byte[] transceive = mifare.transceive(new byte[]{0x30, -128});
                for (int i = 0; i < 5; i++) {
                    if ((transceive[0] & move) == 0) temp += "0";
                    else temp += "1";
                    move = move >> 1;
                }

            }catch (Exception e){
                System.out.println("读状态位异常");

            }
            try {
                mifare.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("uid为：" + result);
            System.out.println("状态位为：" + temp);
        }
    }

    /** 产生随机的4位数字证书*/
    public void getNum(){
        int a0, a1, a2, a3;
        a0 = (int) (Math.random() * 10);
        a1 = (int) (Math.random() * 10);
        a2 = (int) (Math.random() * 10);
        a3 = (int) (Math.random() * 10);

        mText = Integer.toString(a0) + Integer.toString(a1) + Integer.toString(a2) + Integer.toString(a3);

        SharedPreferences sharedPreferences = getSharedPreferences("certi", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("certificate",mText);
        editor.apply();

        System.out.println("随机产生的证书为：" + mText);
    }

    /** 向服务器发送请求测试*/
    public void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://47.107.37.50:8000/get_school_property/?schoolid=7")
                            .build();
                    Response response = client.newCall(request).execute();
                    System.out.println("发送网络请求是否成功：" + response.isSuccessful());
                }catch(Exception e){

                }
            }
        }).start();
    }
}
