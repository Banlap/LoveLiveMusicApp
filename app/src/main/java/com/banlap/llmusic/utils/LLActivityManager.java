package com.banlap.llmusic.utils;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;

import com.banlap.llmusic.base.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.ListIterator;
import java.util.Stack;

/**
 * 页面管理类
 * */
public class LLActivityManager {

    private Stack<WeakReference<BaseActivity<? extends ViewModel, ? extends ViewDataBinding>>> mStack;

    private LLActivityManager() {
        mStack = new Stack<>();
    }

    //单例静态内部类
    private static final class SingletonHolder {
        private static final LLActivityManager INSTANCE = new LLActivityManager();
    }

    public static LLActivityManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 添加activity
     * @param activity act
     */
    public void addActivity(BaseActivity<? extends ViewModel, ? extends ViewDataBinding> activity) {
        WeakReference<BaseActivity<? extends ViewModel, ? extends ViewDataBinding>> weakReference = new WeakReference<>(activity);
        mStack.add(weakReference);
    }

    /**
     * activity出栈
     * 同时结束指定类名的activity
     *
     * @param activity act
     */
    public void removeActivity(BaseActivity<? extends ViewModel, ? extends ViewDataBinding> activity) {
        if (mStack.empty()) {
            activity.finish();
            return;
        }

        listIteratorNext(activity, null);
    }

    /**
     * activity出栈
     * 同时结束指定类名的activity
     *
     * @param cls 类对象
     */
    public void removeActivity(Class<?> cls) {
        if (mStack.empty()) {
            return;
        }

        listIteratorNext(null, cls);
    }

    /**
     * 迭代查找
     * */
    private void listIteratorNext(BaseActivity<? extends ViewModel, ? extends ViewDataBinding> activity,
                                  Class<?> cls) {
        ListIterator<WeakReference< BaseActivity<? extends ViewModel, ? extends ViewDataBinding>>> iterator = mStack.listIterator();
        while (iterator.hasNext()) {
            BaseActivity<? extends ViewModel, ? extends ViewDataBinding> act = iterator.next().get();
            if (act == null) {
                iterator.remove();
                continue;
            }

            //
            if(activity != null) {
                if (act == activity) {
                    activity.finish();
                    iterator.remove();
                }
            } else if(cls != null){
                if (act.getClass() == cls) {
                    act.finish();
                    iterator.remove();
                }
            }

        }
    }

    /**
     * 获取栈顶页面
     */
    public BaseActivity<? extends ViewModel, ? extends ViewDataBinding> getTopActivity() {
        if (mStack.empty()) {
            return null;
        }
        WeakReference<BaseActivity<? extends ViewModel, ? extends ViewDataBinding>> peek = mStack.peek();
        return peek.get();
    }


    /**
     * 判断栈里面是否存在指定的activity
     *
     * @param cls 指定activity对象
     * @return true：存在；false：不存在
     */
    public boolean existActivity(Class<?> cls) {
        if (mStack.empty()) {
            return false;
        }
        ListIterator<WeakReference<BaseActivity<? extends ViewModel, ? extends ViewDataBinding>>> iterator = mStack.listIterator();
        while (iterator.hasNext()) {
            BaseActivity<? extends ViewModel, ? extends ViewDataBinding> activity = iterator.next().get();
            if (activity == null) {
                iterator.remove();
                continue;
            }
            if (activity.getClass() == cls) {
                return true;
            }
        }
        return false;
    }
}
