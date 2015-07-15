package com.jingcai.apps.aizhuan.activity.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class GoldWatcher implements TextWatcher {

    //监听改变的文本框   
    private EditText editText;

    /**
     * 构造函数
     */
    public GoldWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void onTextChanged(CharSequence ss, int start, int before, int count) {
        String editable = editText.getText().toString();
        String str = stringFilter(editable.toString());
        if (!editable.equals(str)) {
            editText.setText(str);
            //设置新的光标所在位置 www.2cto.com    
            editText.setSelection(str.length());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    public static String stringFilter(String str) throws PatternSyntaxException {
        // 只保留数字，且不能已0开头
        String regEx = "[^0-9]|(^0\\d*$)";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}