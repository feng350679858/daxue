package com.jingcai.apps.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jingcai.apps.custometoast.R;

/**
 * Created by lejing on 15/7/28.
 */
public class TopToast2 {
    public static void showToast(Context context, String message, int height) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.toast_layout, null);
        ((TextView) view.findViewById(com.jingcai.apps.custometoast.R.id.tv_text)).setText(message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, height);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }
}
