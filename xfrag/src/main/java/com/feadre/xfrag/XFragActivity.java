package com.feadre.xfrag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

/**
 * 创建人：Feadre zhouke
 * 创建时间：2016/9/1 22:07
 */
public abstract class XFragActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XFrag.initXFragActivity(this, getFrameLayoutResID(), getDefFragment());
        onCreateNow(savedInstanceState);
//        Log.e("MainActivity", "onCreate");
    }

    //指定默认空间
    protected abstract int getFrameLayoutResID();

    //定义初始Fragment
    protected abstract Class<? extends Fragment> getDefFragment();

    public abstract void onCreateNow(Bundle savedInstanceState);

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //保存Fragment状态 用于恢复
        XFrag.save( outState);
//        Log.e("MainActivity", "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        XFrag.restore(savedInstanceState);
//        Log.e("MainActivity", "onRestoreInstanceState");
    }

    @Override
    public final boolean onKeyDown(int keyCode, KeyEvent event) {
        //拦截返回键 先交由XFrag处理 如果返回false 则向下传递
        if (XFrag.onBackPressed(this, keyCode)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        XFrag.onDestroy(this);
        super.onDestroy();
    }
}
