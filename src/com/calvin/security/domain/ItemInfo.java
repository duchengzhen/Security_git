package com.calvin.security.domain;

/**
 * Created by calvin on 2014/6/16.
 */
public class ItemInfo {
    /**
     * 栏目对应ID
     *  */
    public Integer id;
    /**
     * 栏目对应NAME
     *  */
    public String name;
    /**
     * 栏目在整体中的排序顺序  rank
     *  */
    public Integer orderId;
    /**
     * 栏目是否选中
     *  */
    public Integer selected;
    /**
     * 条目对应图标的id
     */
    public Integer iconId;

    public ItemInfo() {
    }

    public ItemInfo(int id, String name, int orderId,int selected,int iconId) {
        this.id = Integer.valueOf(id);
        this.iconId=Integer.valueOf(iconId);
        this.name = name;
        this.orderId = Integer.valueOf(orderId);
        this.selected = Integer.valueOf(selected);
    }

    public int getId() {
        return this.id.intValue();
    }

    public String getName() {
        return this.name;
    }

    public int getOrderId() {
        return this.orderId.intValue();
    }

    public Integer getSelected() {
        return this.selected;
    }

    public void setId(int paramInt) {
        this.id = Integer.valueOf(paramInt);
    }

    public void setName(String paramString) {
        this.name = paramString;
    }

    public void setOrderId(int paramInt) {
        this.orderId = Integer.valueOf(paramInt);
    }

    public void setSelected(Integer paramInteger) {
        this.selected = paramInteger;
    }

    public String toString() {
        return "ItemInfo [id=" + this.id + ", name=" + this.name
                + ", selected=" + this.selected + "]";
    }
}
