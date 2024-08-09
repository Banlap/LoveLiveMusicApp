package com.banlap.llmusic.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.databinding.ActivityDownloadBinding;
import com.banlap.llmusic.databinding.DialogDownloadMenuBinding;
import com.banlap.llmusic.databinding.ItemDownloadListBinding;
import com.banlap.llmusic.model.DownloadMusic;
import com.banlap.llmusic.receiver.DownloadReceiver;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.ui.ThemeHelper;
import com.banlap.llmusic.uivm.vm.DownloadVM;
import com.banlap.llmusic.utils.DownloadHelper;
import com.banlap.llmusic.utils.LLActivityManager;
import com.banlap.llmusic.utils.PxUtil;
import com.banlap.llmusic.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends BaseActivity<DownloadVM, ActivityDownloadBinding>
    implements DownloadVM.DownloadCallBack {

    private static final String TAG = DownloadActivity.class.getSimpleName();
    public List<DownloadMusic> downloadMusicList;
    public DownloadListAdapter downloadListAdapter;
    private int rThemeId =0;

    private boolean isDestroy = false;

    @Override
    protected int getLayoutId() { return R.layout.activity_download; }

    @Override
    protected void initData() {
        downloadMusicList = new ArrayList<>();

        List<DownloadMusic> splist = SPUtil.getListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, DownloadMusic.class);

        if(splist.size()>0) {
            downloadMusicList.addAll(splist);
        }

        String strThemeId = SPUtil.getStrValue(getApplicationContext(), SPUtil.SaveThemeId);
        if(!TextUtils.isEmpty(strThemeId)) {
            changeTheme(Integer.parseInt(strThemeId));
        }

        downloadMusicList.add(new DownloadMusic());
        downloadMusicList.add(new DownloadMusic());

    }

    /** 改变主题 */
    private void changeTheme(int rId) {
        rThemeId = rId;
        SPUtil.setStrValue(getApplicationContext(), SPUtil.SaveThemeId, String.valueOf(rId));
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_CHANGE_THEME));
        //主题变更
        ThemeHelper.getInstance().downloadActivityTheme(this, rId, getViewDataBinding());
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        getViewDataBinding().setVm(getViewModel());
        getViewModel().setCallBack(this);

        downloadListAdapter = new DownloadListAdapter(this, downloadMusicList);
        getViewDataBinding().rvMessageList.setLayoutManager(new LinearLayoutManager(this));
        getViewDataBinding().rvMessageList.setAdapter(downloadListAdapter);
        downloadListAdapter.notifyDataSetChanged();


        getViewDataBinding().llDownloadMain.setVisibility(View.GONE);
        getViewDataBinding().llDownloadNull.setVisibility(View.VISIBLE);

        getViewDataBinding().pbProgress.setMax(100);

        if(DownloadHelper.isDownloading()) {
            Log.e(TAG, "当前有下载任务");
            DownloadReceiver.startHandler();
        } else {
            Log.e(TAG, "当前没有下载任务");
        }

    }

    @Override
    public void viewBack() { finish(); }

    @Override
    public void refreshList() {
        List<DownloadMusic> splist = SPUtil.getListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, DownloadMusic.class);
        downloadMusicList.clear();
        downloadMusicList.addAll(splist);
        downloadListAdapter.notifyDataSetChanged();
    }


    public void showDownloadMenu(View view) {
        // Activity 已经销毁时不再给点击
        if(isFinishing()) { return; }
        if(isDestroy) { return; }
        if(isDoubleClick()) { return; }
        DialogDownloadMenuBinding dialogDownloadMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_download_menu, null, false);

        PopupWindow downloadMenuPopupWindow  = new PopupWindow(dialogDownloadMenuBinding.getRoot(),
                PxUtil.getInstance().dp2px(110, this),  WindowManager.LayoutParams.WRAP_CONTENT, true);
        downloadMenuPopupWindow.setTouchable(true);
        downloadMenuPopupWindow.setAnimationStyle(R.style.showPopupMenuAnimation);

        downloadMenuPopupWindow.setBackgroundDrawable(getDrawable(R.drawable.shape_button_menu_normal));

        dialogDownloadMenuBinding.tvCleanList.setTextColor(getColor(R.color.white));
        dialogDownloadMenuBinding.tvContinueDownload.setTextColor(getColor(R.color.white));
        dialogDownloadMenuBinding.vLine.setBackgroundColor(getColor(R.color.gray_c9));

        //清空列表
        dialogDownloadMenuBinding.llCleanList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadMenuPopupWindow.dismiss();
                getViewModel().deleteList();
            }
        });

        //继续下载
        dialogDownloadMenuBinding.llContinueDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadMenuPopupWindow.dismiss();
                DownloadHelper.getDownloadList();
                DownloadHelper.startDownload();
            }
        });


        ThemeHelper.getInstance().downloadMenuTheme(this, rThemeId, downloadMenuPopupWindow, dialogDownloadMenuBinding);


        downloadMenuPopupWindow.showAsDropDown(view,  PxUtil.getInstance().dp2px(-80, this),  PxUtil.getInstance().dp2px(10, this));


    }

    @SuppressLint({"SetTextI18n", "RemoteViewLayout"})
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.VIEW_DOWNLOAD_MUSIC:
                if(!TextUtils.isEmpty(event.str)) {
                    getViewDataBinding().tvMusicFileName.setText(event.str);
                }
                getViewDataBinding().pbProgress.setProgress(event.i);
                getViewDataBinding().tvValue.setText(String.valueOf(event.i));
                break;
            case ThreadEvent.VIEW_DOWNLOAD_MUSIC_SHOW:
                getViewDataBinding().llDownloadMain.setVisibility(View.VISIBLE);
                getViewDataBinding().llDownloadNull.setVisibility(View.GONE);
                break;
            case ThreadEvent.VIEW_DOWNLOAD_MUSIC_UPDATE:
                List<DownloadMusic> splist = SPUtil.getListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, DownloadMusic.class);
                if(splist.size()>0) {
                    downloadMusicList.clear();
                    downloadMusicList.addAll(splist);
                    downloadListAdapter.notifyDataSetChanged();
                }
                break;
            case ThreadEvent.VIEW_DOWNLOAD_MUSIC_FINISH:
                getViewDataBinding().pbProgress.setProgress(event.i);
                getViewDataBinding().tvValue.setText(String.valueOf(event.i));
                break;

            case ThreadEvent.VIEW_DOWNLOAD_MUSIC_CANCEL:
                getViewDataBinding().llDownloadMain.setVisibility(View.GONE);
                getViewDataBinding().llDownloadNull.setVisibility(View.VISIBLE);
                break;
        }
    }

    public static class DownloadListViewHolder extends RecyclerView.ViewHolder {
        public DownloadListViewHolder(@NonNull View itemView) { super(itemView); }
    }
    public class DownloadListAdapter extends RecyclerView.Adapter<DownloadListViewHolder> {

        private Context context;
        private List<DownloadMusic> list;

        public DownloadListAdapter(Context context, List<DownloadMusic> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public DownloadListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemDownloadListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                    R.layout.item_download_list, parent,false);
            return new DownloadListViewHolder(binding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull DownloadListViewHolder holder, int position) {
            ItemDownloadListBinding binding = DataBindingUtil.getBinding(holder.itemView);
            if(null != binding) {

                ThemeHelper.getInstance().downloadListTheme(context, rThemeId, binding);
                String fileName = list.get(position).fileName;
                binding.tvFileName.setText(!TextUtils.isEmpty(fileName) ? fileName : "");
                String status = list.get(position).status;
                if(!TextUtils.isEmpty(status)) {
                    if(status.equals("0")) {
                        binding.tvStatus.setText("已完成");
                    } else if(status.equals("1")) {
                        binding.tvStatus.setText("下载中");
                    } else if(status.equals("2")) {
                        binding.tvStatus.setText("失败");
                    } else if(status.equals("3")) {
                        binding.tvStatus.setText("等待中");
                    }
                } else {
                    binding.tvStatus.setText("");
                }

            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    @Override
    protected void onDestroy() {
        isDestroy = true;
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        DownloadReceiver.stopHandler();
    }
}
