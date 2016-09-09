package com.feadre.xfrag.request;

import android.support.v4.app.Fragment;

import com.feadre.xfrag.ListTree;
import com.feadre.xfrag.LogUtils;

import java.util.List;

/**
 * 类描述：目标有效性
 * 特点:
 * 创建人：Feadre zhouke
 * QQ:318740003
 * 创建时间：2016/9/7 11:51
 * 版本号:
 * 更新时间:
 */
public class DataCheck extends BaseRequestHandler {
    ListTree<Fragment> mListTree;
    List<Fragment>     mLockList; //保存lock锁定不能关闭的Fragment

    public DataCheck(ListTree<Fragment> listTree, List<Fragment> lockList) {
        mListTree = listTree;
        mLockList = lockList;
    }

    @Override
    public boolean execute(Request request) {
        if (request.lock == Request.LOCK) {
            if (mLockList != null) {
                if (request.open!=null)
                mLockList.add(request.open);
                if (request.close!=null)
                    mLockList.add(request.close);
                if (request.show != null)
                    mLockList.add(request.show);
                if (request.open!=null) {
                    mLockList.add(request.hide);
                }
            }
        }
        if (request.lock == Request.UNLOCK) {
            if (mLockList != null && !mLockList.isEmpty()) {
                mLockList.remove(request.open);
                mLockList.remove(request.close);
                mLockList.remove(request.hide);
                mLockList.remove(request.show);
            }
        }

        //先检查是否锁定 再关闭
        if (request.close != null && mLockList != null && mLockList.contains(request.close)) {
            LogUtils.e("不能关闭，已经锁定的Fragment，请先解锁！");
            request.close = null;
        }

        if (request.hide != null && mLockList != null && mLockList.contains(request.hide)) {
            LogUtils.e("不能隐藏，已经锁定的Fragment，请先解锁！");
            request.hide = null;
        }

        //检查是否包含
        if (request.close != null) {
            if (mListTree.contains(request.close)) {
                mListTree.remove(request.close);
            } else {
                LogUtils.e("关闭对象不存在");
                return false;
            }
        }

        if (request.hide != null) {
            if (!mListTree.contains(request.hide)) {
                LogUtils.e("隐藏对象不存在");
                return false;
            }
        }

        if (request.show != null) {
            if (!mListTree.contains(request.show)) {
                LogUtils.e("显示对象不存在");
                return false;
            }
        }
        return true;
    }
}
