package com.banlap.llmusic.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * @author Banlap on 2021/11/30
 */
public abstract class BaseActivity<VM extends ViewModel, VDB extends ViewDataBinding> extends AppCompatActivity {

    protected VM mViewModel;
    protected VDB mViewDataBinding;

    public VM getViewModel() { return mViewModel; }
    public VDB getViewDataBinding() { return mViewDataBinding; }

    @LayoutRes
    protected abstract int getLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        mViewDataBinding.setLifecycleOwner(this);
        if(savedInstanceState!=null) {
            String data = savedInstanceState.getString("SIS");
            Toast.makeText(this, "SIS: " + data, Toast.LENGTH_SHORT).show();
        }
        init();
        initData();
        initView();
    }

    @SuppressWarnings("unchecked")
    protected void init() {
        Class<VM> vmClass = (Class<VM>)((ParameterizedType)(Objects.requireNonNull(this.getClass().getGenericSuperclass()))).getActualTypeArguments()[0];
        mViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(vmClass);
    }

    protected abstract void initData();
    protected abstract void initView();
}
