package com.feadre.xfrag;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.feadre.xfrag.request.BaseRequestHandler;
import com.feadre.xfrag.request.BasicCheck;
import com.feadre.xfrag.request.DataCheck;
import com.feadre.xfrag.request.DataHandler;
import com.feadre.xfrag.request.Request;
import com.feadre.xfrag.request.RequestExecute;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：Feadre zhouke
 * 创建时间：2016/9/4 8:22
 */
public class XFragmentManager {
    private FragmentManager mFM;
    private RequestExecute  mRequestExecute;

    ListTree<Fragment> mListTree;
    List<Fragment>     mLockList; //保存lock锁定不能关闭的Fragment 检测有lock属性时才初始化

    private long lastClickTime;
    private int initCount = 0;

    public XFragmentManager() {
        mListTree = new ListTree<>();
        mLockList = new ArrayList<>();
    }

    /**
     * 入口
     *
     * @param request
     * @return 返回false 没有执行  返回true 执行全部成功 返回值暂时没用
     */
    public boolean startCheck(Request request) {

        if (request.type == Request.CLICK || request.type == Request.BACK) {
            if (isFastClick(request)) {
                return false;//返回失败
            }
        }

        BaseRequestHandler handler = new BasicCheck(); //基本检测
        BaseRequestHandler dataCheck = new DataCheck(mListTree, mLockList);//目标有效性
        BaseRequestHandler dataHandler = new DataHandler(mFM, mListTree, mLockList);//数据处理
        //执行
        handler.setNext(dataCheck);
        dataCheck.setNext(dataHandler);
        dataHandler.setNext(mRequestExecute);

        return handler.dispatch(request);
    }

    public void initOpen(Request request) {
        //没有初始化才打开
        if (isFirst(request)) {
            startCheck(request);
        }
    }

    /**
     * activity不匹配 这是最后一个Fragment 由其它处理
     * 拦截返回键
     *
     * @param request @return 返回false允许其它处理 返回true拦截
     */
    public boolean onBackPressed(Request request) {
        //这里一直点很快有可能关闭不了BUG 特别是在动画很
        //只有损失 一点点性能 先判断close==null 返回
        //这个方法 是否需要同步锁
//        if (!isFastClick(request)) {
//            return true;
//        }

        //如果系统栈中有 说明有可能其它什么地方使用了FM
        //返回时先返回其它的
        if (mFM.getBackStackEntryCount() > 0) {
            mFM.popBackStack();
            return true;
        }

        //返回时先返回其它的
        List<Fragment> fmFrags = mFM.getFragments();
        int fmSize = fmFrags.size();
        int stackSize = mListTree.sizeCount();

        //没有数据时 由activity自己处理
        if (fmSize == 0 && stackSize == 0) {
            return false;
        }

        if (stackSize > fmSize) {
            LogUtils.e("栈数据异常");
            //stack重建 不要null
            mListTree.rebuild(fmFrags);
        }

        if (stackSize < fmSize) {
            //倒序遍历
            int n = 0;
            for (int i = fmSize - 1; i >= 0; i--) {
                Fragment fragment = fmFrags.get(i);
                if (fragment == null) {
                    //系统集合中可能会存在null数据 跳过如popBackStack 弹出后 集合中 仍有null数据
                    n++;//很有可能就是因为这个才不同步
                } else {//关闭由外界打开的 如果直接使用FM打开(比如由其它类库打开) 这里条件成立 先关闭其它的
                    //调用MF关闭
                    mFM.beginTransaction().remove(fragment).commit();
                    return true;
                }
                //如果非除null 尺寸相等 说明数据是同步的 就不需要再判断了
                if (stackSize + n == fmSize) {
                    //按照正常回退
                    break;
                }
            }
        }

        //这个方法
        Fragment[] last2 = mListTree.get2Last(new Fragment[2], mLockList);

        Fragment close = last2[0];//顶
        Fragment show = last2[1];//倒二

        if (close == null) {
            return false;
        }

        //最后一个由activity处理
//        if (show == null) {
//            return false;
//        } else {
        request.close = close;
        request.show = show;

//        passClickCheck();

        //至少有一个关闭的对象
        //返回false允许其它处理 返回true拦截
        startCheck(request);
        return true;
    }

//    private void passClickCheck() {
//        lastClickTime = lastClickTime - mClickSpace - 1;//跳过间隔检测
//    }

    /**
     * @param request
     * @return 返回true通过
     */
    private boolean isFastClick(Request request) {
        //这个时间点击无效
        if (System.currentTimeMillis() - lastClickTime > request.animTime) {
            //点击时间减上一次大于  mClickSpace
            //赋值
            lastClickTime = System.currentTimeMillis();
        } else {
            LogUtils.d("亲点击太快了");
            return true;
        }
        return false;
    }

    public boolean unlock(Fragment target) {
        boolean b = false;
        if (mLockList != null && !mLockList.isEmpty()) {
            b = mLockList.remove(target);
        }
        return b;
    }

    public void addListner(ExecuteListner listner) {
        mRequestExecute.addListners(listner);
    }

    public void removeListner(ExecuteListner listner) {
        mRequestExecute.removListners(listner);
    }

    /**
     * 定义在activity开始时显示的Fragment
     * 每次activity重建时init会调用
     * FM会更换,对应里面的FM的指针会更换 设置setRetainInstance(true)
     * 但信息不会丢失 如果FM有数据
     * 则重建stack---如果有多个栈的情况下 这时栈数据结构会丢失
     * FM恢复后 所有Fragment会全部显示 隐藏状态会清除
     * 需要在Activity中保存隐藏状态
     *
     * @param defFragment
     * @param request
     * @return
     */
    public void init(@NonNull FragmentManager fm,
                     Class<? extends Fragment> defFragment, Request request
    ) {
        setFM(fm);
        initCount = 0;//重置 在旋转时 需手动重置
        if (request.fragLayoutId != 0 && defFragment != null) {
            //没有初始化 才打开
            if (isFirst(request)) {
                try {
                    Fragment fragment = defFragment.newInstance();
                    if (fragment != null) {
                        request.open = fragment;
                        startCheck(request);
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 是否已经初始化 在初始化时使用
     * 旋转重建是 Activity一定会销毁 xfrag 不会销毁
     * 系统杀死 Activity 和xfrag 都会销毁
     * setRetainInstance(true);//重建时不会彻底销毁Fragment
     *
     * @param request
     * @return true 第一次打开 需要显示  false 这是在重建 无需显示
     */
    public boolean isFirst(Request request) { //名称并不合适
        //request 判断用于扩展
//        if (request.type == Request.XFrag) {
//
//        }
//        if (request.type = Request.ORDER) {
//
//        }

        List<Fragment> fmFragments = mFM.getFragments();
//        if (request.type != Request.ORDER) {
        if (fmFragments == null || fmFragments.isEmpty()) {
            //fm里没有数据 是第一次创建 需要显示
            initCount++;
            return true;
        }
//        }

        if (fmFragments.size() == initCount) {
            return true;
        }
        //fm里有数据 系统杀死mListTree数据消失 旋转有数据 根据数据重建stack
        if (mListTree.sizeCount() == 0) {
            //如果已经重现 尺寸大于0不进行操作
            mListTree.rebuild(fmFragments);
        }
        return false;
    }

    public void setFM(FragmentManager fm) {
        mFM = fm;
        mRequestExecute = new RequestExecute(mFM);
    }

    public static final String XFRAG_HIDE_STATE = "xfrag_hide";
    public static final String XFRAG_LOCK_STATE = "xfrag_lock";

    public void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            boolean[] hideState = savedInstanceState.getBooleanArray(XFRAG_HIDE_STATE);
            int[] lockIndex = savedInstanceState.getIntArray(XFRAG_LOCK_STATE);
            if (hideState == null) {
                return;
            }

            List<Fragment> fragments = mFM.getFragments();
            if (fragments == null || fragments.isEmpty()) {
                return;
            }

            int n = 0;
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment = fragments.get(i);
                if (fragment == null) {
                    continue;
                }

                //索引对应lockIndex 数组
                if (lockIndex != null && n < lockIndex.length && lockIndex[n] == i + 1000) {
                    if (mLockList == null) {
                        mLockList = new ArrayList<>();
                    }
                    mLockList.add(fragment);
                    n++;
                }

                //根据状态隐藏
                if (hideState[i]) {
                    mFM.beginTransaction().hide(fragments.get(i)).commit();
                }
            }
        }

    }


    public void saveState(Bundle outState) {
        //保存隐藏
        List<Fragment> fragments = mFM.getFragments();
        if (fragments != null && !fragments.isEmpty()) {
            boolean hideState[] = new boolean[fragments.size()];
            int[] lockState = null;
            int n = 0;//lockState 数组索引
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment = fragments.get(i);
                if (fragment == null) {
                    continue;
                }

                //记录每一个 有效fragment 的隐藏状态
                hideState[i] = fragment.isHidden();
                //使用mFM中有效的Fragment与mLockList 比对如果是锁定则保存集合索引
                if (mLockList == null || mLockList.isEmpty()) {
                    continue;
                }

                if (lockState == null) {
                    lockState = new int[mLockList.size()];
                }

                if (mLockList.contains(fragment)) {
                    //解决初始为0的冲突
                    lockState[n] = i + 1000;
                    n++;
                }
            }

            //记录隐藏
            outState.putBooleanArray(XFRAG_HIDE_STATE, hideState);
            //记录锁定
            if (lockState != null && lockState[0] != 0) {
                outState.putIntArray(XFRAG_LOCK_STATE, lockState);
            }
        }
    }

    public void onDestroy() {
        mFM = null;
        mLockList = null;
        mListTree = null;
    }
}
