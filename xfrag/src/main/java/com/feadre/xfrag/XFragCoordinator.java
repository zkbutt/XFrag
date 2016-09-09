package com.feadre.xfrag;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.feadre.xfrag.request.Request;


/**
 * 类描述：
 * 特点:
 * 创建人：Feadre zhouke
 * QQ:318740003
 * 创建时间：2016/9/6 10:18
 * 版本号:
 * 更新时间:
 */
public class XFragCoordinator {
    private Request mRequest; //在构造时创建
    private Request bak; //rebak时创建

    XFragmentManager mManager;//在构造时创建
//    WeakReference<FragmentActivity> activityRef;

    //加一个
    private FragmentActivity mActivity;//重建会重新赋值，不会泄漏  当销毁后没有再启动XFrag时 mActivity 泄漏

    /*-------两栖方法   即可对外直接调用 又可链式调用-------*/
    public XFragCoordinator addListner(ExecuteListner listner) {
        mManager.addListner(listner);
        return this;
    }

    public XFragCoordinator removeListner(ExecuteListner listner) {
        mManager.removeListner(listner);
        return this;
    }

    public XFragCoordinator open(Fragment open) {
        mRequest.open = open;
        return this;
    }

    //这两个重载是为了避免某些地方 写入语句却没有执行最后 导致请求异常
    //保证链式开始一定是默认请求
    XFragCoordinator open(Fragment open, boolean b) {
        if (b) {
            restoreQuest();
        }
        return open(open);
    }

    XFragCoordinator close(Fragment close, boolean b) {
        if (b) {
            restoreQuest();
        }
        return close(close);
    }

    public XFragCoordinator close(Fragment close) {
        mRequest.close = close;
        return this;
    }

    /*-------链式方法-------*/
    public XFragCoordinator layout(@IdRes int fragLayoutId) {
        mRequest.fragLayoutId = fragLayoutId;
        return this;
    }

    public XFragCoordinator hide(Fragment from) {
        mRequest.hide = from;
        return this;
    }

    public XFragCoordinator mode(int stackMode) {
        mRequest.stackMode = stackMode;
        return this;
    }

    public XFragCoordinator lock(boolean b) {
        if (b) {
            mRequest.lock = Request.LOCK;
        } else {
            mRequest.lock = Request.UNLOCK;
        }
        return this;
    }

    public XFragCoordinator bundle(Bundle bundle) {
        mRequest.bundle = bundle;
        return this;
    }

    /**
     * 设置一次性动画 动画时间超过500必须设置
     */
    public XFragCoordinator anim(@AnimRes int addIn,
                                 @AnimRes int addOut,
                                 @AnimRes int quitIn,
                                 @AnimRes int quitOut,
                                 int animTime) {
        mRequest.animTime = animTime;
        return anim(addIn, addOut, quitIn, quitOut);
    }

    public XFragCoordinator anim(@AnimRes int addIn,
                                 @AnimRes int addOut,
                                 @AnimRes int quitIn,
                                 @AnimRes int quitOut) {
        mRequest.addIn = addIn;
        mRequest.addOut = addOut;
        mRequest.quitIn = quitIn;
        mRequest.quitOut = quitOut;
        return this;
    }

    /*----链式执行方法----*/
    public void execute() {
        mManager.startCheck(mRequest);
        restoreQuest();
    }

    public void initOpen() {
        mRequest.type = Request.ORDER;
        lock(true);
        mManager.initOpen(mRequest);
        restoreQuest();
    }

    void setAnim(boolean b) {
        if (b) {
            mRequest.addIn = R.anim.left_in;
            mRequest.addOut = R.anim.right_out;
            mRequest.quitIn = R.anim.right_in;
            mRequest.quitOut = R.anim.left_out;
            //设置退出动画 //退出动画每次都要初始化
            rebak();
        } else {
            mRequest.quitIn = 0;
            mRequest.addIn = 0;
            mRequest.addOut = 0;
            mRequest.quitOut = 0;
            rebak();
        }
    }

    /**
     * 设置永久动画
     */
    void setAnim(@AnimRes int addIn,
                 @AnimRes int addOut,
                 @AnimRes int quitOut,
                 @AnimRes int quitIn,
                 int animTime) {
        mRequest.animTime = animTime;
        mRequest.addIn = addIn;
        mRequest.addOut = addOut;
        mRequest.quitOut = quitOut;
        mRequest.quitIn = quitIn;
        rebak();
    }

    /*-------结束方法 调用结束链式-------*/
    /*-------对外方法-------*/

    /**
     * 这是第二种初始化方法
     *
     * @param activity
     */
    void initXFrag(FragmentActivity activity) {
        if (activity == null) {
            LogUtils.e("activity 不能为null ");
            return;
        }
        if (mActivity != null) {
            //已经初始化
            return;
        }
        mActivity = activity;
        mManager = new XFragmentManager();
        rebak();//将默认加入备份还原点 不能在setDefFragment后面
    }

    void initXFragActivity(FragmentActivity activity,
                           @IdRes int defId,
                           Class<? extends Fragment> defFragment) {
        if (activity == null) {
            return;
        }
        mActivity = activity;
        mManager = new XFragmentManager();
        //必须defId和defFragment都有效设置才生效
        if (defId != 0 && defFragment != null) {
            layout(defId);
            rebak();//将默认加入备份还原点 不能在setDefFragment后面
            lock(true);//通常默认显示的需要锁定
            mRequest.type = Request.XFrag;
            mManager.init(mActivity.getSupportFragmentManager(), defFragment, mRequest);
            restoreQuest();
        }
    }

    boolean unlock(Fragment target) {
        return mManager.unlock(target);
    }

    boolean onBackPressed(Activity activity, int keyCode) {
        //不是指定的activity 交由他自己处理
        if (activity != mActivity) {
            return false;
        }
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return false;
        }
        mRequest.type = Request.BACK;
        boolean b = mManager.onBackPressed(mRequest);
        restoreQuest();
        return b;
    }

    void save(Bundle outState) {
        mManager.saveState(outState);
    }

    void restore(Bundle savedInstanceState) {
        mManager.restoreState(savedInstanceState);
    }

    void onDestroy(FragmentActivity activity) {
        //判断是不是初始化自己的那个View
        if (activity == mActivity) {
            mManager.onDestroy();
            mActivity = null;
            mCoordinator = null;
        }
    }

    /**
     * 备份还原 原型模式
     * 初始化备份
     * 设置默认时备份
     */
    private void rebak() {
        try {
            bak = (Request) mRequest.clone();//将默认id做备份
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 每次请求结束时恢复
     */
    private void restoreQuest() {
        try {
            mRequest = (Request) bak.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单例
     */
    private XFragCoordinator() {
        mManager = new XFragmentManager();
        mRequest = new Request();
        rebak();
    }

    private static XFragCoordinator mCoordinator;

    static XFragCoordinator getCoordinator() {
        if (mCoordinator == null) {
            synchronized (XFragCoordinator.class) {
                if (mCoordinator == null) {
                    mCoordinator = new XFragCoordinator();
                }
            }
        }
        return mCoordinator;
    }
}
