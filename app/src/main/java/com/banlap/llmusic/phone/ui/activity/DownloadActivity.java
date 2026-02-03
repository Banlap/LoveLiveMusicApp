package com.banlap.llmusic.phone.ui.activity;

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
import com.banlap.llmusic.base.BaseApplication;
import com.banlap.llmusic.databinding.ActivityDownloadBinding;
import com.banlap.llmusic.databinding.DialogDownloadMenuBinding;
import com.banlap.llmusic.databinding.ItemDownloadListBinding;
import com.banlap.llmusic.receiver.DownloadReceiver;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.phone.ui.ThemeHelper;
import com.banlap.llmusic.phone.uivm.vm.DownloadVM;
import com.banlap.llmusic.sql.room.RoomDownloadMusic;
import com.banlap.llmusic.sql.AppData;
import com.banlap.llmusic.utils.AppExecutors;
import com.banlap.llmusic.utils.DownloadHelper;
import com.banlap.llmusic.utils.PxUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DownloadActivity extends BaseActivity<DownloadVM, ActivityDownloadBinding>
    implements DownloadVM.DownloadCallBack {

    private static final String TAG = DownloadActivity.class.getSimpleName();
    public List<RoomDownloadMusic> downloadMusicList;
    public DownloadListAdapter downloadListAdapter;
    private int rThemeId =0;

    private boolean isDestroy = false;

    @Override
    protected int getLayoutId() { return R.layout.activity_download; }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        downloadMusicList = new ArrayList<>();
        downloadMusicList.addAll(AppData.roomDownloadMusicList);
        downloadMusicList.add(new RoomDownloadMusic());
        downloadMusicList.add(new RoomDownloadMusic());

        if(AppData.roomSettings != null && !TextUtils.isEmpty(AppData.roomSettings.saveThemeId)) {
            try {
                changeTheme(Integer.parseInt(AppData.roomSettings.saveThemeId));
            } catch (Exception e) {
                Log.e(TAG, "saveThemeId转换失败,使用默认参数");
            }
        }

    }

    /** 改变主题 */
    private void changeTheme(int rId) {
        rThemeId = rId;
        //主题变更
        ThemeHelper.getInstance().downloadActivityTheme(this, rId, getViewDataBinding());
    }

    @Override
    protected void initView() {
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
        runOnUiThread(()-> {
            downloadMusicList.clear();
            downloadMusicList.addAll(AppData.roomDownloadMusicList);
            AppData.addNullDataDownloadMusic(downloadMusicList, 2);
            downloadListAdapter.notifyDataSetChanged();
        });
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
                refreshList();
                break;
            case ThreadEvent.VIEW_DOWNLOAD_MUSIC_FINISH:
                getViewDataBinding().pbProgress.setProgress(event.i);
                getViewDataBinding().tvValue.setText(String.valueOf(event.i));
                break;

            case ThreadEvent.VIEW_DOWNLOAD_MUSIC_CANCEL:
                getViewDataBinding().llDownloadMain.setVisibility(View.GONE);
                getViewDataBinding().llDownloadNull.setVisibility(View.VISIBLE);
                break;

            case ThreadEvent.VIEW_ROOM_GET_THEME_ID:
                if(event.str.equals(DownloadActivity.class.getSimpleName())) {
                    if(!TextUtils.isEmpty(event.str2)) {
                        try {
                            rThemeId = Integer.parseInt(event.str2);
                            changeTheme(rThemeId);
                        } catch (Exception e) {
                            Log.e(TAG, "themeId转换失败, ");
                        }
                    }
                }
                break;
        }
    }

    public static class DownloadListViewHolder extends RecyclerView.ViewHolder {
        public DownloadListViewHolder(@NonNull View itemView) { super(itemView); }
    }
    public class DownloadListAdapter extends RecyclerView.Adapter<DownloadListViewHolder> {

        private Context context;
        private List<RoomDownloadMusic> list;

        public DownloadListAdapter(Context context, List<RoomDownloadMusic> list) {
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
                    switch (status) {
                        case "0":
                            binding.tvStatus.setText("已完成");
                            break;
                        case "1":
                            binding.tvStatus.setText("下载中");
                            break;
                        case "2":
                            binding.tvStatus.setText("失败");
                            break;
                        case "3":
                            binding.tvStatus.setText("等待中");
                            break;
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
