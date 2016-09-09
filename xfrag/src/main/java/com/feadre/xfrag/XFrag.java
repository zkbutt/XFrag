package com.feadre.xfrag;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;


/**
 * 类描述：门面 在activity中 严禁使用FM手动添加
 * <p/>
 * setRetainInstance(true)  super.onCreate(savedInstanceState); activity重建时不销毁Fragment
 * 问题1:在没有使用open close时 设置了数据 数据会遗留到下一次
 * 创建人：Feadre zhouke
 * 创建时间：2016/9/4 8:19
 */
public class XFrag {
    /*----------------------初始化处理---------------------------*/
    public static void initXFrag(FragmentActivity activity) {
        XFragCoordinator.getCoordinator().initXFrag(activity);
    }

    //仅用于在主activity初始化
    static void initXFragActivity(FragmentActivity activity,
                                         @IdRes int defId,
                                         Class<? extends Fragment> defFragment) {
        XFragCoordinator.getCoordinator().initXFragActivity(activity, defId, defFragment);
    }

    /*----------------------链式入口---------------------------*/
    //在任意地方搞用open  开始链式调用  最后使用open
    //在其它Activity中使用有可能有异常  startCheck
    public static XFragCoordinator open(Fragment open) {
        XFragCoordinator coordinator = XFragCoordinator.getCoordinator();
        coordinator.open(open, true);
        return coordinator;
    }

    public static XFragCoordinator close(Fragment close) {
        XFragCoordinator coordinator = XFragCoordinator.getCoordinator();
        coordinator.close(close, true);
        return coordinator;
    }

    /*----------------------配置---------------------------*/
    public static void addListner(ExecuteListner listner) {
        XFragCoordinator coordinator = XFragCoordinator.getCoordinator();
        coordinator.addListner(listner);
    }

    public static void removeListner(ExecuteListner listner) {
        XFragCoordinator coordinator = XFragCoordinator.getCoordinator();
        coordinator.removeListner(listner);
    }

//    /**
//     * 设置自定义动画 一次性的
//     */
//    public static XFragCoordinator anim(@AnimRes int addIn,
//                                        @AnimRes int addOut,
//                                        @AnimRes int quitOut,
//                                        @AnimRes int quitIn) {
//        XFragCoordinator coordinator = XFragCoordinator.getCoordinator();
//        coordinator.anim(addIn, addOut, quitOut, quitIn);
//        return coordinator;
//    }

    /**
     * 该方法 只能单独使用
     * 设置永久动画  使用anim(false) 可以关闭 关闭后需重新设置
     */
    public static void setAnim(@AnimRes int addIn,
                               @AnimRes int addOut,
                               @AnimRes int quitOut,
                               @AnimRes int quitIn,
                               int animTime) {
        XFragCoordinator coordinator = XFragCoordinator.getCoordinator();
        coordinator.setAnim(addIn, addOut, quitOut, quitIn,animTime);
    }

    public static void setAnim(boolean b) {
        XFragCoordinator coordinator = XFragCoordinator.getCoordinator();
        coordinator.setAnim(b);
    }
    /**
     * 解开锁定
     *
     * @param target
     * @return true 解锁成功
     */
    public static boolean unlock(Fragment target) {
        XFragCoordinator coordinator = XFragCoordinator.getCoordinator();
        return coordinator.unlock(target);
    }

    /*----------------------生命周期处理---------------------------*/

    /**
     * 拦截返回键 详情见XFragActivity
     * switch (keyCode) {
     * case KeyEvent.KEYCODE_BACK:
     * 拦截返回键 先交由XFrag处理 如果返回false 则向下传递
     * if (XFrag.onBackPressed(this)) {
     * return true;
     * }
     * }
     *
     * @param activity
     * @param keyCode
     * @return true Xfrag已处理
     */
    public static boolean onBackPressed(Activity activity, int keyCode) {
        XFragCoordinator coordinator = XFragCoordinator.getCoordinator();
        return coordinator.onBackPressed(activity, keyCode);
    }

    public static void save(Bundle outState) {
        XFragCoordinator.getCoordinator().save(outState);
    }

    public static void restore(Bundle savedInstanceState) {
        XFragCoordinator.getCoordinator().restore(savedInstanceState);
    }

    public static void onDestroy(FragmentActivity activity) {
        XFragCoordinator.getCoordinator().onDestroy(activity);
    }

}
