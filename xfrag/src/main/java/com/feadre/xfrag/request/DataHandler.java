package com.feadre.xfrag.request;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.feadre.xfrag.ListTree;
import com.feadre.xfrag.LogUtils;

import java.util.List;

/**
 * 类描述：第三层
 * 特点:
 * 创建人：Feadre zhouke
 * QQ:318740003
 * 创建时间：2016/9/7 11:51
 * 版本号:
 * 更新时间:
 */
public class DataHandler extends BaseRequestHandler {
    private final FragmentManager mFM;
    private ListTree<Fragment> mListTree;
    private List<Fragment>     mLockList; //保存lock锁定不能关闭的Fragment

    public DataHandler(FragmentManager fm, ListTree<Fragment> listTree, List<Fragment> lockList) {
        mListTree = listTree;
        mLockList = lockList;
        mFM = fm;
    }

    @Override
    public boolean execute(Request request) {
        if (request.bundle != null) {//将数据置入 这时刚构造在oncreate前
            //复用Fragment有可能会出错
            try {
                request.open.setArguments(request.bundle);
            } catch (Exception e) {
                LogUtils.e("Fragment已启动 无法传输数据");
                e.printStackTrace();
            }
        }

        if (request.open != null) {
            pushStack(request.open, request.stackMode);
        }

        return true;
    }

    private void pushStack(Fragment open, int stackMode) {
        switch (stackMode) {
            case Request.STANDARD:
                mListTree.putStandard(open);
                break;

            case Request.SINGLE_TOP:
                mListTree.putSingleTop(open);
                break;

            case Request.SINGLE_TASK:
                List<Fragment> removes = mListTree.putSingleTask(open, mLockList);
                for (Fragment rf : removes) {
                    mFM.beginTransaction().remove(rf).commit();
                }
                break;

            case Request.SINGLE_INSTANCE:
                mListTree.putSingleInstance(open);
                break;
        }
    }
}
