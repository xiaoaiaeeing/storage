package com.example.pengllrn.publishcertificate.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.base.BaseNfcActivity;
import com.example.pengllrn.publishcertificate.constant.Constant;
import com.example.pengllrn.publishcertificate.internet.OkHttp;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by pengllrn on 2019/1/4.
 */

public class CopyActivity extends BaseNfcActivity {

    private String cText;//复制的证书
    private String mlogo;
    private String mtype;
    private String result;//uid
    private String temp;//状态位
    private String certi_server;//发给服务器的证书
    private String uid_server;//发给服务器的uid
    private String applyUrl = Constant.SERVER_URL;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0x2020:

            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy);
    }

    @Override
    public void onNewIntent(Intent intent) {
        SharedPreferences pref = getSharedPreferences("certi", MODE_PRIVATE);
        cText = pref.getString("certificate","");
        System.out.println("The copied certification is : " + cText);
        if (cText == null)
            return;
        //获取Tag对象
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        analysisTag(detectedTag);
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

        writeTag(detectedTag, cText);

        SharedPreferences pf = getSharedPreferences("Info", MODE_PRIVATE);
        mlogo = pf.getString("Name", "");
        mtype = pf.getString("Type", "");

        /**
         * 得到发给服务器的证书号
         */
        for(int i = 0; i < 4; i++){
            char a = cText.charAt(i);
            String mstr = "3" + a;
            certi_server = certi_server + mstr;
        }

        /**
         * 得到发给服务器的uid
         */
        for(int i = 0; i < 6; i++){
            char a = result.charAt(i);
            uid_server = uid_server + a;
        }
        for (int i = 8; i < 16; i++){
            char a = result.charAt(i);
            uid_server = uid_server + a;
        }

        if ((mlogo != "") && (mtype != "")) {
            OkHttp okHttp = new OkHttp(getApplicationContext(),mHandler);
            okHttp.getFromInternet(applyUrl);
            Toast.makeText(CopyActivity.this, "发证成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(CopyActivity.this, "请选择品牌或者单双证类型", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 写非ndef格式数据
     * @param tag
     * @param num
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

    /**
     * 读uid和状态位
     * @param tag
     */
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
}
