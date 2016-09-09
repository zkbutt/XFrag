package com.feadre.xfrag.request;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;

/**
 * 类描述：原型模式
 * 创建人：Feadre zhouke
 * 创建时间：2016/9/3 22:52
 */
public class Request implements Cloneable {
    public static final int STANDARD        = 11;
    public static final int SINGLE_TOP      = 12;
    public static final int SINGLE_TASK     = 13;
    public static final int SINGLE_INSTANCE = 14;

    public static final int CLICK = 21;
    public static final int ORDER = 22;
    public static final int BACK  = 23;
    public static final int XFrag = 24;

    public static final int DEF    = 31;
    public static final int LOCK   = 32;
    public static final int UNLOCK = 33;

    @IntDef({DEF, LOCK, UNLOCK})
    public @interface LockStack {
    }

    @IntDef({CLICK, ORDER, BACK, XFrag})
    public @interface Type {
    }

    @IntDef({STANDARD, SINGLE_TOP, SINGLE_TASK, SINGLE_INSTANCE})
    public @interface StackMode {
    }

    @IdRes
    public int fragLayoutId = 0;//必须
    public Fragment hide;//隐藏
    public Fragment show;//关闭时显示
    public Fragment open;//必须的 打开
    public Fragment close;//必须的 关闭
    public Bundle   bundle;//null或有

    @LockStack
    public int lock      = DEF; //已默认
    @StackMode
    public int stackMode = STANDARD;//已默认
    @Type
    public int type      = CLICK;//已默认


// public    boolean isHide = true;//是否需要隐藏  关闭时是否显示

    public int addIn;//针对ADD的动画
    public int addOut;//隐藏时动画 成对生效
    public int quitOut;//针对 close 的动画 单个生效
    public int quitIn;//针对 close 的动画
    //    public Animation quitInAnim;
//    public Animation quitOutAnim;
    public int animTime = 500; //如果动画时间超过500必须重新指定 否则有可能出问题

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
