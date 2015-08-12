package com.jingcai.apps.aizhuan.activity.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class WordCountWatcher implements TextWatcher {
    private final TextView textView;
    private final int maxCount;

    public WordCountWatcher(TextView textView, int maxCount) {
        this.textView = textView;
        this.maxCount = maxCount;
    }

    public void afterTextChanged(Editable edt) {
        textView.setText(String.format("剩余%s字", (maxCount - edt.length())));
    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }
}