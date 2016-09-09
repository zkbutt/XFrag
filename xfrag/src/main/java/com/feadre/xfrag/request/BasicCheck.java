package com.feadre.xfrag.request;

import com.feadre.xfrag.LogUtils;

/**
 * 类描述：基本检测
 * 特点:
 * 创建人：Feadre zhouke
 * QQ:318740003
 * 创建时间：2016/9/7 0:00
 * 版本号:
 * 更新时间:
 */
public class BasicCheck extends BaseRequestHandler {
    @Override
    public boolean execute(Request request) {
        if (request == null) {
            LogUtils.e("request不能为null");
            return false;
        }

        if (request.open == null && request.close == null && request.hide == null && request.show == null) {
            LogUtils.e("open close hide show----目标fragment不能为空");
            return false;
        }

        if (request.open != null && request.fragLayoutId == 0) {
            LogUtils.e("Requst没有指定有效的fragLayoutId");
            return false;
        }
        return true;
    }
}
