package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.help.GroupAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Request;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob16.Partjob16Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob17.Partjob17Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob28.Partjob28Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob28.Partjob28Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lejing on 15/7/14.
 */
public class HelpWendaDeployActivity extends BaseActivity {
    private MessageHandler messageHandler;

    private Button btn_wenda_help;
    private EditText et_content;

    // 定义字符串数组作为提示的文本
    String[] books = new String[]{"Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
            "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
            "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
            "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
            "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia",
            "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory",
            "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi",
            "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"};
    private TextView tv_group;
    private CheckBox cb_anonymous;
    private TopicAdapter adapter;
    private PopupWin groupWin;
    private GroupAdapter groupAdapter;
    private XListView groupListView;
    private int mCurrentStart;
    private AutoCompleteTextView actv_topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.help_wenda_deploy);
        messageHandler = new MessageHandler(this);

        initHeader();

        initView();
    }

    private void initHeader() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        tvTitle.setText("发布求问");

        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    private class MyWatcher implements TextWatcher {
//        public void afterTextChanged(Editable s) {
//            doAfterTextChanged();
//        }
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        }
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//        }
//    }

    class TopicAdapter extends BaseAdapter implements ListAdapter, Filterable {
        private Context ctx;
        private LayoutInflater mInflater;
        private List<Partjob28Response.Parttimejob> list;
        private MyFilter mFilter;

        public void setList(List<Partjob28Response.Parttimejob> list) {
            this.list = list;
        }

        public TopicAdapter(Context ctx) {
            this.ctx = ctx;
            mInflater = LayoutInflater.from(ctx);
        }

        @Override
        public int getCount() {
            return null == list ? 0 : list.size();
        }

        @Override
        public Partjob28Response.Parttimejob getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.popup_list_item2, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_pop_item = (TextView) convertView.findViewById(R.id.tv_pop_item);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Partjob28Response.Parttimejob job = getItem(position);
            viewHolder.job = job;
            viewHolder.tv_pop_item.setText(job.getTopicname());
            return convertView;
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new MyFilter();
            }
            return mFilter;
        }

        class MyFilter extends Filter {
            //private List<String> mObjects;
            private final Object mLock = new Object();
            private ArrayList<String> mOriginalValues;

            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
//                FilterResults results = new FilterResults();
//                if (mOriginalValues == null) {
//                    synchronized (mLock) {
//                        mOriginalValues = new ArrayList<String>(list);
//                    }
//                }
//                if (prefix == null || prefix.length() == 0) {
//                    ArrayList<String> list;
//                    synchronized (mLock) {
//                        list = new ArrayList<String>(mOriginalValues);
//                    }
//                    results.values = list;
//                    results.count = list.size();
//                } else {
//                    String prefixString = prefix.toString().toLowerCase();
//                    ArrayList<String> values;
//                    synchronized (mLock) {
//                        values = new ArrayList<String>(mOriginalValues);
//                    }
//                    final int count = values.size();
//                    final ArrayList<String> newValues = new ArrayList<String>();
//
//                    for (int i = 0; i < count; i++) {
//                        final String value = values.get(i);
//                        final String valueText = value.toLowerCase();
//
//                        // First match against the whole, non-splitted value
//                        if (valueText.startsWith(prefixString)) {
//                            newValues.add(value);
//                        } else {
//                            final String[] words = valueText.split(" ");
//                            final int wordCount = words.length;
//
//                            // Start at index 0, in case valueText starts with space(s)
//                            for (int k = 0; k < wordCount; k++) {
//                                if (words[k].startsWith(prefixString)) {
//                                    newValues.add(value);
//                                    break;
//                                }
//                            }
//                        }
//                    }
//
//                    results.values = newValues;
//                    results.count = newValues.size();
//                }
//                return results;
                doFilter(prefix.toString().toLowerCase());
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
//                list = (List) results.values;
//                if (results.count > 0) {
//                    notifyDataSetChanged();
//                } else {
//                    notifyDataSetInvalidated();
//                }
            }
        }
    }

    private void doFilter(final String filter) {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Partjob28Request req = new Partjob28Request();
                Partjob28Request.Parttimejob job = req.new Parttimejob();
                job.setTopicname(filter);
                req.setParttimejob(job);
                new AzService().doTrans(req, Partjob28Response.class, new AzService.Callback<Partjob28Response>() {
                    @Override
                    public void success(Partjob28Response resp) {
                        if ("0".equals(resp.getResultCode())) {
                            List<Partjob28Response.Parttimejob> list = resp.getBody().getParttimejob_list();
                            if (null == list) {
                                list = new ArrayList<Partjob28Response.Parttimejob>();
                            }
                            messageHandler.postMessage(11, list);
                        } else {
                            messageHandler.postMessage(12, resp.getResultMessage());
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        });
    }

    public class ViewHolder {
        public Partjob28Response.Parttimejob job;
        public TextView tv_pop_item;
    }

    private void initView() {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.popup_list_item2, books);
        adapter = new TopicAdapter(this);
//        adapter.setList(Arrays.asList(books));
        actv_topic = (AutoCompleteTextView) findViewById(R.id.actv_topic);
        actv_topic.setAdapter(adapter);
//        actv_topic.addTextChangedListener(new MyWatcher());

        et_content = (EditText) findViewById(R.id.et_content);
        tv_group = (TextView) findViewById(R.id.tv_group);
        tv_group.setTag(UserSubject.getSchoolid());
        tv_group.setText(UserSubject.getSchoolname());
        cb_anonymous = (CheckBox) findViewById(R.id.cb_anonymous);


        tv_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == groupWin) {
                    View parentView = HelpWendaDeployActivity.this.getWindow().getDecorView();
                    View contentView = LayoutInflater.from(HelpWendaDeployActivity.this).inflate(R.layout.help_jishi_deploy_group_pop, null);

                    groupWin = PopupWin.Builder.create(HelpWendaDeployActivity.this)
                            .setParentView(parentView)
                            .setContentView(contentView)
                            .build();

                    groupWin.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            groupWin.dismiss();
                        }
                    });

                    groupAdapter = new GroupAdapter(HelpWendaDeployActivity.this);
                    groupListView = (XListView) groupWin.findViewById(R.id.xlv_list);
                    groupListView.setAdapter(groupAdapter);

                    groupListView.setPullRefreshEnable(true);
                    groupListView.setPullLoadEnable(true);
                    groupListView.setAutoLoadEnable(true);

                    groupListView.setXListViewListener(new XListView.IXListViewListener() {
                        @Override
                        public void onRefresh() {
                            groupAdapter.clearData();
                            mCurrentStart = 0;
                            groupListView.setPullLoadEnable(true);
                            initGroupData();
                        }

                        @Override
                        public void onLoadMore() {
                            initGroupData();
                        }
                    });

                    groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Base04Response.Body.Region region = groupAdapter.getItem(position);
                            GroupAdapter.ViewHolder holder = (GroupAdapter.ViewHolder) view.getTag();
                            tv_group.setText(holder.region.getRegionname());
                            tv_group.setTag(holder.region.getRegionid());
                            groupWin.dismiss();
                        }
                    });

                    initGroupData();
                }
                groupWin.show();
            }
        });

        btn_wenda_help = (Button) findViewById(R.id.btn_wenda_help);
        btn_wenda_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkDeploy()){
                    return;
                }
                doDeploy();
            }
        });
    }


    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    try {
                        List<Base04Response.Body.Region> list = (List<Base04Response.Body.Region>) msg.obj;
                        groupAdapter.addData(list);
                        groupAdapter.notifyDataSetChanged();
                        mCurrentStart += list.size();
                        onLoad();
                        if (list.size() < GlobalConstant.PAGE_SIZE) {
                            groupListView.setPullLoadEnable(false);
                        }
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1: {
                    try {
                        showToast("获取圈子失败:" + msg.obj);
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 2: {
                    try {
                        showToast("发布即时帮助成功！");
                        finish();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 3: {
                    try {
                        showToast("发布失败:" + msg.obj);
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 11: {
                    List<Partjob28Response.Parttimejob> list = (List<Partjob28Response.Parttimejob>) msg.obj;
                    if (list.size() > 0) {
                        adapter.setList(list);
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.notifyDataSetInvalidated();
                    }
                    break;
                }
                case 12: {
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }


    private void onLoad() {
        groupListView.stopRefresh();
        groupListView.stopLoadMore();
        groupListView.setRefreshTime(DateUtil.formatDate(new Date(), "MM-dd HH:mm"));
    }

    private void initGroupData() {
        if (actionLock.tryLock()) {
            final Context context = this;
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final AzService azService = new AzService(context);
                    final Base04Request req = new Base04Request();
                    final Base04Request.Region region = req.new Region();
                    region.setStudentid(UserSubject.getStudentid());  //从UserSubject中获取studentId
                    region.setAreacode(GlobalConstant.gis.getAreacode());
                    region.setStart(String.valueOf(mCurrentStart));
                    region.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                    req.setRegion(region);
                    azService.doTrans(req, Base04Response.class, new AzService.Callback<Base04Response>() {
                        @Override
                        public void success(Base04Response response) {
                            ResponseResult result = response.getResult();
                            if ("0".equals(result.getCode())) {
                                Base04Response.Body partjob07Body = response.getBody();
                                List<Base04Response.Body.Region> regionList = partjob07Body.getRegion_list();
                                if (null == regionList) {
                                    regionList = new ArrayList<Base04Response.Body.Region>();
                                }
                                messageHandler.postMessage(0, regionList);
                            } else {
                                messageHandler.postMessage(1, result.getMessage());
                            }
                        }

                        @Override
                        public void fail(AzException e) {
                            messageHandler.postException(e);
                        }
                    });
                }
            });
        }
    }
    private boolean checkDeploy(){
        if(StringUtil.isEmpty(actv_topic.getText().toString())){
            showToast("请输入话题");
            return false;
        }
        if(StringUtil.isEmpty(et_content.getText().toString())){
            showToast("请输入求助内容");
            return false;
        }
        return true;
    }

    private void doDeploy(){
        if (!actionLock.tryLock()) {
            return;
        }
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Partjob17Request req = new Partjob17Request();
                Partjob17Request.Parttimejob job = req.new Parttimejob();
                job.setStudentid(UserSubject.getStudentid());
//                job.setTopicid(topicid);
//                job.setTopiccontent(topiccontent);
                job.setContent(et_content.getText().toString());
                job.setRegionid(tv_group.getTag().toString());
                job.setAnonflag(cb_anonymous.isChecked() ? "1" : "0");
                job.setGisx(GlobalConstant.getGis().getGisx());
                job.setGisy(GlobalConstant.getGis().getGisy());
                req.setParttimejob(job);
                new AzService().doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse resp) {
                        if ("0".equals(resp.getResultCode())) {
                            messageHandler.postMessage(2);
                        } else {
                            messageHandler.postMessage(3, resp.getResultMessage());
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        });
    }
}
