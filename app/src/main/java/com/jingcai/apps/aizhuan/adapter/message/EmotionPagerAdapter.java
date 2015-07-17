package com.jingcai.apps.aizhuan.adapter.message;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Spannable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.util.PixelUtil;
import com.jingcai.apps.aizhuan.util.SmileUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Json Ding on 2015/7/1.
 */
public class EmotionPagerAdapter extends PagerAdapter {
    private static final String TAG = "EmotionPagerAdpater";

    private static final int COLUME_COUNT = 7;
    private static final int MAX_ROW_COUNT = 3;

    private int mEmotionCount;  // 表情总数
    private int mPagerCount;
    private List<String> mFileNames;  //ee_1 的全名
    private List<Integer> mEmotionResIds;   //ee_ 的drawable id
    private List<Integer> mEmotionNumber;   //ee_ 后面的编号

    private Context mContext;
    private List<GridView> mGridList;

    private OnEmotionClickListener mOnEmotionClickListener;

    public EmotionPagerAdapter(Context cxt){
        mContext = cxt;
        mEmotionResIds = new ArrayList<>();

        initData();
    }

    public void setOnEmotionClickListener(OnEmotionClickListener onEmotionClickListener){
        mOnEmotionClickListener = onEmotionClickListener;
    }

    private void initData() {
        getEmotionCountAndFileNames();
        initResourceId();
        //根据表情总数、列数、最大行数获取页数
        if(mEmotionCount % (COLUME_COUNT * MAX_ROW_COUNT) == 0){
            mPagerCount = mEmotionCount / (COLUME_COUNT * MAX_ROW_COUNT);
        }else{
            mPagerCount = mEmotionCount / (COLUME_COUNT * MAX_ROW_COUNT) + 1;
        }
        mGridList = new ArrayList<>(mPagerCount);
    }

    /**
     * 根据文件名获取所有表情资源的id
     */
    private void initResourceId() {
        String packageName = mContext.getPackageName();
        for(String s : mFileNames){
            int resId = mContext.getResources().getIdentifier(s, "drawable", packageName);
            mEmotionResIds.add(resId);
        }
    }

    /**
     * 使用反射，获取SmileUtils中，所有ee_开头的属性，累加到eMotionCount中，将文件名加入mFileNames
     */
    private void getEmotionCountAndFileNames() {
        mFileNames = new ArrayList<>(mEmotionCount);
        mEmotionNumber = new ArrayList<>(mEmotionCount);

        Class<SmileUtils> smileUtilsClass = SmileUtils.class;
        for(Field f : smileUtilsClass.getDeclaredFields()){
            if (f.getName().startsWith("ee_")){
                mEmotionCount ++ ;
                mFileNames.add(f.getName());
            }
        }
        //排序成自然序列
        Collections.sort(mFileNames,new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                String lhsIndex =lhs.substring(lhs.indexOf("_")+1, lhs.length());
                String rhsIndex =rhs.substring(rhs.indexOf("_")+1, rhs.length());
                return Integer.parseInt(lhsIndex) - Integer.parseInt(rhsIndex);
            }
        });

        //将排序好的表情编号，加入列表
        int size = mFileNames.size();
        for (int i = 0; i < size; i++) {
            String s = mFileNames.get(i);
            String index = s.substring(s.indexOf("_") + 1, s.length());
            mEmotionNumber.add(Integer.parseInt(index));
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        GridView gridView = new GridView(mContext);
        gridView.setNumColumns(COLUME_COUNT);
        gridView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,AbsListView.LayoutParams.WRAP_CONTENT));
        int start = position * COLUME_COUNT * MAX_ROW_COUNT;
        int end = 0;
        //如果不是最后一页
        if(position != mPagerCount -1){
            end = start + COLUME_COUNT * MAX_ROW_COUNT ;
        }else{//最后一页，总数对页数取余
            end = start + (mEmotionCount % (COLUME_COUNT * MAX_ROW_COUNT) == 0 ? COLUME_COUNT * MAX_ROW_COUNT : mEmotionCount % (COLUME_COUNT * MAX_ROW_COUNT));
        }
        end--;
        if(start != 0){
            start --;
        }
        List<Integer> drawableList = mEmotionResIds.subList(start, end);
        drawableList = new ArrayList<>(drawableList);
        drawableList.add(R.drawable.icon_conversation_emotion_delete);  //加入删除按钮
        List<Integer> subNumberList = mEmotionNumber.subList(start,end);
        subNumberList = new ArrayList<>(subNumberList);
        subNumberList.add(-1);  //加入删除按钮下标
        gridView.setAdapter(new EmotionGridAdapter(mContext,subNumberList,drawableList));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value ="";
                if(id == -1){
                    mOnEmotionClickListener.onDeleteClick();
                    return;
                }
                try {
                    Field field = SmileUtils.class.getField("ee_" + id);
                    value = (String) field.get(null);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    Log.e(TAG,"SmileUtils property ee_"+id+" not find");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                mOnEmotionClickListener.onEmotionClick(SmileUtils.getSmiledText(mContext,value));
            }
        });
        mGridList.add(position,gridView);
        container.addView(gridView,gridView.getLayoutParams());
        return gridView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mGridList.get(position));
    }

    @Override
    public int getCount() {
        return mPagerCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 每一页GridView的adapter
     */
    private static class EmotionGridAdapter extends BaseAdapter{

        private Context mContext;
        private List<Integer> mDrawableIdList;
        private List<Integer> mEmotionNumber;

        public EmotionGridAdapter(Context ctx, List<Integer> emotionNumber, List<Integer> drawableList){
            mContext = ctx;
            mDrawableIdList = drawableList;
            mEmotionNumber = emotionNumber;
        }

        @Override
        public int getCount() {
            return mDrawableIdList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDrawableIdList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mEmotionNumber.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = new ImageView(mContext);
                convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            }
            ImageView view = (ImageView) convertView;
            int dp_10 = PixelUtil.dip2px(mContext, 10.0f);
            view.setPadding(dp_10, dp_10, dp_10, dp_10);
            view.setImageDrawable(mContext.getResources().getDrawable((mDrawableIdList.get(position))));
            return view;
        }
    }

    public interface OnEmotionClickListener{
        void onEmotionClick(Spannable spannable);
        void onDeleteClick();
    }
}
