package com.example.pengllrn.publishcertificate.activity;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.base.BaseNfcActivity;
import com.example.pengllrn.publishcertificate.utils.UriPrefix;

import java.nio.charset.Charset;

public class AidPublishActivity extends BaseNfcActivity {

    private String mUri = "d156000139://\033\001\000\001\000\001\000\001\000\001\000";
    private boolean isSuccessfullyWritted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_publish);
    }

    @Override
    public void onNewIntent(Intent intent) {

        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{createUriRecord(mUri)});
        boolean result = writeTag(ndefMessage, detectedTag);
        if (result) {
            Toast.makeText(this, "aid写入成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "aid写入失败", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 将Uri转成NdefRecord
     *
     * @param uriStr
     * @return
     */
    public static NdefRecord createUriRecord(String uriStr) {
        byte prefix = 0;
        for (Byte b : UriPrefix.URI_PREFIX_MAP.keySet()) {
            String prefixStr = UriPrefix.URI_PREFIX_MAP.get(b).toLowerCase();
            if ("".equals(prefixStr))
                continue;
            if (uriStr.toLowerCase().startsWith(prefixStr)) {
                prefix = b;
                uriStr = uriStr.substring(prefixStr.length());
                break;
            }
        }
        byte[] data = new byte[1 + uriStr.length()];
        data[0] = prefix;
        System.arraycopy(uriStr.getBytes(), 0, data, 1, uriStr.length());
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], data);
        return record;
    }

    /**
     * 写入标签
     *
     * @param message
     * @param tag
     * @return
     */
    public static boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    return false;
                }
                ndef.writeNdefMessage(message);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
}