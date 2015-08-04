package com.jingcai.apps.aizhuan.adapter.mine;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.service.business.school.school01.School01Response;
import com.jingcai.apps.aizhuan.service.business.school.school03.School03Response;

import java.util.List;

import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

/**
 * Created by Administrator on 2015/7/28.
 */
public class AreaAdapter extends AbstractWheelTextAdapter {
    // Countries names
    private List<School03Response.Body.Areainfo> areaArrayList;

    /**
     * Constructor
     */
    public AreaAdapter(Context context,List<School03Response.Body.Areainfo> areaList) {
        super(context, R.layout.area_adapter_item,NO_RESOURCE);
        setItemTextResource(R.id.area_name);
        areaArrayList=areaList;
    }

    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        View view = super.getItem(index, cachedView, parent);
        TextView textView=(TextView)view.findViewById(R.id.area_name);
        textView.setText(areaArrayList.get(index).getName());
        return view;
    }

    @Override
    public int getItemsCount() {
        return areaArrayList.size();
    }

    @Override
    public CharSequence getItemText(int index) {
        return areaArrayList.get(index).getCode();
    }
    public CharSequence getItemName(int index) {
        return areaArrayList.get(index).getName();
    }
}