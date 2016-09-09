package com.feadre.xfrag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 类描述：仿造Activity 栈 支持4种模式
 * 特点:只要集合中有 Fragment 最近打开的一定在栈顶
 * 创建人：Feadre zhouke
 * 创建时间：2016/9/1 22:07
 */
public class ListTree<T> {

    private List<List<T>> mListTree = new ArrayList<List<T>>();

    public int sizeCount() {
        int size = 0;
        Iterator<List<T>> iterator = mListTree.iterator();
        while (iterator.hasNext()) {
            List<T> next = iterator.next();
            Iterator<T> nIterator = next.iterator();
            while (nIterator.hasNext()) {
                nIterator.next();
                size++;
            }
        }
        return size;
    }

    public void putStandard(T fragment) {
        checkListTree();
        List<T> lastStack = getLastStack();
        lastStack.add(fragment);
    }

    /**
     * 如果顶部有则不处理 没在顶部则添加
     */
    public void putSingleTop(T fragment) {
        checkListTree();
        List<T> lastStack = getLastStack();
        if (lastStack.isEmpty()) {
            lastStack.add(fragment);
        } else {
            if (!compareFragment(fragment, lastStack.get(lastStack.size() - 1))) {
                lastStack.add(fragment);
            }
        }
    }

    public void putSingleInstance(T t) {
        List<T> stack = new ArrayList<>();
        stack.add(t);
        mListTree.add(stack);
    }

    /**
     * 如果当前栈中有数据 把他找出来 清楚上面他上面的 并记录返回他上面的数据 用于FM清除
     *
     * @param t
     * @param lock 如果有锁定的 不能移除
     * @return
     */
    public List<T> putSingleTask(T t, List<T> lock) {
        checkListTree();
        List<T> lastStack = getLastStack();
        List<T> removeFragments = new ArrayList<T>();

        if (lastStack.isEmpty()) {
            lastStack.add(t);
        } else {
            boolean isClear = false;
            //与最后一个栈的元素比较
            for (int i = 0; i < lastStack.size(); i++) {
                if (isClear) {//已经在栈下面找到 现在是移除
                    T temp = lastStack.get(i);
                    if (lock != null && !lock.isEmpty() && lock.contains(temp)) {//上面有锁定的 将锁定移到栈底部
                        lastStack.remove(temp);
                        lastStack.add(0, temp);
                    } else {
                        removeFragments.add(temp);
                        lastStack.remove(i);
                    }
                } else {
                    if (compareFragment(lastStack.get(i), t)) {
                        isClear = true;
                    }
                }
            }
        }
        return removeFragments;
    }


    /**
     * 比较完整类名
     */
    private boolean compareFragment(T frag1, T frag2) {
        return frag1.getClass().getName().equals(frag2.getClass().getName());
    }

    private void checkListTree() {
        if (mListTree.isEmpty()) {
            mListTree.add(new ArrayList<T>());
        }
    }

    private List<T> getLastStack() {
        return mListTree.get(mListTree.size() - 1);
    }

    /**
     * 取出最后两个fragment
     *
     * @return fragArr[0] 是顶 [1] 是倒二
     */
    public T[] get2Last(T[] ts, List<T> ex) {

        if (mListTree.isEmpty()) {
            return ts;
        }

        //mStackList至少有一个栈
        for (int i = mListTree.size() - 1; i >= 0; i--) {
            List<T> stack = mListTree.get(i);
            if (stack == null || stack.isEmpty()) {
                mListTree.remove(i);
            } else {
                //stack至少有一个 倒序
                for (int j = stack.size() - 1; j >= 0; j--) {
                    T t = stack.get(j);
                    //排除锁定的
                    if (ex != null && !ex.isEmpty() && ex.contains(t)) {
                        continue;
                    }
                    if (ts[0] == null) {
                        ts[0] = stack.get(j);//hide
                    } else {
                        ts[1] = stack.get(j);
                        break;
                    }
                }
            }
        }
        return ts;
    }

    public T[] get2Last(T[] ts) {
        return get2Last(ts, null);
    }

    /**
     * 拿出当前顶部的
     *
     * @return 如果没有返回空
     */
    public T getCurrentTop() {
        if (mListTree.isEmpty()) {
            return null;
        }
        T last = null;
        List<T> lastList = mListTree.get(mListTree.size() - 1);
        if (!lastList.isEmpty()) {
            last = lastList.get(lastList.size() - 1);
        }
        return last;
    }

    /**
     * 移除最后一个栈顶的
     */
    public void removeLast() {
        // stack.remove(j);
        int i = mListTree.size() - 1;
        if (i >= 0) {
            List<T> lastStack = mListTree.get(i);
            if (lastStack != null && (!lastStack.isEmpty())) {
                lastStack.remove(lastStack.size() - 1);
                //删除后 为空 就从移除他
                if (lastStack.isEmpty()) {
                    mListTree.remove(lastStack);
                }
            } else {
                mListTree.remove(lastStack);
            }
        }
    }

    /**
     * 倒序移除
     *
     * @return
     */
    public boolean remove(T t) {
        for (int i = mListTree.size() - 1; i >= 0; i--) {
            List<T> ts = mListTree.get(i);
            for (int j = ts.size() - 1; j >= 0; j--) {
                if (ts.get(j) == t) {
                    ts.remove(j);
                    if (ts.isEmpty()) {
                        mListTree.remove(i);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains(T t) {
        Iterator<List<T>> sIterator = mListTree.iterator();
        while (sIterator.hasNext()) {
            List<T> next = sIterator.next();
            Iterator<T> nIterator = next.iterator();
            while (nIterator.hasNext()) {
                if (nIterator.next() == t) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addStack(List<T> stack) {
        mListTree.add(stack);
    }

    public void clear() {
        mListTree.clear();
    }

    /**
     * 重建不要null数据
     *
     * @param list
     */
    public void rebuild(List<T> list) {
        clear();
        List<T> tList = new ArrayList<>();
        for (T t : list) {
            if (t != null) {
                tList.add(t);
            }
        }
        if (!tList.isEmpty()) {
            mListTree.add(tList);
        }
    }
}
