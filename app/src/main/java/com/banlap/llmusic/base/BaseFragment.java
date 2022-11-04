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
import java.util.Objects;

public abstract class BaseFragment<VM extends ViewModel, VDB extends ViewDataBinding> extends Fragment {
    protected VM mViewModel;
    protected VDB mViewDataBinding;

    public VM getViewModel() { return mViewModel; }
    public VDB getViewDataBinding() { return mViewDataBinding; }

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

}
