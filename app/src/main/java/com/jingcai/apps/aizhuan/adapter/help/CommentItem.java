package com.jingcai.apps.aizhuan.adapter.help;

/**
 * Created by lejing on 15/8/4.
 */
public abstract class CommentItem {
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public abstract String getContentid();

    public abstract String getContent();

    public abstract String getOptime();

    public abstract String getPraisecount();

    public abstract String getPraiseflag();

    public abstract String getPraiseid();

    public abstract String getSourcecollege();

    public abstract String getSourceid();

    public abstract String getSourceimgurl();

    public abstract String getSourcelevel();

    public abstract String getSourcename();

    public abstract String getSourceschool();

    public abstract String getRefname();

    public abstract String getRefcontent();

    public abstract void setPraiseflag(String praiseflag);

    public abstract void setPraiseid(String praiseid);

    public abstract void setPraisecount(String praisecount);
}
