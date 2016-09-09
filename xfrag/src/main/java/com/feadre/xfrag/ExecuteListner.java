package com.feadre.xfrag;

import com.feadre.xfrag.request.Request;

/**
 * 类描述：实现这个类 运行listner 即可
 *  只能侦听 XFrag 管理的打开关闭
 * 创建人：Feadre zhouke
 * 创建时间：2016/9/4 17:06
 */
public interface ExecuteListner {

    //操作前
    void onExecutePre(Request request);

    //操作完成 会在动画前
    void onExecuteComplete(Request request);

}
