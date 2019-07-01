package com.hfjs.kotlin_mapuse.test;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class TestBeanClass implements MultiItemEntity {
    public static final int TEXT = 1;
    public static final int IMG = 2;
    private int itemType;
    private Class clazz;
    private String name;

    public TestBeanClass(int itemType) {
        this.itemType = itemType;
    }

    public TestBeanClass(String name) {
        this.name = name;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}