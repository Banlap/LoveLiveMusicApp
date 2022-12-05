package com.banlap.llmusic.ui;

import android.Manifest;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseFragment;
import com.banlap.llmusic.databinding.FragmentLocalListBinding;
import com.banlap.llmusic.databinding.ItemLocalMusicListBinding;
import com.banlap.llmusic.model.LocalFile;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.uivm.LocalListFVM;
import com.banlap.llmusic.utils.MyAnimationUtil;
import com.banlap.llmusic.utils.SPUtil;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocalListFragment extends BaseFragment<LocalListFVM, FragmentLocalListBinding>
    implements LocalListFVM.LocalListCallBack {

    private final static String TAG = LocalListFragment.class.getSimpleName();
    private List<LocalFile> mLocalMusicList;
    private AlertDialog mAlertDialog;                   //弹窗
    private LocalListAdapter localListAdapter;
    private RequestOptions requestOptions;
    private int rThemeId =0;                             //当前主题
    private ActivityResultLauncher<Intent> intentSelectMusicLauncher;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_local_list;
    }

    @Override
    protected void initData() {
        mLocalMusicList = new ArrayList<>();
        //本地缓存列表
        List<LocalFile> spList = SPUtil.getListValue(getActivity(), "LocalListData", LocalFile.class);
        if(spList.size()>0){
            mLocalMusicList.addAll(spList);
        } else {
            LocalFile nullLocalFile = new LocalFile();
            LocalFile nullLocalFile2 = new LocalFile();
            mLocalMusicList.add(nullLocalFile);
            mLocalMusicList.add(nullLocalFile2);
            SPUtil.setListValue(getActivity(), "LocalListData", mLocalMusicList);
        }

        requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.format(DecodeFormat.PREFER_RGB_565); //设置为这种格式去掉透明度通道，可以减少内存占有

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        EventBus.getDefault().register(this);

        changeTheme();
        localListAdapter = new LocalListAdapter(getContext(), mLocalMusicList);
        getViewDataBinding().rvLocalMusicList.setLayoutManager(new LinearLayoutManager(getContext()));
        getViewDataBinding().rvLocalMusicList.setAdapter(localListAdapter);
        localListAdapter.notifyDataSetChanged();

        if(null != mLocalMusicList) {
            getViewDataBinding().llLocalListNull.setVisibility(mLocalMusicList.size()>2 ? View.GONE : View.VISIBLE);
            getViewDataBinding().tvMusicCount.setText("" + (mLocalMusicList.size()-2));
        }

        intentSelectMusicLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(null != result.getData()) {
                            getViewModel().selectFile(result.getData());
                        }
                    }
                }
        );

        getViewDataBinding().llEditDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mLocalMusicList && mLocalMusicList.size() >0) {
                    for(int i=0; i< mLocalMusicList.size(); i++) {
                        mLocalMusicList.get(i).setDelete(!mLocalMusicList.get(i).isDelete);
                    }
                    localListAdapter.notifyItemRangeChanged(0, mLocalMusicList.size(), R.id.iv_delete);
                }

            }
        });

    }


    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.SCAN_LOCAL_FILE_BY_CHECK_PERMISSION:
                checkScanPermission(event.str);
                break;
            case ThreadEvent.SCAN_LOCAL_FILE:
                scan();
                break;
            case ThreadEvent.SCAN_LOCAL_FILE_SUCCESS:
                if(null != mLocalMusicList) {
                    mLocalMusicList.clear();
                    mLocalMusicList.addAll(event.tList);
                    getViewDataBinding().llLocalListNull.setVisibility(mLocalMusicList.size()>2 ? View.GONE : View.VISIBLE);
                    localListAdapter.notifyDataSetChanged();

                    getViewDataBinding().tvMusicCount.setText("" + (mLocalMusicList.size()-2));
                    SPUtil.setListValue(getContext(), "LocalListData", mLocalMusicList);

                }
                break;
            case ThreadEvent.SCAN_LOCAL_FILE_ERROR:
                break;
            case ThreadEvent.SELECT_LOCAL_FILE_SUCCESS:
                if(null != mLocalMusicList) {
                    List<LocalFile> localFileList = new ArrayList<>();
                    localFileList.addAll(mLocalMusicList);

                    mLocalMusicList.clear();
                    mLocalMusicList.addAll(event.tList);
                    mLocalMusicList.addAll(localFileList);

                    getViewDataBinding().llLocalListNull.setVisibility(mLocalMusicList.size()>2 ? View.GONE : View.VISIBLE);
                    localListAdapter.notifyDataSetChanged();

                    getViewDataBinding().tvMusicCount.setText("" + (mLocalMusicList.size()-2));
                    SPUtil.setListValue(getContext(), "LocalListData", mLocalMusicList);
                }
                break;
            case ThreadEvent.VIEW_CHANGE_THEME:
                changeTheme();
                break;
            case ThreadEvent.VIEW_DELETE_LOCAL_MUSIC:
                getViewDataBinding().tvMusicCount.setText(""+(mLocalMusicList.size()-2));
                getViewDataBinding().llLocalListNull.setVisibility(mLocalMusicList.size()>2 ? View.GONE : View.VISIBLE);
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        changeTheme();
    }



    /**
     * 改变主题
     * */
    private void changeTheme() {
        String strThemeId = SPUtil.getStrValue(getContext(), "SaveThemeId");
        if(strThemeId!=null) {
            if(!strThemeId.equals("")) {
                rThemeId = Integer.parseInt(strThemeId);
                changeTheme(rThemeId);
            }
        }
    }
    private void changeTheme(int rThemeId) {
        if(rThemeId == R.id.ll_theme_normal) {
            getViewDataBinding().tvSingleMusic.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvMusicCount.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvNullLocalList.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvEditDelete.setTextColor(getResources().getColor(R.color.light_ff));
        } else if(rThemeId == R.id.ll_theme_dark) {
            getViewDataBinding().tvSingleMusic.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvMusicCount.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvNullLocalList.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvEditDelete.setTextColor(getResources().getColor(R.color.white));
        } else if(rThemeId == R.id.ll_theme_white) {
            getViewDataBinding().tvSingleMusic.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvMusicCount.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvNullLocalList.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvEditDelete.setTextColor(getResources().getColor(R.color.purple));
        } else if(rThemeId == R.id.ll_theme_orange) {
            getViewDataBinding().tvSingleMusic.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvMusicCount.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvNullLocalList.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvEditDelete.setTextColor(getResources().getColor(R.color.orange_0b));
        } else if(rThemeId == R.id.ll_theme_light) {
            getViewDataBinding().tvSingleMusic.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvMusicCount.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvNullLocalList.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvEditDelete.setTextColor(getResources().getColor(R.color.light_ff));
        } else {
            getViewDataBinding().tvSingleMusic.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvMusicCount.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvNullLocalList.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvEditDelete.setTextColor(getResources().getColor(R.color.light_ff));
        }
        if(null != localListAdapter) {
            localListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void viewBack() {

    }

    private void checkScanPermission(String type) {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        //验证是否许可权限
        for (String str : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), str) != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                ActivityCompat.requestPermissions(getActivity(), permissions, MainActivity.REQUEST_CODE_SCAN_LOCAL_FILE);
                return;
            }
        }

        if("scan".equals(type)) {
            scan();
        } else {
            selectFile();
        }

    }

    private void scan() {
        getViewModel().scan();
    }

    private void selectFile() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.setDataAndType(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "audio/*");
        intentSelectMusicLauncher.launch(intent);
    }



    public static class LocalListViewHolder extends RecyclerView.ViewHolder {
        public LocalListViewHolder(@NonNull View itemView) { super(itemView); }
    }
    public class LocalListAdapter extends RecyclerView.Adapter<LocalListViewHolder> {

        private Context context;
        private List<LocalFile> localFileList;

        public LocalListAdapter(Context context, List<LocalFile> localFileList) {
            this.context = context;
            this.localFileList = localFileList;
        }

        @NonNull
        @Override
        public LocalListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemLocalMusicListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_local_music_list, parent, false);
            return new LocalListViewHolder(binding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull LocalListViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            final ItemLocalMusicListBinding binding = DataBindingUtil.getBinding(holder.itemView);
            if (binding != null) {

                //变更主题
                if(rThemeId!=0) {
                    if(rThemeId == R.id.ll_theme_normal) {
                        binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                        binding.tvMusicName.setTextColor(getResources().getColor(R.color.light_ff));
                        binding.tvSingerName.setTextColor(getResources().getColor(R.color.light_ff));
                        binding.ivAdd.setBackgroundResource(R.drawable.ic_add_light);
                        binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_light_ff);
                    } else if(rThemeId == R.id.ll_theme_dark) {
                        binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                        binding.tvMusicName.setTextColor(getResources().getColor(R.color.white));
                        binding.tvSingerName.setTextColor(getResources().getColor(R.color.white));
                        binding.ivAdd.setBackgroundResource(R.drawable.ic_add);
                        binding.ivDelete.setBackgroundResource(R.drawable.ic_delete);
                    } else if (rThemeId == R.id.ll_theme_white) {
                        binding.rlMusicAll.setBackgroundResource(R.drawable.selector_white_theme_selected2);
                        binding.tvMusicName.setTextColor(getResources().getColor(R.color.purple));
                        binding.tvSingerName.setTextColor(getResources().getColor(R.color.gray_purple_ac));
                        binding.ivAdd.setBackgroundResource(R.drawable.ic_add_gray_purple);
                        binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_purple_ac);
                    } else if (rThemeId == R.id.ll_theme_orange) {
                        binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                        binding.tvMusicName.setTextColor(getResources().getColor(R.color.orange_0b));
                        binding.tvSingerName.setTextColor(getResources().getColor(R.color.orange_0b));
                        binding.ivAdd.setBackgroundResource(R.drawable.ic_add_orange);
                        binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_orange);
                    } else if(rThemeId == R.id.ll_theme_light) {
                        binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                        binding.tvMusicName.setTextColor(getResources().getColor(R.color.light_ff));
                        binding.tvSingerName.setTextColor(getResources().getColor(R.color.light_ff));
                        binding.ivAdd.setBackgroundResource(R.drawable.ic_add_light);
                    } else {
                        binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                        binding.tvMusicName.setTextColor(getResources().getColor(R.color.light_ff));
                        binding.tvSingerName.setTextColor(getResources().getColor(R.color.light_ff));
                        binding.ivAdd.setBackgroundResource(R.drawable.ic_add_light);
                        binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_light_ff);
                    }
                } else {
                    binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                    binding.tvMusicName.setTextColor(getResources().getColor(R.color.light_ff));
                    binding.tvSingerName.setTextColor(getResources().getColor(R.color.light_ff));
                    binding.ivAdd.setBackgroundResource(R.drawable.ic_add_light);
                    binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_light_ff);
                }

                binding.llDelete.setVisibility(localFileList.get(position).isDelete? View.VISIBLE :View.GONE);
                binding.rlMusicAll.setVisibility(localFileList.get(position).title !=null? View.VISIBLE :View.GONE);
                binding.tvMusicName.setText(localFileList.get(position).title);
                binding.tvSingerName.setText(localFileList.get(position).artist);

                binding.ivMusicImg.setTag(R.id.img_load, position);
                Log.e(TAG, "position: " + position + " pic: " + localFileList.get(position).pic +
                        " new pic: " + mLocalMusicList.get(position).pic);

                binding.ivMusicImg.setImageBitmap(null);

                if(null != localFileList.get(position).pic) {
                    byte[] b = localFileList.get(position).pic;
                    Bitmap bitMap = BitmapFactory.decodeByteArray(b, 0, localFileList.get(position).pic.length);
                    binding.ivMusicImg.setImageBitmap(bitMap);
                   /* Glide.with(binding.ivMusicImg.getContext())
                            .setDefaultRequestOptions(requestOptions)
                            .load(bitMap)
                            .skipMemoryCache(true)//跳过内存缓存
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(binding.ivMusicImg);*/
                    int tag = (int) binding.ivMusicImg.getTag(R.id.img_load);
                    Log.e(TAG, "tag: " + tag);

                } else {
                    binding.ivMusicImg.setImageBitmap(null);
                    //Glide.with(binding.ivMusicImg.getContext()).clear(binding.ivMusicImg);
                    int tag = (int) binding.ivMusicImg.getTag(R.id.img_load);
                    Log.e(TAG, "tag: " + tag);
                }


                binding.rlMusicAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = new Random().nextInt(99999) + 10000;
                        Music music = new Music();
                        music.setMusicId(id);
                        music.setMusicName(localFileList.get(position).title);
                        music.setMusicSinger(localFileList.get(position).artist);
                        music.setMusicURL(localFileList.get(position).path);
                        music.setMusicImgByte(localFileList.get(position).pic);
                        music.setLocal(true);
                        EventBus.getDefault().post(new ThreadEvent<Music>(ThreadEvent.PLAY_LOCAL_MUSIC, music, false));
                    }
                });


                binding.llAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //变更主题
                        if(rThemeId!=0) {
                            if (rThemeId == R.id.ll_theme_normal) {
                                binding.ivAddAnimator.setVisibility(View.GONE);
                                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight1.setVisibility(View.VISIBLE);
                                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight1);
                                animatorSet.start();
                            } else if(rThemeId == R.id.ll_theme_dark) {
                                binding.ivAddAnimator.setVisibility(View.GONE);
                                binding.ivAddAnimatorBlack.setVisibility(View.VISIBLE);
                                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorBlack);
                                animatorSet.start();

                            } else if (rThemeId == R.id.ll_theme_white) {
                                binding.ivAddAnimator.setVisibility(View.VISIBLE);
                                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimator);
                                animatorSet.start();
                            } else if (rThemeId == R.id.ll_theme_orange) {
                                binding.ivAddAnimator.setVisibility(View.GONE);
                                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                                binding.ivAddAnimatorOrange.setVisibility(View.VISIBLE);
                                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorOrange);
                                animatorSet.start();
                            } else if(rThemeId == R.id.ll_theme_light) {
                                binding.ivAddAnimator.setVisibility(View.GONE);
                                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight2);
                                animatorSet.start();
                            } else {
                                binding.ivAddAnimator.setVisibility(View.GONE);
                                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight1);
                                animatorSet.start();
                            }
                        } else {
                            binding.ivAddAnimator.setVisibility(View.GONE);
                            binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                            binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                            binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                            binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                            AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight1);
                            animatorSet.start();
                        }

                        int id = new Random().nextInt(99999) + 10000;
                        Music music = new Music();
                        music.setMusicId(id);
                        music.setMusicName(localFileList.get(position).title);
                        music.setMusicSinger(localFileList.get(position).artist);
                        music.setMusicURL(localFileList.get(position).path);
                        music.setMusicImgByte(localFileList.get(position).pic);
                        music.setLocal(true);
                        EventBus.getDefault().post(new ThreadEvent<Music>(ThreadEvent.ADD_LOCAL_MUSIC, music, false));
                    }
                });

                binding.llDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        localFileList.remove(position);
                        localListAdapter.notifyItemRemoved(position);
                        localListAdapter.notifyItemRangeChanged(position, mLocalMusicList.size());
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DELETE_LOCAL_MUSIC));
                        SPUtil.setListValue(getContext(), "LocalListData", localFileList);
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return localFileList.size();
        }
    }




}
