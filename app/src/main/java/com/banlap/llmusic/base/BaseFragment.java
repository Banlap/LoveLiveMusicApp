package com.banlap.llmusic.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class BaseFragment<VM extends ViewModel, VDB extends ViewDataBinding> extends Fragment {
    protected VM mViewModel;
    protected VDB mViewDataBinding;
    //添加共享 ViewModel
    protected Map<Class<? extends ViewModel>, ViewModel> mViewModels = new HashMap<>();

    public VM getViewModel() { return mViewModel; }
    public VDB getViewDataBinding() { return mViewDataBinding; }

    /**
     * 获取共享ViewModel的方法
     * @apiNote 需要传入指定的class类才能获取viewModel内方法
     * */
    protected <SVM extends ViewModel> SVM getViewModel(@NonNull Class<SVM> modelClass) {
        if (!mViewModels.containsKey(modelClass)) {
            SVM viewModel = new ViewModelProvider(requireActivity()).get(modelClass);
            mViewModels.put(modelClass, viewModel);
        }
        return modelClass.cast(mViewModels.get(modelClass));
    }

    @LayoutRes
    protected abstract int getLayoutId();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mViewDataBinding.setLifecycleOwner(this);

        init();
        initData();
        initView();

        return mViewDataBinding.getRoot();
    }

    protected void init() {
        Class<VM> vmClass = (Class<VM>) ((ParameterizedType) Objects.requireNonNull(this.getClass().getGenericSuperclass())).getActualTypeArguments()[0];
        mViewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(vmClass);
    }

    protected abstract void initData();
    protected abstract void initView();

    /**
     * 判断是否双击
     * */
    public boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        if(Math.abs(currentTime - BaseActivity.lastTime) < BaseActivity.TIME_500MS) {
            BaseActivity.lastTime = currentTime;
            return true;
        }
        BaseActivity.lastTime = currentTime;
        return false;
    }

}
