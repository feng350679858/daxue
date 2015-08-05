package com.jingcai.apps.aizhuan.activity.common;

import android.text.Editable;
import android.text.TextWatcher;

public class MoneyWatcher implements TextWatcher {

    public void afterTextChanged(Editable edt) {
        String str = edt.toString();
        if(str.length() > 1 && str.charAt(0) == '0' && str.charAt(1) != '.'){
            edt.delete(0, 1);
            str = edt.toString();
        }
        int posDot = str.indexOf(".");
        if (posDot < 0) return;
        if (posDot == 0) {
            edt.insert(0, "0");
            return;
        }
        if (str.length() - posDot - 1 > 2) {
            edt.delete(posDot + 3, posDot + 4);
        }
    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }
}