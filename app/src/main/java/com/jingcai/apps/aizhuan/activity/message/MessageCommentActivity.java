package com.jingcai.apps.aizhuan.activity.message;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.adapter.message.CommentListAdapter;
import com.jingcai.apps.aizhuan.entity.TestCommentsBean;

import java.util.ArrayList;
import java.util.List;

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
        List<TestCommentsBean> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                list.add(new TestCommentsBean(null, "林" + i, null,
                        "回复" + i + "爷：我最美！我最美！我最美！我最美！我最美！",
                        "昨天", "回复" + i + "爷：我才最美！！！！", i + "爷", null, "萌萌哒"));
            } else {
                list.add(new TestCommentsBean(null, "林" + i, null,
                        "回复" + i + "爷：我最美！我最美！我最美！我最美！我最美！",
                        "昨天", null, i + "爷", null, "萌萌哒"));
            }
        }
        mListAdapter.setListData(list);
        mLvComments.setAdapter(mListAdapter);
    }


}
