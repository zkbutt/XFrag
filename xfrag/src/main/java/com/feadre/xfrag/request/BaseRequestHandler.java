package com.feadre.xfrag.request;

/**
 * 类描述：
 * 特点:
 * 创建人：Feadre zhouke
 * QQ:318740003
 * 创建时间：2016/9/6 23:44
 * 版本号:
 * 更新时间:
 */
public abstract class BaseRequestHandler {

    private BaseRequestHandler next;

    public BaseRequestHandler setNext(BaseRequestHandler next) {
        this.next = next;
        return next;
    }

    public BaseRequestHandler getNext() {
        return next;
    }

    /**
     * @param request
     * @return 返回true 通过检测
     */
    public boolean dispatch(Request request) {
        if (!execute(request)) {
            return false;
        }

        if (getNext() == null) {
            return true;
        }
        return getNext().dispatch(request);

    }

    /**
     * @param request
     * @return true 检测通过 下一个
     */
    public abstract boolean execute(Request request);

}
