package com.jingcai.apps.aizhuan.activity.message;

import android.os.Bundle;
import android.widget.ListView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.adapter.message.CommentListAdapter;
import com.jingcai.apps.aizhuan.entity.TestCommentsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Json Ding on 2015/7/14.
 */
public class MessageCommendActivity extends BaseActivity {

    private ListView mLvComments;
    private CommentListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_comment);

        initView();
    }

    private void initView() {
        mLvComments = (ListView) findViewById(R.id.lv_comments);
        mListAdapter = new CommentListAdapter(this);
        List<TestCommentsBean> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if(i%2==0){
                list.add(new TestCommentsBean(null,"林"+i,null,
                        "林"+i+"赞了这个回复",
                        "昨天","回复"+i+"爷：我才最美！！！！",i+"爷",null,"萌萌哒"));
            }else {
                list.add(new TestCommentsBean(null,"林"+i,null,
                        "林"+i+"赞了这个求助",
                        "昨天",null,i+"爷",null,"萌萌哒"));
            }
        }
        mListAdapter.setListData(list);
        mLvComments.setAdapter(mListAdapter);
    }


}
