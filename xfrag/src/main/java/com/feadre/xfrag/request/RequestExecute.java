package com.feadre.xfrag.request;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.feadre.xfrag.ExecuteListner;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：Feadre zhouke
 * 创建时间：2016/9/3 23:25
 */
public class RequestExecute extends BaseRequestHandler {
    private List<ExecuteListner> mListners;
    private FragmentManager      mFM;

    public RequestExecute(FragmentManager FM) {
        mFM = FM;
    }

    @Override
    public boolean execute(final Request request) {
        sendExecutePre(request);

        FragmentTransaction transaction = mFM.beginTransaction();

        transaction = transaction.setCustomAnimations(request.addIn, request.addOut, request.quitIn, request.addOut);

        if (request.open != null) {
            transaction = transaction.add(request.fragLayoutId, request.open, request.open.getClass().getName());
        }

        if (request.hide != null) {
            transaction = transaction.hide(request.hide);
        }

        //关闭动画必须先动画再关闭  这里逻辑有问题
        if (request.close != null) {
            transaction = transaction.remove(request.close);
        }

        if (request.show != null) {
            transaction = transaction.show(request.show);
        }

        transaction.commit();
        sendExecuteComplete(request);
        return true;
    }

    public void addListners(ExecuteListner listner) {
        if (mListners == null) {
            mListners = new ArrayList<ExecuteListner>();
        }
        mListners.add(listner);
    }

    public void removListners(ExecuteListner listner) {
        if (mListners != null && !mListners.isEmpty()) {
            mListners.remove(listner);
        }
    }

    private void sendExecutePre(Request request) {
        if (mListners != null && !mListners.isEmpty()) {
            for (ExecuteListner listner : mListners) {
                listner.onExecutePre(request);
            }
        }
    }

    private void sendExecuteComplete(Request request) {
        if (mListners != null && !mListners.isEmpty()) {
            for (ExecuteListner listner : mListners) {
                listner.onExecuteComplete(request);
            }
        }
    }
}
