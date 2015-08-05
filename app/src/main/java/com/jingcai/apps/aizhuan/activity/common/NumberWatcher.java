package com.jingcai.apps.aizhuan.activity.common;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class NumberWatcher implements TextWatcher {

    public void afterTextChanged(Editable edt) {
        String str = edt.toString();

        int posDot = str.indexOf("0");
        if (posDot == 0) {
            edt.delete(0, 1);
        }
    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

//    private String stringFilter(String str) throws PatternSyntaxException {
//        // 只保留数字，且不能已0开头
//        String regEx = "[^0-9]|(^0\\d*$)";
//        Pattern p = Pattern.compile(regEx);
//        Matcher m = p.matcher(str);
//        return m.replaceAll("").trim();
//    }
}