package com.jingcai.apps.aizhuan.activity.mine.help;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;

/**
 * Created by lejing on 15/8/13.
 */
public class MineHelpProcessUtil {
    private final BaseActivity baseActivity;
    private int[] status_node_id_arr = new int[]{R.id.layout_status_node_0, R.id.layout_status_node_1, R.id.layout_status_node_2, R.id.layout_status_node_3};
    private int[] status_node_id_arr2 = new int[]{R.id.cb_status_node_0, R.id.cb_status_node_1, R.id.cb_status_node_2, R.id.cb_status_node_3};
    private int[] status_line_id_arr = new int[]{R.id.cb_status_line_1, R.id.cb_status_line_2, R.id.cb_status_line_3, R.id.cb_status_line_4};
    private int[] status_name_id_arr = new int[]{R.id.tv_status_name_0, R.id.tv_status_name_1, R.id.tv_status_name_2, R.id.tv_status_name_3};

    private View[] status_node_arr = new View[status_name_id_arr.length];
    private CheckBox[] status_node_arr2 = new CheckBox[status_name_id_arr.length];
    private CheckBox[] status_line_arr = new CheckBox[status_name_id_arr.length];
    private TextView[] status_name_arr = new TextView[status_name_id_arr.length];

    public MineHelpProcessUtil(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public void initView() {
        for (int i = 0; i < status_name_id_arr.length; i++) {
            status_node_arr[i] = baseActivity.findViewById(status_node_id_arr[i]);
            status_node_arr2[i] = (CheckBox) baseActivity.findViewById(status_node_id_arr2[i]);
            status_line_arr[i] = (CheckBox) baseActivity.findViewById(status_line_id_arr[i]);
            status_name_arr[i] = (TextView) baseActivity.findViewById(status_name_id_arr[i]);
        }
    }

    public void setProcess(int nodeCount, int selectCount, String... label) {
        if (nodeCount < 2 || nodeCount > status_name_arr.length) {
            throw new RuntimeException("进度条显示个数出错");
        }
        if(null == label || nodeCount > label.length){
            throw new RuntimeException("进度条显示个数出错");
        }
        for (int i = nodeCount - 1; i < status_name_arr.length - 1; i++) {
            status_node_arr[i].setVisibility(View.GONE);
            status_name_arr[i].setVisibility(View.GONE);
            status_line_arr[i].setVisibility(View.GONE);
        }
        for(int i=0;i<nodeCount - 1;i++){
            status_name_arr[i].setText(label[i]);
            if(i<selectCount) {
                status_node_arr2[i].setChecked(true);
                status_line_arr[i].setChecked(true);
            }
        }
        //设置最后一个状态名称
        status_name_arr[status_name_arr.length - 1].setText(label[nodeCount - 1]);
        //如果状态全部完成，设置最后一条线的状态
        if(nodeCount == selectCount){
            status_line_arr[status_line_arr.length - 1].setChecked(true);
        }
    }
}
