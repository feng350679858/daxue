package com.jingcai.apps.aizhuan.activity.message;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.adapter.message.CommentListAdapter;

/**
 * Created by Json Ding on 2015/7/14.
 */
public class MessageCommentActivity extends BaseActivity {

    private ListView mLvComments;
    private CommentListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_comment);
        initHeader();
        initView();
    }


    private void initHeader() {
        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        //需要用到再findViewById，不要需则不调用，提高效率
//        ImageView ivFunc = (ImageView) findViewById(R.id.iv_func);
//        TextView tvFunc = (TextView) findViewById(R.id.tv_func);

        tvTitle.setText("评论");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initView() {
        mLvComments = (ListView) findViewById(R.id.lv_comments);
        mListAdapter = new CommentListAdapter(this);
//        mListAdapter.setListData(list);
        mLvComments.setAdapter(mListAdapter);
    }


}
