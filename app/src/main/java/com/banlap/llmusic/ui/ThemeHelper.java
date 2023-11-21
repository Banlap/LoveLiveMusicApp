package com.banlap.llmusic.ui;

import static android.view.View.GONE;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.banlap.llmusic.R;
import com.banlap.llmusic.databinding.ActivityMainBinding;
import com.banlap.llmusic.databinding.ActivitySettingsBinding;
import com.banlap.llmusic.databinding.DialogChangeModeMenuBinding;
import com.banlap.llmusic.databinding.DialogCharacterMenuBinding;
import com.banlap.llmusic.databinding.DialogMainMenuBinding;
import com.banlap.llmusic.databinding.DialogPanelMoreMenuBinding;
import com.banlap.llmusic.databinding.DialogSortMenuBinding;
import com.banlap.llmusic.databinding.FragmentLocalListBinding;
import com.banlap.llmusic.databinding.FragmentMainListBinding;
import com.banlap.llmusic.databinding.ItemLocalMusicListBinding;
import com.banlap.llmusic.databinding.ItemLocalPlayListAddBinding;
import com.banlap.llmusic.databinding.ItemLocalPlayListBinding;
import com.banlap.llmusic.databinding.ItemMusicListBinding;
import com.banlap.llmusic.databinding.ItemPlayListBinding;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.service.MusicPlayService;
import com.banlap.llmusic.ui.activity.MainActivity;
import com.banlap.llmusic.utils.MyAnimationUtil;
import com.banlap.llmusic.widget.HorizontalProgressBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;

/**
 * 主题管理类
 * */
public class ThemeHelper {

    public static ThemeHelper getInstance() { return new ThemeHelper(); }
    private RequestOptions requestOptions;

    /**
     * MainActivity主题改变 - 整体ui
     * */
    public void changeTheme(Context context, int rThemeId, ActivityMainBinding vdb, MusicPlayService.MusicBinder binder) {
        if(requestOptions == null) {
            requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            //requestOptions.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL); //关键代码，加载原始大小
            requestOptions.format(DecodeFormat.PREFER_RGB_565); //设置为这种格式去掉透明度通道，可以减少内存占有
        }

        if(rThemeId == R.id.ll_theme_normal) {
            vdb.rlPlayControllerIn.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.clCurrentMusicPanel.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.clCurrentMusicList.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.clNewCurrentMusicList.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.tvDiscover.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvLocal.setTextColor(context.getResources().getColor(R.color.white));
            vdb.vLine.setBackgroundResource(R.drawable.shape_button_white);
            vdb.tvTitleBar.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvListMsgName1.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvPlayAll.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvCancel.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvListMsgName2.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvSingle.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvCount.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvMusicCount.setTextColor(context.getResources().getColor(R.color.light_ff));


            vdb.tvPlayMode.setTextColor(context.getResources().getColor(R.color.black));
            vdb.tvListSize.setTextColor(context.getResources().getColor(R.color.black));
            vdb.tvMusicName.setTextColor(context.getResources().getColor(R.color.black));
            vdb.tvSingerName.setTextColor(context.getResources().getColor(R.color.black));
            vdb.tvStartTime.setTextColor(context.getResources().getColor(R.color.black));
            vdb.tvAllTime.setTextColor(context.getResources().getColor(R.color.black));
            vdb.tvNewPlayMode.setTextColor(context.getResources().getColor(R.color.black));
            vdb.tvNewListSize.setTextColor(context.getResources().getColor(R.color.black));
            vdb.tvNewMusicName.setTextColor(context.getResources().getColor(R.color.black_33));
            vdb.tvNewSingerName.setTextColor(context.getResources().getColor(R.color.black_33));
            vdb.tvNewStartTime.setTextColor(context.getResources().getColor(R.color.black_33));
            vdb.tvNewAllTime.setTextColor(context.getResources().getColor(R.color.black_33));
            vdb.tvNewPlayMusicName.setTextColor(context.getResources().getColor(R.color.black));

            vdb.clBg.setBackgroundResource(R.mipmap.ic_gradient_color5);
            vdb.clControllerMode.setBackgroundResource(R.mipmap.ic_gradient_color5);
            //vdb.rlPlayController.setBackgroundResource(R.drawable.shape_button_alpha_50);
            vdb.btCurrentList.setBackgroundResource(R.drawable.selector_music_list_2_selected);
            vdb.btNewCurrentList.setBackgroundResource(R.drawable.selector_music_list_2_selected);

            vdb.llAllPlay.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSearch.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSort.setBackgroundResource(R.drawable.selector_normal_selected);
            //vdb.llSettings.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSearchBar.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llCancel.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llBack.setBackgroundResource(R.drawable.selector_normal_selected);

            vdb.ivAllPlay.setBackgroundResource(R.drawable.ic_play_mini_light);
            vdb.ivSearch.setBackgroundResource(R.drawable.ic_search_light);
            vdb.ivSort.setBackgroundResource(R.drawable.ic_sort_light);
            //vdb.ivSettings.setBackgroundResource(R.drawable.ic_settings_light);
            vdb.ivDeleteAll.setBackgroundResource(R.drawable.ic_delete_black);
            vdb.ivNewDeleteAll.setBackgroundResource(R.drawable.ic_delete_black);
            vdb.ivSearchMusic.setBackgroundResource(R.drawable.ic_search_light);
            vdb.ivBack.setBackgroundResource(R.drawable.ic_arrow_back_light);
            vdb.ivPanelLast.setBackgroundResource(R.drawable.selector_last_black_selected);
            vdb.ivPanelNext.setBackgroundResource(R.drawable.selector_next_black_selected);
            vdb.ivNewPanelLast.setBackgroundResource(R.drawable.selector_last_black_33_selected);
            vdb.ivNewPanelNext.setBackgroundResource(R.drawable.selector_next_black_33_selected);
            vdb.ivChangeMode.setBackgroundResource(R.drawable.ic_bg_mode_black);
            vdb.ivSettings.setBackgroundResource(R.drawable.ic_more_black);

            vdb.ivCloseControllerMode.setBackgroundResource(R.drawable.ic_arrow_down_black_33);
            vdb.ivNewChangeMode.setBackgroundResource(R.drawable.ic_bg_mode_black_33);
            vdb.ivNewMyFavorite.setBackgroundResource(MainActivity.isFavorite? R.drawable.ic_favorite_black_33 : R.drawable.ic_favorite_empty_black_33);

            vdb.ivNewMore.setBackgroundResource(R.drawable.ic_more_black_33);
            vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black_33);
            vdb.ivNewCurrentList.setBackgroundResource(R.drawable.ic_music_list_black_33);

            vdb.etSearchMusic.setHintTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.etSearchMusic.setTextColor(context.getResources().getColor(R.color.light_ff));

            vdb.llMainMenuBt.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.ivMainMenuBt.setBackgroundResource(R.drawable.ic_menu);

            if(MainActivity.playMode == 0) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
            } else if (MainActivity.playMode == 1) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
            } else if (MainActivity.playMode == 2) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
            }

            if(binder!=null) {
                if (binder.isPlay()) {
                    vdb.btPlay.setBackgroundResource(R.drawable.selector_pause_black_selected);
                    vdb.btNewPlay.setBackgroundResource(R.drawable.selector_pause_black_selected);
                    vdb.ivPanelPlay.setBackgroundResource(R.drawable.selector_pause_circle_black_selected);
                    vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.selector_pause_circle_black_33_selected);
                } else {
                    vdb.btPlay.setBackgroundResource(R.drawable.selector_play_black_selected);
                    vdb.btNewPlay.setBackgroundResource(R.drawable.selector_play_black_selected);
                    vdb.ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_black_selected);
                    vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_black_33_selected);
                }
            } else {
                vdb.btPlay.setBackgroundResource(R.drawable.selector_play_black_selected);
                vdb.btNewPlay.setBackgroundResource(R.drawable.selector_play_black_selected);
                vdb.ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_black_selected);
                vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_black_33_selected);
            }
            if(MainActivity.currentMusicImg != null || MainActivity.currentBitmap != null) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load((MainActivity.currentMusicImg != null && !MainActivity.currentMusicImg.equals(""))? MainActivity.currentMusicImg : MainActivity.currentBitmap)
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.light_f9)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivMusicImg);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load((MainActivity.currentMusicImg != null && !MainActivity.currentMusicImg.equals(""))? MainActivity.currentMusicImg : MainActivity.currentBitmap)
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.light_f9)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivNewMusicImg);
            }
            //解决seekBar滚动条变形问题
            Rect r = vdb.sbMusicBar.getProgressDrawable().getBounds();
            vdb.sbMusicBar.setThumb(context.getResources().getDrawable(R.drawable.shape_seek_bar_thumb));
            vdb.sbMusicBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_seek_bar));
            vdb.sbMusicBar.getProgressDrawable().setBounds(r);
            Rect r2 = vdb.sbNewMusicBar.getProgressDrawable().getBounds();
            vdb.sbNewMusicBar.setThumb(context.getResources().getDrawable(R.drawable.shape_seek_bar_thumb));
            vdb.sbNewMusicBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_seek_bar));
            vdb.sbNewMusicBar.getProgressDrawable().setBounds(r2);
            //vdb.sbMusicBar.setProgressTintMode(PorterDuff.Mode.SRC_ATOP);
            //loading加载颜色
            vdb.pbLoadingMusic.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.light_f9), PorterDuff.Mode.SRC_IN);
            vdb.prLoading.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.light_f9), PorterDuff.Mode.SRC_IN);
            vdb.hpvProgress.setLinearGradient(R.color.light_f9);
            vdb.pbNewProgress.setProgressDrawable(context.getDrawable(R.drawable.shape_bg_progress_bar_light_f8));
        } else if(rThemeId == R.id.ll_theme_blue) {
            vdb.rlPlayControllerIn.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.clCurrentMusicPanel.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.clCurrentMusicList.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.clNewCurrentMusicList.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.pbLoadingMusic.setProgressDrawable(context.getResources().getDrawable(R.color.blue_0E));
            vdb.tvDiscover.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvLocal.setTextColor(context.getResources().getColor(R.color.white));
            vdb.vLine.setBackgroundResource(R.drawable.shape_button_white);
            vdb.tvTitleBar.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvListMsgName1.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvListMsgName2.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvPlayAll.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvCancel.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvMusicCount.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvSingle.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvCount.setTextColor(context.getResources().getColor(R.color.white));

            vdb.tvPlayMode.setTextColor(context.getResources().getColor(R.color.blue_0E));
            vdb.tvListSize.setTextColor(context.getResources().getColor(R.color.blue_0E));
            vdb.tvMusicName.setTextColor(context.getResources().getColor(R.color.blue_0E));
            vdb.tvSingerName.setTextColor(context.getResources().getColor(R.color.blue_0E));
            vdb.tvStartTime.setTextColor(context.getResources().getColor(R.color.blue_0E));
            vdb.tvAllTime.setTextColor(context.getResources().getColor(R.color.blue_0E));
            vdb.tvNewPlayMode.setTextColor(context.getResources().getColor(R.color.blue_0E));
            vdb.tvNewListSize.setTextColor(context.getResources().getColor(R.color.blue_0E));
            vdb.tvNewMusicName.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvNewSingerName.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvNewStartTime.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvNewAllTime.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvNewPlayMusicName.setTextColor(context.getResources().getColor(R.color.blue_0E));

            vdb.clBg.setBackgroundResource(R.mipmap.ic_gradient_color8);
            vdb.clControllerMode.setBackgroundResource(R.mipmap.ic_gradient_color8);
            //vdb.rlPlayController.setBackgroundResource(R.drawable.shape_button_orange_alpha_50);
            vdb.btCurrentList.setBackgroundResource(R.drawable.ic_music_list_blue);
            vdb.btNewCurrentList.setBackgroundResource(R.drawable.ic_music_list_blue);

            vdb.llAllPlay.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSearch.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSort.setBackgroundResource(R.drawable.selector_normal_selected);
            //vdb.llSettings.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSearchBar.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llCancel.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llBack.setBackgroundResource(R.drawable.selector_normal_selected);

            vdb.ivAllPlay.setBackgroundResource(R.drawable.ic_play_mini_white);
            vdb.ivSearch.setBackgroundResource(R.drawable.ic_search);
            vdb.ivSort.setBackgroundResource(R.drawable.ic_sort);
            //vdb.ivSettings.setBackgroundResource(R.drawable.ic_settings);
            vdb.ivDeleteAll.setBackgroundResource(R.drawable.ic_delete_blue);
            vdb.ivNewDeleteAll.setBackgroundResource(R.drawable.ic_delete_blue);
            vdb.ivSearchMusic.setBackgroundResource(R.drawable.ic_search);
            vdb.ivBack.setBackgroundResource(R.drawable.ic_arrow_back);
            vdb.ivPanelLast.setBackgroundResource(R.drawable.selector_last_blue_selected);
            vdb.ivPanelNext.setBackgroundResource(R.drawable.selector_next_blue_selected);
            vdb.ivNewPanelLast.setBackgroundResource(R.drawable.selector_last_selected);
            vdb.ivNewPanelNext.setBackgroundResource(R.drawable.selector_next_selected);
            vdb.ivChangeMode.setBackgroundResource(R.drawable.ic_bg_mode_blue);
            vdb.ivSettings.setBackgroundResource(R.drawable.ic_more_blue);

            vdb.ivCloseControllerMode.setBackgroundResource(R.drawable.ic_arrow_down);
            vdb.ivNewChangeMode.setBackgroundResource(R.drawable.ic_bg_mode);
            vdb.ivNewMyFavorite.setBackgroundResource(MainActivity.isFavorite? R.drawable.ic_favorite : R.drawable.ic_favorite_empty);

            vdb.ivNewMore.setBackgroundResource(R.drawable.ic_more);
            vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_order_play);
            vdb.ivNewCurrentList.setBackgroundResource(R.drawable.ic_music_list);

            vdb.etSearchMusic.setHintTextColor(context.getResources().getColor(R.color.white));
            vdb.etSearchMusic.setTextColor(context.getResources().getColor(R.color.white));

            vdb.llMainMenuBt.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.ivMainMenuBt.setBackgroundResource(R.drawable.ic_menu);

            if(MainActivity.playMode == 0) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_blue);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_order_play_blue);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_blue);
            } else if (MainActivity.playMode == 1) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_blue);
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_blue);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_blue);
            } else if (MainActivity.playMode == 2) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_blue);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_single_play_blue);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_blue);
            }

            if(binder!=null) {
                if (binder.isPlay()) {
                    vdb.btPlay.setBackgroundResource(R.drawable.selector_pause_blue_selected);
                    vdb.btNewPlay.setBackgroundResource(R.drawable.selector_pause_blue_selected);
                    vdb.ivPanelPlay.setBackgroundResource(R.drawable.selector_pause_circle_blue_selected);
                    vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.ic_pause_circle_white);
                } else {
                    vdb.btPlay.setBackgroundResource(R.drawable.selector_play_blue_selected);
                    vdb.btNewPlay.setBackgroundResource(R.drawable.selector_play_blue_selected);
                    vdb.ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_blue_selected);
                    vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.ic_play_circle_white);
                }
            } else {
                vdb.btPlay.setBackgroundResource(R.drawable.selector_play_blue_selected);
                vdb.btNewPlay.setBackgroundResource(R.drawable.selector_play_blue_selected);
                vdb.ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_blue_selected);
                vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.ic_play_circle_white);
            }
            if(MainActivity.currentMusicImg != null || MainActivity.currentBitmap != null) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load((MainActivity.currentMusicImg != null && !MainActivity.currentMusicImg.equals(""))? MainActivity.currentMusicImg : MainActivity.currentBitmap)
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.blue_0E)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivMusicImg);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load((MainActivity.currentMusicImg != null && !MainActivity.currentMusicImg.equals(""))? MainActivity.currentMusicImg : MainActivity.currentBitmap)
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.blue_0E)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivNewMusicImg);
            }
            //解决seekBar滚动条变形问题
            Rect r = vdb.sbMusicBar.getProgressDrawable().getBounds();
            vdb.sbMusicBar.setThumb(context.getResources().getDrawable(R.drawable.shape_seek_bar_thumb_blue));
            vdb.sbMusicBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_seek_bar_blue));
            vdb.sbMusicBar.getProgressDrawable().setBounds(r);
            Rect r2 = vdb.sbNewMusicBar.getProgressDrawable().getBounds();
            vdb.sbNewMusicBar.setThumb(context.getResources().getDrawable(R.drawable.shape_seek_bar_thumb_blue));
            vdb.sbNewMusicBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_seek_bar_blue));
            vdb.sbNewMusicBar.getProgressDrawable().setBounds(r2);
            //vdb.sbMusicBar.setProgressTintMode(PorterDuff.Mode.SRC_ATOP);
            //loading加载颜色
            vdb.pbLoadingMusic.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.blue_ac), PorterDuff.Mode.SRC_IN);
            vdb.prLoading.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.blue_ac), PorterDuff.Mode.SRC_IN);
            vdb.hpvProgress.setLinearGradient(R.color.blue_0E);
            vdb.pbNewProgress.setProgressDrawable(context.getDrawable(R.drawable.shape_bg_progress_bar_blue));
        } else if(rThemeId == R.id.ll_theme_dark) {
            vdb.rlPlayControllerIn.setBackgroundResource(R.drawable.shape_button_black_2);
            vdb.clCurrentMusicPanel.setBackgroundResource(R.drawable.shape_button_black_2);
            vdb.clCurrentMusicList.setBackgroundResource(R.drawable.shape_button_black_2);
            vdb.clNewCurrentMusicList.setBackgroundResource(R.drawable.shape_button_black_2);
            vdb.pbLoadingMusic.setProgressDrawable(context.getResources().getDrawable(R.color.gray_36));
            vdb.tvDiscover.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvLocal.setTextColor(context.getResources().getColor(R.color.white));
            vdb.vLine.setBackgroundResource(R.drawable.shape_button_white);
            vdb.tvTitleBar.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvListMsgName1.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvListMsgName2.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvPlayAll.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvCancel.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvMusicCount.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvSingle.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvCount.setTextColor(context.getResources().getColor(R.color.white));

            vdb.tvPlayMode.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvListSize.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvMusicName.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvSingerName.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvStartTime.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvAllTime.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvNewPlayMode.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvNewListSize.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvNewMusicName.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvNewSingerName.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvNewStartTime.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvNewAllTime.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvNewPlayMusicName.setTextColor(context.getResources().getColor(R.color.white));

            vdb.clBg.setBackgroundResource(R.mipmap.ic_gradient_color6);
            vdb.clControllerMode.setBackgroundResource(R.mipmap.ic_gradient_color6);
            //vdb.rlPlayController.setBackgroundResource(R.drawable.shape_button_orange_alpha_50);
            vdb.btCurrentList.setBackgroundResource(R.drawable.ic_music_list);
            vdb.btNewCurrentList.setBackgroundResource(R.drawable.ic_music_list);

            vdb.llAllPlay.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSearch.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSort.setBackgroundResource(R.drawable.selector_normal_selected);
            //vdb.llSettings.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSearchBar.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llCancel.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llBack.setBackgroundResource(R.drawable.selector_normal_selected);

            vdb.ivAllPlay.setBackgroundResource(R.drawable.ic_play_mini_white);
            vdb.ivSearch.setBackgroundResource(R.drawable.ic_search);
            vdb.ivSort.setBackgroundResource(R.drawable.ic_sort);
            //vdb.ivSettings.setBackgroundResource(R.drawable.ic_settings);
            vdb.ivDeleteAll.setBackgroundResource(R.drawable.ic_delete);
            vdb.ivNewDeleteAll.setBackgroundResource(R.drawable.ic_delete);
            vdb.ivSearchMusic.setBackgroundResource(R.drawable.ic_search);
            vdb.ivBack.setBackgroundResource(R.drawable.ic_arrow_back);
            vdb.ivPanelLast.setBackgroundResource(R.drawable.selector_last_selected);
            vdb.ivPanelNext.setBackgroundResource(R.drawable.selector_next_selected);
            vdb.ivNewPanelLast.setBackgroundResource(R.drawable.selector_last_selected);
            vdb.ivNewPanelNext.setBackgroundResource(R.drawable.selector_next_selected);
            vdb.ivChangeMode.setBackgroundResource(R.drawable.ic_bg_mode);
            vdb.ivSettings.setBackgroundResource(R.drawable.ic_more);

            vdb.ivCloseControllerMode.setBackgroundResource(R.drawable.ic_arrow_down);
            vdb.ivNewChangeMode.setBackgroundResource(R.drawable.ic_bg_mode);
            vdb.ivNewMyFavorite.setBackgroundResource(MainActivity.isFavorite? R.drawable.ic_favorite : R.drawable.ic_favorite_empty);

            vdb.ivNewMore.setBackgroundResource(R.drawable.ic_more);
            vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_order_play);
            vdb.ivNewCurrentList.setBackgroundResource(R.drawable.ic_music_list);

            vdb.etSearchMusic.setHintTextColor(context.getResources().getColor(R.color.white));
            vdb.etSearchMusic.setTextColor(context.getResources().getColor(R.color.white));

            vdb.llMainMenuBt.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.ivMainMenuBt.setBackgroundResource(R.drawable.ic_menu);

            if(MainActivity.playMode == 0) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_order_play);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_order_play);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play);
            } else if (MainActivity.playMode == 1) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_random_play);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_random_play);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play);
            } else if (MainActivity.playMode == 2) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_single_play);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_single_play);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play);
            }

            if(binder!=null) {
                if (binder.isPlay()) {
                    vdb.btPlay.setBackgroundResource(R.drawable.ic_pause_2_white);
                    vdb.btNewPlay.setBackgroundResource(R.drawable.ic_pause_2_white);
                    vdb.ivPanelPlay.setBackgroundResource(R.drawable.ic_pause_circle_white);
                    vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.ic_pause_circle_white);
                } else {
                    vdb.btPlay.setBackgroundResource(R.drawable.ic_play_2_white);
                    vdb.btNewPlay.setBackgroundResource(R.drawable.ic_play_2_white);
                    vdb.ivPanelPlay.setBackgroundResource(R.drawable.ic_play_circle_white);
                    vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.ic_play_circle_white);
                }
            } else {
                vdb.btPlay.setBackgroundResource(R.drawable.ic_play_2_white);
                vdb.btNewPlay.setBackgroundResource(R.drawable.ic_play_2_white);
                vdb.ivPanelPlay.setBackgroundResource(R.drawable.ic_play_circle_white);
                vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.ic_play_circle_white);
            }
            if(MainActivity.currentMusicImg != null || MainActivity.currentBitmap != null) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load((MainActivity.currentMusicImg != null && !MainActivity.currentMusicImg.equals(""))? MainActivity.currentMusicImg : MainActivity.currentBitmap)
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.white)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivMusicImg);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load((MainActivity.currentMusicImg != null && !MainActivity.currentMusicImg.equals(""))? MainActivity.currentMusicImg : MainActivity.currentBitmap)
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.white)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivNewMusicImg);
            }
            //解决seekBar滚动条变形问题
            Rect r = vdb.sbMusicBar.getProgressDrawable().getBounds();
            vdb.sbMusicBar.setThumb(context.getResources().getDrawable(R.drawable.shape_seek_bar_thumb));
            vdb.sbMusicBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_seek_bar_dark));
            vdb.sbMusicBar.getProgressDrawable().setBounds(r);
            Rect r2 = vdb.sbNewMusicBar.getProgressDrawable().getBounds();
            vdb.sbNewMusicBar.setThumb(context.getResources().getDrawable(R.drawable.shape_seek_bar_thumb));
            vdb.sbNewMusicBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_seek_bar_dark));
            vdb.sbNewMusicBar.getProgressDrawable().setBounds(r2);
            //vdb.sbMusicBar.setProgressTintMode(PorterDuff.Mode.SRC_ATOP);
            //loading加载颜色
            vdb.pbLoadingMusic.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.gray_36), PorterDuff.Mode.SRC_IN);
            vdb.prLoading.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.gray_36), PorterDuff.Mode.SRC_IN);
            vdb.hpvProgress.setLinearGradient(R.color.white);
            vdb.pbNewProgress.setProgressDrawable(context.getDrawable(R.drawable.shape_bg_progress_bar_black));
        } else if(rThemeId == R.id.ll_theme_white) {
            vdb.rlPlayControllerIn.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.clCurrentMusicPanel.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.clCurrentMusicList.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.clNewCurrentMusicList.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.tvDiscover.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvLocal.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.vLine.setBackgroundResource(R.drawable.shape_button_purple);
            vdb.tvTitleBar.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvListMsgName1.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvListMsgName2.setTextColor(context.getResources().getColor(R.color.gray_purple_ac));
            vdb.tvPlayAll.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvCancel.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvSingle.setTextColor(context.getResources().getColor(R.color.gray_purple_ac));
            vdb.tvCount.setTextColor(context.getResources().getColor(R.color.gray_purple_ac));
            vdb.tvMusicCount.setTextColor(context.getResources().getColor(R.color.gray_purple_ac));

            vdb.tvPlayMode.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvListSize.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvMusicName.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvSingerName.setTextColor(context.getResources().getColor(R.color.gray_purple_ac));
            vdb.tvStartTime.setTextColor(context.getResources().getColor(R.color.gray_purple_ac));
            vdb.tvAllTime.setTextColor(context.getResources().getColor(R.color.gray_purple_ac));
            vdb.tvNewPlayMode.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvNewListSize.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvNewMusicName.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvNewSingerName.setTextColor(context.getResources().getColor(R.color.gray_purple_ac));
            vdb.tvNewStartTime.setTextColor(context.getResources().getColor(R.color.gray_purple_ac));
            vdb.tvNewAllTime.setTextColor(context.getResources().getColor(R.color.gray_purple_ac));
            vdb.tvNewPlayMusicName.setTextColor(context.getResources().getColor(R.color.purple));

            vdb.clBg.setBackgroundResource(R.color.background_color_F2);
            vdb.clControllerMode.setBackgroundResource(R.color.background_color_F2);
            //vdb.rlPlayController.setBackgroundResource(R.drawable.shape_button_white_alpha_50);
            vdb.btCurrentList.setBackgroundResource(R.drawable.ic_music_list_purple);
            vdb.btNewCurrentList.setBackgroundResource(R.drawable.ic_music_list_purple);

            vdb.llAllPlay.setBackgroundResource(R.drawable.selector_white_theme_selected);
            vdb.llSearch.setBackgroundResource(R.drawable.selector_white_theme_selected);
            vdb.llSort.setBackgroundResource(R.drawable.selector_white_theme_selected);
            //vdb.llSettings.setBackgroundResource(R.drawable.selector_white_theme_selected);
            vdb.llSearchBar.setBackgroundResource(R.drawable.selector_white_theme_selected);
            vdb.llCancel.setBackgroundResource(R.drawable.selector_white_theme_selected);
            vdb.llBack.setBackgroundResource(R.drawable.selector_white_theme_selected);


            vdb.ivAllPlay.setBackgroundResource(R.drawable.ic_play_mini_purple);
            vdb.ivSearch.setBackgroundResource(R.drawable.ic_search_purple);
            vdb.ivSort.setBackgroundResource(R.drawable.ic_sort_purple);
            //vdb.ivSettings.setBackgroundResource(R.drawable.ic_settings_purple);
            vdb.ivDeleteAll.setBackgroundResource(R.drawable.ic_delete_purple);
            vdb.ivNewDeleteAll.setBackgroundResource(R.drawable.ic_delete_purple);
            vdb.ivSearchMusic.setBackgroundResource(R.drawable.ic_search_purple);
            vdb.ivBack.setBackgroundResource(R.drawable.ic_arrow_back_purple);
            vdb.ivPanelLast.setBackgroundResource(R.drawable.selector_last_purple_selected);
            vdb.ivPanelNext.setBackgroundResource(R.drawable.selector_next_purple_selected);
            vdb.ivNewPanelLast.setBackgroundResource(R.drawable.selector_last_purple_selected);
            vdb.ivNewPanelNext.setBackgroundResource(R.drawable.selector_next_purple_selected);
            vdb.ivChangeMode.setBackgroundResource(R.drawable.ic_bg_mode_purple);
            vdb.ivSettings.setBackgroundResource(R.drawable.ic_more_purple);

            vdb.ivCloseControllerMode.setBackgroundResource(R.drawable.ic_arrow_down_purple);
            vdb.ivNewChangeMode.setBackgroundResource(R.drawable.ic_bg_mode_purple);
            vdb.ivNewMyFavorite.setBackgroundResource(MainActivity.isFavorite? R.drawable.ic_favorite_purple : R.drawable.ic_favorite_empty_purple);

            vdb.ivNewMore.setBackgroundResource(R.drawable.ic_more_purple);
            vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_purple);
            vdb.ivNewCurrentList.setBackgroundResource(R.drawable.ic_music_list_purple);

            vdb.etSearchMusic.setHintTextColor(context.getResources().getColor(R.color.gray_purple_ac));
            vdb.etSearchMusic.setTextColor(context.getResources().getColor(R.color.purple));

            vdb.llMainMenuBt.setBackgroundResource(R.drawable.shape_button_black_alpha);
            vdb.ivMainMenuBt.setBackgroundResource(R.drawable.ic_menu_purple);

            if(MainActivity.playMode == 0) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_purple);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_order_play_purple);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_purple);
            } else if (MainActivity.playMode == 1) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_purple);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_random_play_purple);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_purple);
            } else if (MainActivity.playMode == 2) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_purple);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_single_play_purple);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_purple);
            }

            if(binder!=null) {
                if (binder.isPlay()) {
                    vdb.btPlay.setBackgroundResource(R.drawable.selector_pause_purple_selected);
                    vdb.btNewPlay.setBackgroundResource(R.drawable.selector_pause_purple_selected);
                    vdb.ivPanelPlay.setBackgroundResource(R.drawable.selector_pause_circle_purple_selected);
                    vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.selector_pause_circle_purple_selected);
                } else {
                    vdb.btPlay.setBackgroundResource(R.drawable.selector_play_purple_selected);
                    vdb.btNewPlay.setBackgroundResource(R.drawable.selector_play_purple_selected);
                    vdb.ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_purple_selected);
                    vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_purple_selected);
                }
            } else {
                vdb.btPlay.setBackgroundResource(R.drawable.selector_play_purple_selected);
                vdb.btNewPlay.setBackgroundResource(R.drawable.selector_play_purple_selected);
                vdb.ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_purple_selected);
                vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_purple_selected);
            }
            if(MainActivity.currentMusicImg != null || MainActivity.currentBitmap != null) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load((MainActivity.currentMusicImg != null && !MainActivity.currentMusicImg.equals(""))? MainActivity.currentMusicImg : MainActivity.currentBitmap)
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.purple)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivMusicImg);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load((MainActivity.currentMusicImg != null && !MainActivity.currentMusicImg.equals(""))? MainActivity.currentMusicImg : MainActivity.currentBitmap)
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.purple)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivNewMusicImg);
            }
            //解决seekBar滚动条变形问题
            Rect r = vdb.sbMusicBar.getProgressDrawable().getBounds();
            vdb.sbMusicBar.setThumb(context.getResources().getDrawable(R.drawable.shape_seek_bar_thumb_purple));
            vdb.sbMusicBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_seek_bar_purple));
            vdb.sbMusicBar.getProgressDrawable().setBounds(r);
            Rect r2 = vdb.sbNewMusicBar.getProgressDrawable().getBounds();
            vdb.sbNewMusicBar.setThumb(context.getResources().getDrawable(R.drawable.shape_seek_bar_thumb_purple));
            vdb.sbNewMusicBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_seek_bar_purple_dark));
            vdb.sbNewMusicBar.getProgressDrawable().setBounds(r2);
            //vdb.sbMusicBar.setProgressTintMode(PorterDuff.Mode.SRC_ATOP);
            //loading加载颜色
            vdb.pbLoadingMusic.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_IN);
            vdb.prLoading.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_IN);
            vdb.hpvProgress.setLinearGradient(R.color.purple);
            vdb.pbNewProgress.setProgressDrawable(context.getDrawable(R.drawable.shape_bg_progress_bar_purple));
        } else if(rThemeId == R.id.ll_theme_orange) {
            vdb.rlPlayControllerIn.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.clCurrentMusicPanel.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.clCurrentMusicList.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.clNewCurrentMusicList.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.pbLoadingMusic.setProgressDrawable(context.getResources().getDrawable(R.color.orange_0b));
            vdb.tvDiscover.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvLocal.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.vLine.setBackgroundResource(R.drawable.shape_button_orange);
            vdb.tvTitleBar.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvListMsgName1.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvListMsgName2.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvPlayAll.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvCancel.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvMusicCount.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvSingle.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvCount.setTextColor(context.getResources().getColor(R.color.orange_0b));

            vdb.tvPlayMode.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvListSize.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvMusicName.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvSingerName.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvStartTime.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvAllTime.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvNewPlayMode.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvNewListSize.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvNewMusicName.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvNewSingerName.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvNewStartTime.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvNewAllTime.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvNewPlayMusicName.setTextColor(context.getResources().getColor(R.color.orange_0b));

            vdb.clBg.setBackgroundResource(R.mipmap.ic_gradient_color7);
            vdb.clControllerMode.setBackgroundResource(R.mipmap.ic_gradient_color7);
            //vdb.rlPlayController.setBackgroundResource(R.drawable.shape_button_orange_alpha_50);
            vdb.btCurrentList.setBackgroundResource(R.drawable.ic_music_list_orange);
            vdb.btNewCurrentList.setBackgroundResource(R.drawable.ic_music_list_orange);

            vdb.llAllPlay.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSearch.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSort.setBackgroundResource(R.drawable.selector_normal_selected);
            //vdb.llSettings.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSearchBar.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llCancel.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llBack.setBackgroundResource(R.drawable.selector_normal_selected);

            vdb.ivAllPlay.setBackgroundResource(R.drawable.ic_play_mini_orange);
            vdb.ivSearch.setBackgroundResource(R.drawable.ic_search_orange);
            vdb.ivSort.setBackgroundResource(R.drawable.ic_sort_orange);
            //vdb.ivSettings.setBackgroundResource(R.drawable.ic_settings_orange);
            vdb.ivDeleteAll.setBackgroundResource(R.drawable.ic_delete_orange);
            vdb.ivNewDeleteAll.setBackgroundResource(R.drawable.ic_delete_orange);
            vdb.ivSearchMusic.setBackgroundResource(R.drawable.ic_search_orange);
            vdb.ivBack.setBackgroundResource(R.drawable.ic_arrow_back_orange);
            vdb.ivPanelLast.setBackgroundResource(R.drawable.selector_last_orange_selected);
            vdb.ivPanelNext.setBackgroundResource(R.drawable.selector_next_orange_selected);
            vdb.ivNewPanelLast.setBackgroundResource(R.drawable.selector_last_orange_selected);
            vdb.ivNewPanelNext.setBackgroundResource(R.drawable.selector_next_orange_selected);
            vdb.ivChangeMode.setBackgroundResource(R.drawable.ic_bg_mode_orange);
            vdb.ivSettings.setBackgroundResource(R.drawable.ic_more_orange);

            vdb.ivCloseControllerMode.setBackgroundResource(R.drawable.ic_arrow_down_orange);
            vdb.ivNewChangeMode.setBackgroundResource(R.drawable.ic_bg_mode_orange);
            vdb.ivNewMyFavorite.setBackgroundResource(MainActivity.isFavorite? R.drawable.ic_favorite_orange : R.drawable.ic_favorite_empty_orange);

            vdb.ivNewMore.setBackgroundResource(R.drawable.ic_more_orange);
            vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_orange);
            vdb.ivNewCurrentList.setBackgroundResource(R.drawable.ic_music_list_orange);

            vdb.etSearchMusic.setHintTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.etSearchMusic.setTextColor(context.getResources().getColor(R.color.orange_0b));

            vdb.llMainMenuBt.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.ivMainMenuBt.setBackgroundResource(R.drawable.ic_menu_orange);

            if(MainActivity.playMode == 0) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_orange);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_order_play_orange);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_orange);
            } else if (MainActivity.playMode == 1) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_orange);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_random_play_orange);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_orange);
            } else if (MainActivity.playMode == 2) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_orange);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_single_play_orange);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_orange);
            }

            if(binder!=null) {
                if (binder.isPlay()) {
                    vdb.btPlay.setBackgroundResource(R.drawable.selector_pause_orange_selected);
                    vdb.btNewPlay.setBackgroundResource(R.drawable.selector_pause_orange_selected);
                    vdb.ivPanelPlay.setBackgroundResource(R.drawable.selector_pause_circle_orange_selected);
                    vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.selector_pause_circle_orange_selected);
                } else {
                    vdb.btPlay.setBackgroundResource(R.drawable.selector_play_orange_selected);
                    vdb.btNewPlay.setBackgroundResource(R.drawable.selector_play_orange_selected);
                    vdb.ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_orange_selected);
                    vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_orange_selected);
                }
            } else {
                vdb.btPlay.setBackgroundResource(R.drawable.selector_play_orange_selected);
                vdb.btNewPlay.setBackgroundResource(R.drawable.selector_play_orange_selected);
                vdb.ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_orange_selected);
                vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_orange_selected);
            }
            if(MainActivity.currentMusicImg != null || MainActivity.currentBitmap != null) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load((MainActivity.currentMusicImg != null && !MainActivity.currentMusicImg.equals(""))? MainActivity.currentMusicImg : MainActivity.currentBitmap)
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.orange_0b)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivMusicImg);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load((MainActivity.currentMusicImg != null && !MainActivity.currentMusicImg.equals(""))? MainActivity.currentMusicImg : MainActivity.currentBitmap)
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.orange_0b)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivNewMusicImg);
            }
            //解决seekBar滚动条变形问题
            Rect r = vdb.sbMusicBar.getProgressDrawable().getBounds();
            vdb.sbMusicBar.setThumb(context.getResources().getDrawable(R.drawable.shape_seek_bar_thumb_orange));
            vdb.sbMusicBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_seek_bar_orange));
            vdb.sbMusicBar.getProgressDrawable().setBounds(r);
            Rect r2 = vdb.sbNewMusicBar.getProgressDrawable().getBounds();
            vdb.sbNewMusicBar.setThumb(context.getResources().getDrawable(R.drawable.shape_seek_bar_thumb_orange));
            vdb.sbNewMusicBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_seek_bar_orange));
            vdb.sbNewMusicBar.getProgressDrawable().setBounds(r2);
            //vdb.sbMusicBar.setProgressTintMode(PorterDuff.Mode.SRC_ATOP);
            //loading加载颜色
            vdb.pbLoadingMusic.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.orange_f4), PorterDuff.Mode.SRC_IN);
            vdb.prLoading.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.orange_f4), PorterDuff.Mode.SRC_IN);
            vdb.hpvProgress.setLinearGradient(R.color.orange_0b);
            vdb.pbNewProgress.setProgressDrawable(context.getDrawable(R.drawable.shape_bg_progress_bar_orange));
        } else if(rThemeId == R.id.ll_theme_light) {
            vdb.rlPlayControllerIn.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.clCurrentMusicPanel.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.clCurrentMusicList.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.clNewCurrentMusicList.setBackgroundResource(R.drawable.shape_button_white_3);
            vdb.tvDiscover.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvLocal.setTextColor(context.getResources().getColor(R.color.white));
            vdb.vLine.setBackgroundResource(R.drawable.shape_button_white);
            vdb.tvTitleBar.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvListMsgName1.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvPlayAll.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvCancel.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvListMsgName2.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvSingle.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvCount.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvMusicCount.setTextColor(context.getResources().getColor(R.color.light_ff));

            vdb.tvPlayMode.setTextColor(context.getResources().getColor(R.color.light_b5));
            vdb.tvListSize.setTextColor(context.getResources().getColor(R.color.light_b5));
            vdb.tvMusicName.setTextColor(context.getResources().getColor(R.color.light_b5));
            vdb.tvSingerName.setTextColor(context.getResources().getColor(R.color.light_b5));
            vdb.tvStartTime.setTextColor(context.getResources().getColor(R.color.light_b5));
            vdb.tvAllTime.setTextColor(context.getResources().getColor(R.color.light_b5));
            vdb.tvNewPlayMode.setTextColor(context.getResources().getColor(R.color.light_b5));
            vdb.tvNewListSize.setTextColor(context.getResources().getColor(R.color.light_b5));
            vdb.tvNewMusicName.setTextColor(context.getResources().getColor(R.color.light_b5));
            vdb.tvNewSingerName.setTextColor(context.getResources().getColor(R.color.light_b5));
            vdb.tvNewStartTime.setTextColor(context.getResources().getColor(R.color.light_b5));
            vdb.tvNewAllTime.setTextColor(context.getResources().getColor(R.color.light_b5));
            vdb.tvNewPlayMusicName.setTextColor(context.getResources().getColor(R.color.light_b5));

            vdb.clBg.setBackgroundResource(R.mipmap.ic_gradient_color4);
            vdb.clControllerMode.setBackgroundResource(R.mipmap.ic_gradient_color4);
            //vdb.rlPlayController.setBackgroundResource(R.drawable.shape_button_light_alpha_50);
            vdb.btCurrentList.setBackgroundResource(R.drawable.ic_music_list_light);
            vdb.btNewCurrentList.setBackgroundResource(R.drawable.ic_music_list_light);

            vdb.llAllPlay.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSearch.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSort.setBackgroundResource(R.drawable.selector_normal_selected);
            //vdb.llSettings.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSearchBar.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llCancel.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llBack.setBackgroundResource(R.drawable.selector_normal_selected);

            vdb.ivAllPlay.setBackgroundResource(R.drawable.ic_play_mini_light);
            vdb.ivSearch.setBackgroundResource(R.drawable.ic_search_light);
            vdb.ivSort.setBackgroundResource(R.drawable.ic_sort_light);
            //vdb.ivSettings.setBackgroundResource(R.drawable.ic_settings_light);
            vdb.ivDeleteAll.setBackgroundResource(R.drawable.ic_delete_light);
            vdb.ivNewDeleteAll.setBackgroundResource(R.drawable.ic_delete_light);
            vdb.ivSearchMusic.setBackgroundResource(R.drawable.ic_search_light);
            vdb.ivBack.setBackgroundResource(R.drawable.ic_arrow_back_light);
            vdb.ivPanelLast.setBackgroundResource(R.drawable.selector_last_light_selected);
            vdb.ivPanelNext.setBackgroundResource(R.drawable.selector_next_light_selected);
            vdb.ivNewPanelLast.setBackgroundResource(R.drawable.selector_last_light_selected);
            vdb.ivNewPanelNext.setBackgroundResource(R.drawable.selector_next_light_selected);
            vdb.ivChangeMode.setBackgroundResource(R.drawable.ic_bg_mode_light);
            vdb.ivSettings.setBackgroundResource(R.drawable.ic_more_light);
            vdb.etSearchMusic.setHintTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.etSearchMusic.setTextColor(context.getResources().getColor(R.color.light_ff));

            vdb.ivCloseControllerMode.setBackgroundResource(R.drawable.ic_arrow_down_light);
            vdb.ivNewChangeMode.setBackgroundResource(R.drawable.ic_bg_mode_light);
            vdb.ivNewMyFavorite.setBackgroundResource(MainActivity.isFavorite? R.drawable.ic_favorite_light : R.drawable.ic_favorite_empty_light);

            vdb.ivNewMore.setBackgroundResource(R.drawable.ic_more_light);
            vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_light);
            vdb.ivNewCurrentList.setBackgroundResource(R.drawable.ic_music_list_light);

            vdb.llMainMenuBt.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.ivMainMenuBt.setBackgroundResource(R.drawable.ic_menu);

            if(MainActivity.playMode == 0) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_light);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_order_play_light);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_light);
            } else if (MainActivity.playMode == 1) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_light);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_random_play_light);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_light);
            } else if (MainActivity.playMode == 2) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_light);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_single_play_light);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_light);
            }

            if(binder!=null) {
                if (binder.isPlay()) {
                    vdb.btPlay.setBackgroundResource(R.drawable.selector_pause_light_selected);
                    vdb.btNewPlay.setBackgroundResource(R.drawable.selector_pause_light_selected);
                    vdb.ivPanelPlay.setBackgroundResource(R.drawable.selector_pause_circle_light_selected);
                    vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.selector_pause_circle_light_selected);
                } else {
                    vdb.btPlay.setBackgroundResource(R.drawable.selector_play_light_selected);
                    vdb.btNewPlay.setBackgroundResource(R.drawable.selector_play_light_selected);
                    vdb.ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_light_selected);
                    vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_light_selected);
                }
            } else {
                vdb.btPlay.setBackgroundResource(R.drawable.selector_play_light_selected);
                vdb.btNewPlay.setBackgroundResource(R.drawable.selector_play_light_selected);
                vdb.ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_light_selected);
                vdb.ivNewPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_light_selected);
            }
            if(MainActivity.currentMusicImg != null || MainActivity.currentBitmap != null) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load((MainActivity.currentMusicImg != null && !MainActivity.currentMusicImg.equals(""))? MainActivity.currentMusicImg : MainActivity.currentBitmap)
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.light_b5)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivMusicImg);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load((MainActivity.currentMusicImg != null && !MainActivity.currentMusicImg.equals(""))? MainActivity.currentMusicImg : MainActivity.currentBitmap)
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.light_b5)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivNewMusicImg);
            }
            //解决seekBar滚动条变形问题
            Rect r = vdb.sbMusicBar.getProgressDrawable().getBounds();
            vdb.sbMusicBar.setThumb(context.getResources().getDrawable(R.drawable.shape_seek_bar_thumb_light));
            vdb.sbMusicBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_seek_bar_light));
            vdb.sbMusicBar.getProgressDrawable().setBounds(r);
            Rect r2 = vdb.sbNewMusicBar.getProgressDrawable().getBounds();
            vdb.sbNewMusicBar.setThumb(context.getResources().getDrawable(R.drawable.shape_seek_bar_thumb_light));
            vdb.sbNewMusicBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_seek_bar_light));
            vdb.sbNewMusicBar.getProgressDrawable().setBounds(r2);
            //vdb.sbMusicBar.setProgressTintMode(PorterDuff.Mode.SRC_ATOP);
            //loading加载颜色
            vdb.pbLoadingMusic.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.light_b5), PorterDuff.Mode.SRC_IN);
            vdb.prLoading.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.light_b5), PorterDuff.Mode.SRC_IN);
            vdb.hpvProgress.setLinearGradient(R.color.light_b5);
            vdb.pbNewProgress.setProgressDrawable(context.getDrawable(R.drawable.shape_bg_progress_bar_light));
        }

    }

    /**
     * MainActivity主题改变 - 播放按钮颜色ui
     * */
    public void playButtonTheme(int rThemeId, ActivityMainBinding vdb) {
        if(rThemeId!=0) {
            if(rThemeId == R.id.ll_theme_normal) {
                vdb.btPlay.setBackgroundResource(R.drawable.selector_play_black_selected);
            } else if(rThemeId == R.id.ll_theme_blue) {
                vdb.btPlay.setBackgroundResource(R.drawable.ic_play_2_blue);
            } else if(rThemeId == R.id.ll_theme_dark) {
                vdb.btPlay.setBackgroundResource(R.drawable.ic_play_2_white);
            } else if(rThemeId == R.id.ll_theme_white) {
                vdb.btPlay.setBackgroundResource(R.drawable.selector_play_purple_selected);
            } else if(rThemeId == R.id.ll_theme_orange) {
                vdb.btPlay.setBackgroundResource(R.drawable.selector_play_orange_selected);
            } else if(rThemeId == R.id.ll_theme_light) {
                vdb.btPlay.setBackgroundResource(R.drawable.selector_play_light_selected);
            }
        }
    }

    /**
     * MainActivity主题改变 - 播放按钮状态ui
     * */
    public void playButtonStatusTheme(int rThemeId, ActivityMainBinding vdb, boolean b) {
        if(rThemeId!=0) {
            Log.i("ABMusicPlayer","rThemeId: " + rThemeId);
            if(rThemeId == R.id.ll_theme_normal) {
                vdb.btPlay.setBackgroundResource(b ? R.drawable.selector_play_black_selected : R.drawable.selector_pause_black_selected);
                vdb.btNewPlay.setBackgroundResource(b ? R.drawable.selector_play_black_selected : R.drawable.selector_pause_black_selected);
                vdb.ivPanelPlay.setBackgroundResource(b ? R.drawable.selector_play_circle_black_selected : R.drawable.selector_pause_circle_black_selected);
                vdb.ivNewPanelPlay.setBackgroundResource(b ? R.drawable.selector_play_circle_black_33_selected : R.drawable.selector_pause_circle_black_33_selected);
            } else if(rThemeId == R.id.ll_theme_blue) {
                vdb.btPlay.setBackgroundResource(b ? R.drawable.selector_play_blue_selected : R.drawable.selector_pause_blue_selected);
                vdb.btNewPlay.setBackgroundResource(b ? R.drawable.selector_play_blue_selected : R.drawable.selector_pause_blue_selected);
                vdb.ivPanelPlay.setBackgroundResource(b ? R.drawable.selector_play_circle_blue_selected : R.drawable.selector_pause_circle_blue_selected);
                vdb.ivNewPanelPlay.setBackgroundResource(b ? R.drawable.ic_play_circle_white : R.drawable.ic_pause_circle_white);
            } else if(rThemeId == R.id.ll_theme_dark) {
                vdb.btPlay.setBackgroundResource(b ? R.drawable.ic_play_2_white : R.drawable.ic_pause_2_white);
                vdb.btNewPlay.setBackgroundResource(b ? R.drawable.ic_play_2_white : R.drawable.ic_pause_2_white);
                vdb.ivPanelPlay.setBackgroundResource(b ? R.drawable.ic_play_circle_white : R.drawable.ic_pause_circle_white);
                vdb.ivNewPanelPlay.setBackgroundResource(b ? R.drawable.ic_play_circle_white : R.drawable.ic_pause_circle_white);
            } else if(rThemeId == R.id.ll_theme_white) {
                vdb.btPlay.setBackgroundResource(b ? R.drawable.selector_play_purple_selected : R.drawable.selector_pause_purple_selected);
                vdb.btNewPlay.setBackgroundResource(b ? R.drawable.selector_play_purple_selected : R.drawable.selector_pause_purple_selected);
                vdb.ivPanelPlay.setBackgroundResource(b ? R.drawable.selector_play_circle_purple_selected : R.drawable.selector_pause_circle_purple_selected);
                vdb.ivNewPanelPlay.setBackgroundResource(b ? R.drawable.selector_play_circle_purple_selected : R.drawable.selector_pause_circle_purple_selected);
            } else if(rThemeId == R.id.ll_theme_orange) {
                vdb.btPlay.setBackgroundResource(b ? R.drawable.selector_play_orange_selected : R.drawable.selector_pause_orange_selected);
                vdb.btNewPlay.setBackgroundResource(b ? R.drawable.selector_play_orange_selected : R.drawable.selector_pause_orange_selected);
                vdb.ivPanelPlay.setBackgroundResource(b ? R.drawable.selector_play_circle_orange_selected : R.drawable.selector_pause_circle_orange_selected);
                vdb.ivNewPanelPlay.setBackgroundResource(b ? R.drawable.selector_play_circle_orange_selected : R.drawable.selector_pause_circle_orange_selected);
            } else if(rThemeId == R.id.ll_theme_light) {
                vdb.btPlay.setBackgroundResource(b ? R.drawable.selector_play_light_selected : R.drawable.selector_pause_light_selected);
                vdb.btNewPlay.setBackgroundResource(b ? R.drawable.selector_play_light_selected : R.drawable.selector_pause_light_selected);
                vdb.ivPanelPlay.setBackgroundResource(b ? R.drawable.selector_play_circle_light_selected : R.drawable.selector_pause_circle_light_selected);
                vdb.ivNewPanelPlay.setBackgroundResource(b ? R.drawable.selector_play_circle_light_selected : R.drawable.selector_pause_circle_light_selected);
            } else {
                vdb.btPlay.setBackgroundResource(b ? R.drawable.selector_play_black_selected : R.drawable.selector_pause_black_selected);
                vdb.btNewPlay.setBackgroundResource(b ? R.drawable.selector_play_black_selected : R.drawable.selector_pause_black_selected);
                vdb.ivPanelPlay.setBackgroundResource(b ? R.drawable.selector_play_circle_black_selected : R.drawable.selector_pause_circle_black_selected);
                vdb.ivNewPanelPlay.setBackgroundResource(b ? R.drawable.selector_play_circle_black_33_selected : R.drawable.selector_pause_circle_black_33_selected);
            }
        } else {
            vdb.btPlay.setBackgroundResource(b ? R.drawable.selector_play_black_selected : R.drawable.selector_pause_black_selected);
            vdb.btNewPlay.setBackgroundResource(b ? R.drawable.selector_play_black_selected : R.drawable.selector_pause_black_selected);
            vdb.ivPanelPlay.setBackgroundResource(b ? R.drawable.selector_play_circle_black_selected : R.drawable.selector_pause_circle_black_selected);
            vdb.ivNewPanelPlay.setBackgroundResource(b ? R.drawable.selector_play_circle_black_33_selected : R.drawable.selector_pause_circle_black_33_selected);
        }
    }

    /**
     * MainActivity主题改变 - 播放控制器圆型图片ui
     * */
    public void musicBarMusicImgTheme(Context context, int rThemeId, ActivityMainBinding vdb, Music music) {
        if(requestOptions == null) {
            requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            //requestOptions.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL); //关键代码，加载原始大小
            requestOptions.format(DecodeFormat.PREFER_RGB_565); //设置为这种格式去掉透明度通道，可以减少内存占有
        }

        if(rThemeId!=0) {
            if(rThemeId == R.id.ll_theme_normal) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(music.isLocal?
                                (null != music.musicImgByte?
                                        BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                        )
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.light_f9)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivMusicImg);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(music.isLocal?
                                (null != music.musicImgByte?
                                        BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                        )
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.light_f9)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivNewMusicImg);
            } else if(rThemeId == R.id.ll_theme_blue) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(music.isLocal?
                                (null != music.musicImgByte?
                                        BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                        )
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.blue_0E)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivMusicImg);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(music.isLocal?
                                (null != music.musicImgByte?
                                        BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                        )
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.blue_0E)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivNewMusicImg);
            } else if(rThemeId == R.id.ll_theme_dark) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(music.isLocal?
                                (null != music.musicImgByte?
                                        BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                        )
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.white)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivMusicImg);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(music.isLocal?
                                (null != music.musicImgByte?
                                        BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                        )
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.white)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivNewMusicImg);
            } else if(rThemeId == R.id.ll_theme_white) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(music.isLocal?
                                (null != music.musicImgByte?
                                        BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                        )
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.purple_light)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivMusicImg);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(music.isLocal?
                                (null != music.musicImgByte?
                                        BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                        )
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.purple_light)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivNewMusicImg);
            } else if(rThemeId == R.id.ll_theme_orange) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(music.isLocal?
                                (null != music.musicImgByte?
                                        BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                        )
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.orange_0b)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivMusicImg);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(music.isLocal?
                                (null != music.musicImgByte?
                                        BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                        )
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.orange_0b)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivNewMusicImg);
            } else if(rThemeId == R.id.ll_theme_light) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(music.isLocal?
                                (null != music.musicImgByte?
                                        BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                        )
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.light_b5)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivMusicImg);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(music.isLocal?
                                (null != music.musicImgByte?
                                        BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                        )
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.light_b5)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivNewMusicImg);
            } else {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(music.isLocal?
                                (null != music.musicImgByte?
                                        BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                        )
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.light_f9)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivMusicImg);
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(music.isLocal?
                                (null != music.musicImgByte?
                                        BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                        )
                        .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.light_f9)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(vdb.ivNewMusicImg);
            }
        } else {
            Glide.with(context)
                    .load(music.isLocal?
                            (null != music.musicImgByte?
                                    BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                    )
                    .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.light_f9)))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(vdb.ivMusicImg);
            Glide.with(context)
                    .load(music.isLocal?
                            (null != music.musicImgByte?
                                    BitmapFactory.decodeByteArray(music.musicImgByte, 0, music.musicImgByte.length) : R.drawable.ic_music_default) : music.getMusicImg()
                    )
                    .transform(new CropCircleWithBorderTransformation(5, context.getResources().getColor(R.color.light_f9)))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(vdb.ivNewMusicImg);
        }
    }

    /**
     * MainActivity主题改变 - 收藏按钮ui
     * */
    public void musicBarMusicFavoriteTheme(Context context, int rThemeId, ActivityMainBinding vdb, boolean isFavorite) {

        if(rThemeId!=0) {
            if(rThemeId == R.id.ll_theme_normal) {
                vdb.ivNewMyFavorite.setBackgroundResource(isFavorite? R.drawable.ic_favorite_black_33 : R.drawable.ic_favorite_empty_black_33);
            } else if(rThemeId == R.id.ll_theme_blue) {
                vdb.ivNewMyFavorite.setBackgroundResource(isFavorite? R.drawable.ic_favorite : R.drawable.ic_favorite_empty);
            } else if(rThemeId == R.id.ll_theme_dark) {
                vdb.ivNewMyFavorite.setBackgroundResource(isFavorite? R.drawable.ic_favorite : R.drawable.ic_favorite_empty);
            } else if(rThemeId == R.id.ll_theme_white) {
                vdb.ivNewMyFavorite.setBackgroundResource(isFavorite? R.drawable.ic_favorite_purple : R.drawable.ic_favorite_empty_purple);
            } else if(rThemeId == R.id.ll_theme_orange) {
                vdb.ivNewMyFavorite.setBackgroundResource(isFavorite? R.drawable.ic_favorite_orange : R.drawable.ic_favorite_empty_orange);
            } else if(rThemeId == R.id.ll_theme_light) {
                vdb.ivNewMyFavorite.setBackgroundResource(isFavorite? R.drawable.ic_favorite_light : R.drawable.ic_favorite_empty_light);
            } else {
                vdb.ivNewMyFavorite.setBackgroundResource(isFavorite? R.drawable.ic_favorite_black_33 : R.drawable.ic_favorite_empty_black_33);
            }
        } else {
            vdb.ivNewMyFavorite.setBackgroundResource(isFavorite? R.drawable.ic_favorite_black_33 : R.drawable.ic_favorite_empty_black_33);
        }
    }


    /**
     * MainActivity主题改变 - 系统菜单ui
     * */
    public void menuPopupWindowTheme(Context context, int rThemeId, PopupWindow menuPopupWindow, DialogMainMenuBinding mainMenuBinding) {
        if(rThemeId != 0) {
            if(rThemeId == R.id.ll_theme_normal) {
                menuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_normal));
                mainMenuBinding.tvSettings.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvCharacter.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvBackground.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvLocalMusic.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvTimeTasks.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine3.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine4.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_blue) {
                menuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_blue));
                mainMenuBinding.tvSettings.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvCharacter.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvBackground.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvLocalMusic.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvTimeTasks.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine3.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine4.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_dark) {
                menuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu));
                mainMenuBinding.tvSettings.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvCharacter.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvBackground.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvLocalMusic.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvTimeTasks.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine3.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine4.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_white) {
                menuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_white));
                mainMenuBinding.tvSettings.setTextColor(context.getResources().getColor(R.color.purple));
                mainMenuBinding.tvCharacter.setTextColor(context.getResources().getColor(R.color.purple));
                mainMenuBinding.tvBackground.setTextColor(context.getResources().getColor(R.color.purple));
                mainMenuBinding.tvLocalMusic.setTextColor(context.getResources().getColor(R.color.purple));
                mainMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.purple));
                mainMenuBinding.tvTimeTasks.setTextColor(context.getResources().getColor(R.color.purple));
                mainMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.purple));
                mainMenuBinding.vLine3.setBackgroundColor(context.getResources().getColor(R.color.purple));
                mainMenuBinding.vLine4.setBackgroundColor(context.getResources().getColor(R.color.purple));
            } else if(rThemeId == R.id.ll_theme_orange) {
                menuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_orange));
                mainMenuBinding.tvSettings.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvCharacter.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvBackground.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvLocalMusic.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvTimeTasks.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine3.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine4.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_light) {
                menuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_light));
                mainMenuBinding.tvSettings.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvCharacter.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvBackground.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvLocalMusic.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvTimeTasks.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine3.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine4.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else {
                menuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_normal));
                mainMenuBinding.tvSettings.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvCharacter.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvBackground.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvLocalMusic.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.tvTimeTasks.setTextColor(context.getResources().getColor(R.color.white));
                mainMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine3.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                mainMenuBinding.vLine4.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            }
        } else {
            menuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_normal));
            mainMenuBinding.tvSettings.setTextColor(context.getResources().getColor(R.color.white));
            mainMenuBinding.tvCharacter.setTextColor(context.getResources().getColor(R.color.white));
            mainMenuBinding.tvBackground.setTextColor(context.getResources().getColor(R.color.white));
            mainMenuBinding.tvLocalMusic.setTextColor(context.getResources().getColor(R.color.white));
            mainMenuBinding.tvTimeTasks.setTextColor(context.getResources().getColor(R.color.white));
            mainMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            mainMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            mainMenuBinding.vLine3.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            mainMenuBinding.vLine4.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
        }
    }

    /**
     * MainActivity主题改变 - 角色菜单ui
     * */
    public void characterPopupWindowTheme(Context context, int rThemeId, PopupWindow characterPopupWindow, DialogCharacterMenuBinding characterMenuBinding) {
        if(rThemeId!=0) {
            if(rThemeId == R.id.ll_theme_normal) {
                characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_normal));
                characterMenuBinding.tvCharacterByKeke.setTextColor(context.getResources().getColor(R.color.white));
                characterMenuBinding.tvCharacterByKanon.setTextColor(context.getResources().getColor(R.color.white));
                characterMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_blue) {
                characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_blue));
                characterMenuBinding.tvCharacterByKeke.setTextColor(context.getResources().getColor(R.color.white));
                characterMenuBinding.tvCharacterByKanon.setTextColor(context.getResources().getColor(R.color.white));
                characterMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_dark) {
                characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu));
                characterMenuBinding.tvCharacterByKeke.setTextColor(context.getResources().getColor(R.color.white));
                characterMenuBinding.tvCharacterByKanon.setTextColor(context.getResources().getColor(R.color.white));
                characterMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_white) {
                characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_white));
                characterMenuBinding.tvCharacterByKeke.setTextColor(context.getResources().getColor(R.color.purple));
                characterMenuBinding.tvCharacterByKanon.setTextColor(context.getResources().getColor(R.color.purple));
                characterMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.purple));
            } else if(rThemeId == R.id.ll_theme_orange) {
                characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_orange));
                characterMenuBinding.tvCharacterByKeke.setTextColor(context.getResources().getColor(R.color.white));
                characterMenuBinding.tvCharacterByKanon.setTextColor(context.getResources().getColor(R.color.white));
                characterMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_light) {
                characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_light));
                characterMenuBinding.tvCharacterByKeke.setTextColor(context.getResources().getColor(R.color.white));
                characterMenuBinding.tvCharacterByKanon.setTextColor(context.getResources().getColor(R.color.white));
                characterMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else {
                characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_normal));
                characterMenuBinding.tvCharacterByKeke.setTextColor(context.getResources().getColor(R.color.white));
                characterMenuBinding.tvCharacterByKanon.setTextColor(context.getResources().getColor(R.color.white));
                characterMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            }
        } else {
            characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_normal));
            characterMenuBinding.tvCharacterByKeke.setTextColor(context.getResources().getColor(R.color.white));
            characterMenuBinding.tvCharacterByKanon.setTextColor(context.getResources().getColor(R.color.white));
            characterMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
        }
    }

    /**
     * MainActivity主题改变 - 音乐控制ui菜单
     * */
    public void changeModePopupWindowTheme(Context context, int rThemeId, PopupWindow characterPopupWindow, DialogChangeModeMenuBinding changeModeMenuBinding) {
        if(rThemeId!=0) {
            if(rThemeId == R.id.ll_theme_normal) {
                characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_normal));
                changeModeMenuBinding.tvControllerMode.setTextColor(context.getResources().getColor(R.color.white));
                changeModeMenuBinding.tvBgMode.setTextColor(context.getResources().getColor(R.color.white));
                changeModeMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_blue) {
                characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_blue));
                changeModeMenuBinding.tvControllerMode.setTextColor(context.getResources().getColor(R.color.white));
                changeModeMenuBinding.tvBgMode.setTextColor(context.getResources().getColor(R.color.white));
                changeModeMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_dark) {
                characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu));
                changeModeMenuBinding.tvControllerMode.setTextColor(context.getResources().getColor(R.color.white));
                changeModeMenuBinding.tvBgMode.setTextColor(context.getResources().getColor(R.color.white));
                changeModeMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_white) {
                characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_gray));
                changeModeMenuBinding.tvControllerMode.setTextColor(context.getResources().getColor(R.color.purple));
                changeModeMenuBinding.tvBgMode.setTextColor(context.getResources().getColor(R.color.purple));
                changeModeMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.purple));
            } else if(rThemeId == R.id.ll_theme_orange) {
                characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_orange));
                changeModeMenuBinding.tvControllerMode.setTextColor(context.getResources().getColor(R.color.white));
                changeModeMenuBinding.tvBgMode.setTextColor(context.getResources().getColor(R.color.white));
                changeModeMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_light) {
                characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_light_7e));
                changeModeMenuBinding.tvControllerMode.setTextColor(context.getResources().getColor(R.color.white));
                changeModeMenuBinding.tvBgMode.setTextColor(context.getResources().getColor(R.color.white));
                changeModeMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else {
                characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_normal));
                changeModeMenuBinding.tvControllerMode.setTextColor(context.getResources().getColor(R.color.white));
                changeModeMenuBinding.tvBgMode.setTextColor(context.getResources().getColor(R.color.white));
                changeModeMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            }
        } else {
            characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_normal));
            changeModeMenuBinding.tvControllerMode.setTextColor(context.getResources().getColor(R.color.white));
            changeModeMenuBinding.tvBgMode.setTextColor(context.getResources().getColor(R.color.white));
            changeModeMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
        }
    }

    /**
     * MainActivity主题改变 - 面板ui菜单
     * */

    public void panelMenuPopupWindowTheme(Context context, int rThemeId, PopupWindow characterPopupWindow, DialogPanelMoreMenuBinding panelMoreMenuBinding) {
        if(rThemeId!=0) {
            if(rThemeId == R.id.ll_theme_normal) {
                panelMoreMenuBinding.llMoreSetDialog.setBackgroundResource(R.drawable.shape_button_menu_normal);
                panelMoreMenuBinding.ivLyricSetTitle.setBackgroundResource(R.drawable.ic_lyric_settings);
                panelMoreMenuBinding.tvLyricSetTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_default));
                panelMoreMenuBinding.sbLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue45.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbSingleLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_default));
                panelMoreMenuBinding.sbSingleLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvSingleValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue45.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbDetailLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_default));
                panelMoreMenuBinding.sbDetailLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvDetailValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue45.setTextColor(context.getResources().getColor(R.color.white));
            } else if(rThemeId == R.id.ll_theme_blue) {
                panelMoreMenuBinding.llMoreSetDialog.setBackgroundResource(R.drawable.shape_button_menu_blue);
                panelMoreMenuBinding.ivLyricSetTitle.setBackgroundResource(R.drawable.ic_lyric_settings);
                panelMoreMenuBinding.tvLyricSetTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_blue));
                panelMoreMenuBinding.sbLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue45.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbSingleLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_blue));
                panelMoreMenuBinding.sbSingleLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvSingleValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue45.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbDetailLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_blue));
                panelMoreMenuBinding.sbDetailLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvDetailValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue45.setTextColor(context.getResources().getColor(R.color.white));
            } else if(rThemeId == R.id.ll_theme_dark) {
                panelMoreMenuBinding.llMoreSetDialog.setBackgroundResource(R.drawable.shape_button_menu);
                panelMoreMenuBinding.ivLyricSetTitle.setBackgroundResource(R.drawable.ic_lyric_settings);
                panelMoreMenuBinding.tvLyricSetTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_white));
                panelMoreMenuBinding.sbLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_dark));
                panelMoreMenuBinding.tvValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue45.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbSingleLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_white));
                panelMoreMenuBinding.sbSingleLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_dark));
                panelMoreMenuBinding.tvSingleValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue45.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbDetailLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_white));
                panelMoreMenuBinding.sbDetailLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_dark));
                panelMoreMenuBinding.tvDetailValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue45.setTextColor(context.getResources().getColor(R.color.white));
            } else if(rThemeId == R.id.ll_theme_white) {
                panelMoreMenuBinding.llMoreSetDialog.setBackgroundResource(R.drawable.shape_button_menu_gray);
                panelMoreMenuBinding.ivLyricSetTitle.setBackgroundResource(R.drawable.ic_lyric_settings_purple);
                panelMoreMenuBinding.tvLyricSetTitle.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.tvLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.sbLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_purple));
                panelMoreMenuBinding.sbLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvValue25.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.tvValue30.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.tvValue35.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.tvValue40.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.tvValue45.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.tvSingleLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.sbSingleLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_purple));
                panelMoreMenuBinding.sbSingleLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvSingleValue25.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.tvSingleValue30.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.tvSingleValue35.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.tvSingleValue40.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.tvSingleValue45.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.tvDetailLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.sbDetailLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_purple));
                panelMoreMenuBinding.sbDetailLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvDetailValue25.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.tvDetailValue30.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.tvDetailValue35.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.tvDetailValue40.setTextColor(context.getResources().getColor(R.color.purple));
                panelMoreMenuBinding.tvDetailValue45.setTextColor(context.getResources().getColor(R.color.purple));
            } else if(rThemeId == R.id.ll_theme_orange) {
                panelMoreMenuBinding.llMoreSetDialog.setBackgroundResource(R.drawable.shape_button_menu_orange);
                panelMoreMenuBinding.ivLyricSetTitle.setBackgroundResource(R.drawable.ic_lyric_settings);
                panelMoreMenuBinding.tvLyricSetTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_orange));
                panelMoreMenuBinding.sbLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue45.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbSingleLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_orange));
                panelMoreMenuBinding.sbSingleLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvSingleValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue45.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbDetailLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_orange));
                panelMoreMenuBinding.sbDetailLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvDetailValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue45.setTextColor(context.getResources().getColor(R.color.white));
            } else if(rThemeId == R.id.ll_theme_light) {
                panelMoreMenuBinding.llMoreSetDialog.setBackgroundResource(R.drawable.shape_button_menu_light_7e);
                panelMoreMenuBinding.ivLyricSetTitle.setBackgroundResource(R.drawable.ic_lyric_settings);
                panelMoreMenuBinding.tvLyricSetTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_light));
                panelMoreMenuBinding.sbLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue45.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbSingleLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_light));
                panelMoreMenuBinding.sbSingleLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvSingleValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue45.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbDetailLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_light));
                panelMoreMenuBinding.sbDetailLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvDetailValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue45.setTextColor(context.getResources().getColor(R.color.white));
            } else {
                panelMoreMenuBinding.llMoreSetDialog.setBackgroundResource(R.drawable.shape_button_menu_normal);
                panelMoreMenuBinding.ivLyricSetTitle.setBackgroundResource(R.drawable.ic_lyric_settings);
                panelMoreMenuBinding.tvLyricSetTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_default));
                panelMoreMenuBinding.sbLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvValue45.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbSingleLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_default));
                panelMoreMenuBinding.sbSingleLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvSingleValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvSingleValue45.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.sbDetailLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_default));
                panelMoreMenuBinding.sbDetailLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
                panelMoreMenuBinding.tvDetailValue25.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue30.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue35.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue40.setTextColor(context.getResources().getColor(R.color.white));
                panelMoreMenuBinding.tvDetailValue45.setTextColor(context.getResources().getColor(R.color.white));
            }
        } else {
            panelMoreMenuBinding.llMoreSetDialog.setBackgroundResource(R.drawable.shape_button_menu_normal);
            panelMoreMenuBinding.ivLyricSetTitle.setBackgroundResource(R.drawable.ic_lyric_settings);
            panelMoreMenuBinding.tvLyricSetTitle.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.tvLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.sbLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_default));
            panelMoreMenuBinding.tvValue25.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.tvValue30.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.tvValue35.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.tvValue40.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.tvValue45.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.tvSingleLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.sbSingleLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_default));
            panelMoreMenuBinding.sbSingleLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
            panelMoreMenuBinding.tvSingleValue25.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.tvSingleValue30.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.tvSingleValue35.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.tvSingleValue40.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.tvSingleValue45.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.tvDetailLyricSizeTitle.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.sbDetailLyricSize.setProgressDrawable(context.getResources().getDrawable(R.drawable.layer_progress_bar_default));
            panelMoreMenuBinding.sbDetailLyricSize.setThumb(context.getResources().getDrawable(R.drawable.shape_round_white));
            panelMoreMenuBinding.tvDetailValue25.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.tvDetailValue30.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.tvDetailValue35.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.tvDetailValue40.setTextColor(context.getResources().getColor(R.color.white));
            panelMoreMenuBinding.tvDetailValue45.setTextColor(context.getResources().getColor(R.color.white));
        }
    }

    /**
     * MainActivity主题改变 - 排序菜单：点击排序按钮ui
     * */
    public void sortMenuButtonTheme(int rThemeId, DialogSortMenuBinding sortMenuBinding, boolean isUpSortByTime, boolean isUpSortByName, boolean isUpSortBySinger) {
        if(rThemeId!=0) {
            if(rThemeId == R.id.ll_theme_white) {
                sortMenuBinding.ivSortByTimeType.setBackgroundResource(isUpSortByTime? R.drawable.ic_sort_up_purple : R.drawable.ic_sort_down_purple);
                sortMenuBinding.ivSortByNameType.setBackgroundResource(isUpSortByName? R.drawable.ic_sort_up_purple : R.drawable.ic_sort_down_purple);
                sortMenuBinding.ivSortBySingerType.setBackgroundResource(isUpSortBySinger? R.drawable.ic_sort_up_purple : R.drawable.ic_sort_down_purple);
            } else {
                sortMenuBinding.ivSortByTimeType.setBackgroundResource(isUpSortByTime? R.drawable.ic_sort_up : R.drawable.ic_sort_down);
                sortMenuBinding.ivSortByNameType.setBackgroundResource(isUpSortByName? R.drawable.ic_sort_up : R.drawable.ic_sort_down);
                sortMenuBinding.ivSortBySingerType.setBackgroundResource(isUpSortBySinger? R.drawable.ic_sort_up : R.drawable.ic_sort_down);
            }
        } else {
            sortMenuBinding.ivSortByTimeType.setBackgroundResource(isUpSortByTime? R.drawable.ic_sort_up : R.drawable.ic_sort_down);
            sortMenuBinding.ivSortByNameType.setBackgroundResource(isUpSortByName? R.drawable.ic_sort_up : R.drawable.ic_sort_down);
            sortMenuBinding.ivSortBySingerType.setBackgroundResource(isUpSortBySinger? R.drawable.ic_sort_up : R.drawable.ic_sort_down);
        }
    }

    /**
     * MainActivity主题改变 - 排序菜单：排序类型按钮ui （按时间排序、按名称排序、按歌手名排序）
     * */
    public void sortTypeButtonTheme(int rThemeId, ImageView iv, boolean isUp) {
        if(rThemeId!=0) {
            if(rThemeId == R.id.ll_theme_white) {
                iv.setBackgroundResource(isUp? R.drawable.ic_sort_up_purple : R.drawable.ic_sort_down_purple);
            } else {
                iv.setBackgroundResource(isUp? R.drawable.ic_sort_up : R.drawable.ic_sort_down);
            }
        } else {
            iv.setBackgroundResource(isUp? R.drawable.ic_sort_up : R.drawable.ic_sort_down);
        }
    }

    /**
     * MainActivity主题改变 - 排序菜单ui
     * */
    public void sortMenuTheme(Context context, int rThemeId, PopupWindow sortMenuPopupWindow, DialogSortMenuBinding sortMenuBinding) {
        if(rThemeId!=0) {
            if(rThemeId == R.id.ll_theme_normal) {
                sortMenuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_normal));
                sortMenuBinding.tvSortByTime.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.tvSortByName.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.tvSortBySinger.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.ivSortByTime.setBackgroundResource(R.drawable.ic_sort_by_time);
                sortMenuBinding.ivSortByName.setBackgroundResource(R.drawable.ic_sort_by_name);
                sortMenuBinding.ivSortBySinger.setBackgroundResource(R.drawable.ic_sort_by_singer);
                sortMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                sortMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_blue) {
                sortMenuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_blue));
                sortMenuBinding.tvSortByTime.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.tvSortByName.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.tvSortBySinger.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.ivSortByTime.setBackgroundResource(R.drawable.ic_sort_by_time);
                sortMenuBinding.ivSortByName.setBackgroundResource(R.drawable.ic_sort_by_name);
                sortMenuBinding.ivSortBySinger.setBackgroundResource(R.drawable.ic_sort_by_singer);
                sortMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                sortMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_dark) {
                sortMenuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu));
                sortMenuBinding.tvSortByTime.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.tvSortByName.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.tvSortBySinger.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.ivSortByTime.setBackgroundResource(R.drawable.ic_sort_by_time);
                sortMenuBinding.ivSortByName.setBackgroundResource(R.drawable.ic_sort_by_name);
                sortMenuBinding.ivSortBySinger.setBackgroundResource(R.drawable.ic_sort_by_singer);
                sortMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                sortMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_white) {
                sortMenuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_white));
                sortMenuBinding.tvSortByTime.setTextColor(context.getResources().getColor(R.color.purple));
                sortMenuBinding.tvSortByName.setTextColor(context.getResources().getColor(R.color.purple));
                sortMenuBinding.tvSortBySinger.setTextColor(context.getResources().getColor(R.color.purple));
                sortMenuBinding.ivSortByTime.setBackgroundResource(R.drawable.ic_sort_by_time_purple);
                sortMenuBinding.ivSortByName.setBackgroundResource(R.drawable.ic_sort_by_name_purple);
                sortMenuBinding.ivSortBySinger.setBackgroundResource(R.drawable.ic_sort_by_singer_purple);
                sortMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.purple));
                sortMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.purple));
            } else if(rThemeId == R.id.ll_theme_orange) {
                sortMenuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_orange));
                sortMenuBinding.tvSortByTime.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.tvSortByName.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.tvSortBySinger.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.ivSortByTime.setBackgroundResource(R.drawable.ic_sort_by_time);
                sortMenuBinding.ivSortByName.setBackgroundResource(R.drawable.ic_sort_by_name);
                sortMenuBinding.ivSortBySinger.setBackgroundResource(R.drawable.ic_sort_by_singer);
                sortMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                sortMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else if(rThemeId == R.id.ll_theme_light) {
                sortMenuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_light));
                sortMenuBinding.tvSortByTime.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.tvSortByName.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.tvSortBySinger.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.ivSortByTime.setBackgroundResource(R.drawable.ic_sort_by_time);
                sortMenuBinding.ivSortByName.setBackgroundResource(R.drawable.ic_sort_by_name);
                sortMenuBinding.ivSortBySinger.setBackgroundResource(R.drawable.ic_sort_by_singer);
                sortMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                sortMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            } else {
                sortMenuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_normal));
                sortMenuBinding.tvSortByTime.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.tvSortByName.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.tvSortBySinger.setTextColor(context.getResources().getColor(R.color.white));
                sortMenuBinding.ivSortByTime.setBackgroundResource(R.drawable.ic_sort_by_time);
                sortMenuBinding.ivSortByName.setBackgroundResource(R.drawable.ic_sort_by_name);
                sortMenuBinding.ivSortBySinger.setBackgroundResource(R.drawable.ic_sort_by_singer);
                sortMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
                sortMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            }
        } else {
            sortMenuPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_normal));
            sortMenuBinding.tvSortByTime.setTextColor(context.getResources().getColor(R.color.white));
            sortMenuBinding.tvSortByName.setTextColor(context.getResources().getColor(R.color.white));
            sortMenuBinding.tvSortBySinger.setTextColor(context.getResources().getColor(R.color.white));
            sortMenuBinding.ivSortByTime.setBackgroundResource(R.drawable.ic_sort_by_time);
            sortMenuBinding.ivSortByName.setBackgroundResource(R.drawable.ic_sort_by_name);
            sortMenuBinding.ivSortBySinger.setBackgroundResource(R.drawable.ic_sort_by_singer);
            sortMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
            sortMenuBinding.vLine2.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));
        }
    }

    /**
     * MainActivity主题改变 - 顺序播放按钮ui
     * @param btType 0: 顺序播放  1: 随机播放 2: 单曲循环
     * */
    public void sequentialPlayButtonTheme(int rThemeId, ActivityMainBinding vdb, int btType) {
        if(rThemeId != 0) {
            if(rThemeId == R.id.ll_theme_normal) {
                if(btType == 0) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black_33);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                } else if(btType == 1) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black_33);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                } else if(btType == 2) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black_33);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                }
            } else if(rThemeId == R.id.ll_theme_blue) {
                if(btType == 0) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_blue);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_order_play_blue);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_order_play);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_blue);
                } else if(btType == 1) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_blue);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_random_play_blue);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_random_play);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_blue);
                } else if(btType == 2) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_blue);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_single_play_blue);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_single_play);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_blue);
                }
            } else if(rThemeId == R.id.ll_theme_dark) {
                if(btType == 0) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_order_play);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_order_play);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_order_play);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play);
                } else if(btType == 1) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_random_play);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_random_play);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_random_play);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play);
                } else if(btType == 2) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_single_play);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_single_play);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_single_play);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play);
                }
            } else if(rThemeId == R.id.ll_theme_white) {
                if(btType == 0) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_purple);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_order_play_purple);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_purple);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_purple);
                } else if(btType == 1) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_purple);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_random_play_purple);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_purple);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_purple);
                } else if(btType == 2) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_purple);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_single_play_purple);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_purple);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_purple);
                }
            } else if(rThemeId == R.id.ll_theme_orange) {
                if(btType == 0) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_orange);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_order_play_orange);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_orange);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_orange);
                } else if(btType == 1) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_orange);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_random_play_orange);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_orange);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_orange);
                } else if(btType == 2) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_orange);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_single_play_orange);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_orange);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_orange);
                }
            } else if(rThemeId == R.id.ll_theme_light) {
                if(btType == 0) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_light);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_order_play_light);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_light);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_light);
                } else if(btType == 1) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_light);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_random_play_light);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_light);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_light);
                } else if(btType == 2) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_light);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_single_play_light);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_light);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_light);
                }
            } else {
                if(btType == 0) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black_33);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                } else if(btType == 1) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black_33);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                } else if(btType == 2) {
                    vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                    vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                    vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black_33);
                    vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                }
            }
        } else {
            if(btType == 0) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black_33);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
            } else if(btType == 1) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black_33);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
            } else if(btType == 2) {
                vdb.ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                vdb.ivNewPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                vdb.ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black_33);
                vdb.btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
            }
        }
    }

    /**
     * MainActivity主题改变 - 音乐列表添加按钮ui
     * */
    public void musicListAddButtonTheme(Context context, int rThemeId, ItemMusicListBinding binding) {
        if(rThemeId!=0) {
            if(rThemeId == R.id.ll_theme_normal) {
                binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.light_ff));
                binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.light_ff));
                binding.ivAdd.setBackgroundResource(R.drawable.ic_add_light);
                binding.ivMore.setBackgroundResource(R.drawable.ic_more_light_ff);
            } else if(rThemeId == R.id.ll_theme_blue) {
                binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.white));
                binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.white));
                binding.ivAdd.setBackgroundResource(R.drawable.ic_add);
                binding.ivMore.setBackgroundResource(R.drawable.ic_more);
            } else if(rThemeId == R.id.ll_theme_dark) {
                binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.white));
                binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.white));
                binding.ivAdd.setBackgroundResource(R.drawable.ic_add);
                binding.ivMore.setBackgroundResource(R.drawable.ic_more);
            } else if (rThemeId == R.id.ll_theme_white) {
                binding.rlMusicAll.setBackgroundResource(R.drawable.selector_white_theme_selected2);
                binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.purple));
                binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.gray_purple_ac));
                binding.ivAdd.setBackgroundResource(R.drawable.ic_add_gray_purple);
                binding.ivMore.setBackgroundResource(R.drawable.ic_more_purple_gray);
            } else if (rThemeId == R.id.ll_theme_orange) {
                binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.orange_0b));
                binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.orange_0b));
                binding.ivAdd.setBackgroundResource(R.drawable.ic_add_orange);
                binding.ivMore.setBackgroundResource(R.drawable.ic_more_orange);
            } else if(rThemeId == R.id.ll_theme_light) {
                binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.light_ff));
                binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.light_ff));
                binding.ivAdd.setBackgroundResource(R.drawable.ic_add_light);
                binding.ivMore.setBackgroundResource(R.drawable.ic_more_light_ff);
            } else {
                binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.light_ff));
                binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.light_ff));
                binding.ivAdd.setBackgroundResource(R.drawable.ic_add_light);
                binding.ivMore.setBackgroundResource(R.drawable.ic_more_light_ff);
            }
        }
    }

    /**
     * MainActivity主题改变 - 音乐列表添加按钮动画ui
     * */
    public void musicListAddButtonAnimatorTheme(int rThemeId, ItemMusicListBinding binding) {
        if(rThemeId!=0) {
            if (rThemeId == R.id.ll_theme_normal) {
                binding.ivAddAnimator.setVisibility(View.GONE);
                binding.ivAddAnimatorBlue.setVisibility(View.GONE);
                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                binding.ivAddAnimatorLight1.setVisibility(View.VISIBLE);
                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight1);
                animatorSet.start();
            } else if(rThemeId == R.id.ll_theme_blue) {
                binding.ivAddAnimator.setVisibility(View.GONE);
                binding.ivAddAnimatorBlue.setVisibility(View.VISIBLE);
                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorBlue);
                animatorSet.start();
            } else if(rThemeId == R.id.ll_theme_dark) {
                binding.ivAddAnimator.setVisibility(View.GONE);
                binding.ivAddAnimatorBlue.setVisibility(View.GONE);
                binding.ivAddAnimatorBlack.setVisibility(View.VISIBLE);
                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorBlack);
                animatorSet.start();
            } else if (rThemeId == R.id.ll_theme_white) {
                binding.ivAddAnimator.setVisibility(View.VISIBLE);
                binding.ivAddAnimatorBlue.setVisibility(View.GONE);
                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimator);
                animatorSet.start();
            } else if (rThemeId == R.id.ll_theme_orange) {
                binding.ivAddAnimator.setVisibility(View.GONE);
                binding.ivAddAnimatorBlue.setVisibility(View.GONE);
                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                binding.ivAddAnimatorOrange.setVisibility(View.VISIBLE);
                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorOrange);
                animatorSet.start();
            } else if(rThemeId == R.id.ll_theme_light) {
                binding.ivAddAnimator.setVisibility(View.GONE);
                binding.ivAddAnimatorBlue.setVisibility(View.GONE);
                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight2);
                animatorSet.start();
            } else {
                binding.ivAddAnimator.setVisibility(View.GONE);
                binding.ivAddAnimatorBlue.setVisibility(View.GONE);
                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight1);
                animatorSet.start();
            }
        } else {
            binding.ivAddAnimator.setVisibility(View.GONE);
            binding.ivAddAnimatorBlue.setVisibility(View.GONE);
            binding.ivAddAnimatorBlack.setVisibility(View.GONE);
            binding.ivAddAnimatorOrange.setVisibility(View.GONE);
            binding.ivAddAnimatorLight1.setVisibility(View.GONE);
            binding.ivAddAnimatorLight2.setVisibility(View.GONE);
            AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight1);
            animatorSet.start();
        }
    }

    /**
     * MainActivity主题改变 - 播放列表ui
     * */
    public void playListTheme(Context context, int rThemeId, ItemPlayListBinding binding, boolean isPlaying) {
        if(rThemeId!=0) {
            if(rThemeId == R.id.ll_theme_normal) {
                binding.tvOrderNum.setTextColor(isPlaying? context.getResources().getColor(R.color.light_f9) :  context.getResources().getColor(R.color.black));
                binding.tvMusicName.setTextColor(isPlaying?  context.getResources().getColor(R.color.light_f9) :  context.getResources().getColor(R.color.black));
                binding.tvSingerName.setTextColor(isPlaying?  context.getResources().getColor(R.color.light_f9) :  context.getResources().getColor(R.color.black));
                binding.ivDelete.setBackgroundResource(R.drawable.selector_delete_selected_2);
                binding.ivMusicRail.setVisibility(isPlaying? View.VISIBLE : GONE);
                binding.ivMusicRailBlue.setVisibility(View.GONE);
                binding.ivMusicRailDark.setVisibility(View.GONE);
                binding.ivMusicRailPurple.setVisibility(View.GONE);
                binding.ivMusicRailOrange.setVisibility(View.GONE);
                binding.ivMusicRailLight.setVisibility(View.GONE);
            } else if (rThemeId == R.id.ll_theme_blue) {
                binding.tvOrderNum.setTextColor(isPlaying? context.getResources().getColor(R.color.blue_0E) :  context.getResources().getColor(R.color.blue_ed));
                binding.tvMusicName.setTextColor(isPlaying?  context.getResources().getColor(R.color.blue_0E) :  context.getResources().getColor(R.color.blue_ed));
                binding.tvSingerName.setTextColor(isPlaying?  context.getResources().getColor(R.color.blue_0E) :  context.getResources().getColor(R.color.blue_ed));
                binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_blue);
                binding.ivMusicRail.setVisibility(View.GONE);
                binding.ivMusicRailBlue.setVisibility(isPlaying? View.VISIBLE : GONE);
                binding.ivMusicRailDark.setVisibility(View.GONE);
                binding.ivMusicRailPurple.setVisibility(View.GONE);
                binding.ivMusicRailOrange.setVisibility(View.GONE);
                binding.ivMusicRailLight.setVisibility(View.GONE);
            } else if (rThemeId == R.id.ll_theme_dark) {
                binding.tvOrderNum.setTextColor(isPlaying?  context.getResources().getColor(R.color.black) :  context.getResources().getColor(R.color.white));
                binding.tvMusicName.setTextColor(isPlaying?  context.getResources().getColor(R.color.black) :  context.getResources().getColor(R.color.white));
                binding.tvSingerName.setTextColor(isPlaying?  context.getResources().getColor(R.color.black) :  context.getResources().getColor(R.color.white));
                binding.ivDelete.setBackgroundResource(R.drawable.ic_delete);
                binding.ivMusicRail.setVisibility(View.GONE);
                binding.ivMusicRailBlue.setVisibility(View.GONE);
                binding.ivMusicRailDark.setVisibility(isPlaying? View.VISIBLE : GONE);
                binding.ivMusicRailPurple.setVisibility(View.GONE);
                binding.ivMusicRailOrange.setVisibility(View.GONE);
                binding.ivMusicRailLight.setVisibility(View.GONE);
            } else if (rThemeId == R.id.ll_theme_white) {
                binding.tvOrderNum.setTextColor(isPlaying?  context.getResources().getColor(R.color.purple_light) :  context.getResources().getColor(R.color.purple));
                binding.tvMusicName.setTextColor(isPlaying?  context.getResources().getColor(R.color.purple_light) :  context.getResources().getColor(R.color.purple));
                binding.tvSingerName.setTextColor(isPlaying?  context.getResources().getColor(R.color.purple_light) :  context.getResources().getColor(R.color.gray_purple_ac));
                binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_purple);
                binding.ivMusicRail.setVisibility(View.GONE);
                binding.ivMusicRailBlue.setVisibility(View.GONE);
                binding.ivMusicRailDark.setVisibility(View.GONE);
                binding.ivMusicRailPurple.setVisibility(isPlaying? View.VISIBLE : GONE);
                binding.ivMusicRailOrange.setVisibility(View.GONE);
                binding.ivMusicRailLight.setVisibility(View.GONE);
            } else if (rThemeId == R.id.ll_theme_orange) {
                binding.tvOrderNum.setTextColor(isPlaying?  context.getResources().getColor(R.color.orange_f4) :  context.getResources().getColor(R.color.orange_0b));
                binding.tvMusicName.setTextColor(isPlaying?  context.getResources().getColor(R.color.orange_f4) :  context.getResources().getColor(R.color.orange_0b));
                binding.tvSingerName.setTextColor(isPlaying?  context.getResources().getColor(R.color.orange_f4) :  context.getResources().getColor(R.color.orange_0b));
                binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_orange);
                binding.ivMusicRail.setVisibility(View.GONE);
                binding.ivMusicRailBlue.setVisibility(View.GONE);
                binding.ivMusicRailDark.setVisibility(View.GONE);
                binding.ivMusicRailPurple.setVisibility(View.GONE);
                binding.ivMusicRailOrange.setVisibility(isPlaying? View.VISIBLE : GONE);
                binding.ivMusicRailLight.setVisibility(View.GONE);
            } else if(rThemeId == R.id.ll_theme_light) {
                binding.tvOrderNum.setTextColor(isPlaying?  context.getResources().getColor(R.color.light_8a) :  context.getResources().getColor(R.color.light_b5));
                binding.tvMusicName.setTextColor(isPlaying?  context.getResources().getColor(R.color.light_8a) :  context.getResources().getColor(R.color.light_b5));
                binding.tvSingerName.setTextColor(isPlaying?  context.getResources().getColor(R.color.light_8a) :  context.getResources().getColor(R.color.light_b5));
                binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_light);
                binding.ivMusicRail.setVisibility(View.GONE);
                binding.ivMusicRailBlue.setVisibility(View.GONE);
                binding.ivMusicRailDark.setVisibility(View.GONE);
                binding.ivMusicRailPurple.setVisibility(View.GONE);
                binding.ivMusicRailOrange.setVisibility(View.GONE);
                binding.ivMusicRailLight.setVisibility(isPlaying? View.VISIBLE : GONE);

            } else {
                binding.tvOrderNum.setTextColor(isPlaying?  context.getResources().getColor(R.color.light_f9) :  context.getResources().getColor(R.color.black));
                binding.tvMusicName.setTextColor(isPlaying?  context.getResources().getColor(R.color.light_f9) :  context.getResources().getColor(R.color.black));
                binding.tvSingerName.setTextColor(isPlaying?  context.getResources().getColor(R.color.light_f9) :  context.getResources().getColor(R.color.black));
                binding.ivMusicRail.setVisibility(isPlaying? View.VISIBLE : GONE);
                binding.ivDelete.setBackgroundResource(R.drawable.selector_delete_selected_2);
                binding.ivMusicRail.setVisibility(isPlaying? View.VISIBLE : GONE);
                binding.ivMusicRailBlue.setVisibility(View.GONE);
                binding.ivMusicRailDark.setVisibility(View.GONE);
                binding.ivMusicRailPurple.setVisibility(View.GONE);
                binding.ivMusicRailOrange.setVisibility(View.GONE);
                binding.ivMusicRailLight.setVisibility(View.GONE);
            }
        } else {
            binding.tvOrderNum.setTextColor(isPlaying? context.getResources().getColor(R.color.light_f9) : context.getResources().getColor(R.color.black));
            binding.tvMusicName.setTextColor(isPlaying? context.getResources().getColor(R.color.light_f9) : context.getResources().getColor(R.color.black));
            binding.tvSingerName.setTextColor(isPlaying? context.getResources().getColor(R.color.light_f9) : context.getResources().getColor(R.color.black));
            binding.ivMusicRail.setVisibility(isPlaying? View.VISIBLE : GONE);
            binding.ivDelete.setBackgroundResource(R.drawable.selector_delete_selected_2);
            binding.ivMusicRail.setVisibility(isPlaying? View.VISIBLE : GONE);
            binding.ivMusicRailBlue.setVisibility(View.GONE);
            binding.ivMusicRailDark.setVisibility(View.GONE);
            binding.ivMusicRailPurple.setVisibility(View.GONE);
            binding.ivMusicRailOrange.setVisibility(View.GONE);
            binding.ivMusicRailLight.setVisibility(View.GONE);
        }
    }

    /**
     * SettingActivity主题改变 - 整体ui
     * */
    public void settingActivityTheme(Context context, int rThemeId, ActivitySettingsBinding vdb) {
        if(rThemeId == R.id.ll_theme_normal) {
            vdb.clBg.setBackgroundResource(R.mipmap.ic_gradient_color5);
            vdb.ivBack.setBackgroundResource(R.drawable.ic_arrow_back_light);
            vdb.ivArrowInto.setBackgroundResource(R.drawable.ic_arrow_into_light);
            vdb.tvSettings.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvTheme.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvThemeNormal.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvThemeBlue.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvThemeDark.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvThemeWhite.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvThemeOrange.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvThemeLight.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.rlSettingsBar.setBackgroundResource(R.drawable.shape_button_white_alpha);
            vdb.llThemeMain.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llThemeNormal.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llThemeWhite.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSettingWelcomeVideo.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llVersionMain.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llCleanCache.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llErrorLog.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llAbout.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvSettingWelcomeVideo.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvVersion.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvVersionValue.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvCleanCache.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvCacheValue.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvErrorLog.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvAbout.setTextColor(context.getResources().getColor(R.color.light_ff));
        } else if(rThemeId == R.id.ll_theme_blue) {
            vdb.clBg.setBackgroundResource(R.mipmap.ic_gradient_color8);
            vdb.ivBack.setBackgroundResource(R.drawable.ic_arrow_back);
            vdb.ivArrowInto.setBackgroundResource(R.drawable.ic_arrow_into);
            vdb.tvSettings.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvTheme.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvThemeNormal.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvThemeBlue.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvThemeDark.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvThemeWhite.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvThemeOrange.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvThemeLight.setTextColor(context.getResources().getColor(R.color.white));
            vdb.rlSettingsBar.setBackgroundResource(R.drawable.shape_button_white_alpha);
            vdb.llThemeMain.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llThemeNormal.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llThemeWhite.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSettingWelcomeVideo.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llVersionMain.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llCleanCache.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llErrorLog.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llAbout.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvSettingWelcomeVideo.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvVersion.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvVersionValue.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvCleanCache.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvCacheValue.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvErrorLog.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvAbout.setTextColor(context.getResources().getColor(R.color.white));
        } else if(rThemeId == R.id.ll_theme_dark) {
            vdb.clBg.setBackgroundResource(R.mipmap.ic_gradient_color6);
            vdb.ivBack.setBackgroundResource(R.drawable.ic_arrow_back);
            vdb.ivArrowInto.setBackgroundResource(R.drawable.ic_arrow_into);
            vdb.tvSettings.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvTheme.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvThemeNormal.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvThemeBlue.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvThemeDark.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvThemeWhite.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvThemeOrange.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvThemeLight.setTextColor(context.getResources().getColor(R.color.white));
            vdb.rlSettingsBar.setBackgroundResource(R.drawable.shape_button_white_alpha);
            vdb.llThemeMain.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llThemeNormal.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llThemeWhite.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSettingWelcomeVideo.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llVersionMain.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llCleanCache.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llErrorLog.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llAbout.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvSettingWelcomeVideo.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvVersion.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvVersionValue.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvCleanCache.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvCacheValue.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvErrorLog.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvAbout.setTextColor(context.getResources().getColor(R.color.white));
        } else if(rThemeId == R.id.ll_theme_white) {
            vdb.clBg.setBackgroundResource(R.color.background_color_F2);
            vdb.ivBack.setBackgroundResource(R.drawable.ic_arrow_back_purple);
            vdb.ivArrowInto.setBackgroundResource(R.drawable.ic_arrow_into_purple);
            vdb.tvTheme.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvSettings.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvThemeNormal.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvThemeBlue.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvThemeDark.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvThemeWhite.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvThemeOrange.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvThemeLight.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.rlSettingsBar.setBackgroundResource(R.drawable.shape_button_black_alpha_2);
            vdb.llThemeMain.setBackgroundResource(R.drawable.shape_button_black_alpha);
            vdb.llThemeNormal.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llThemeWhite.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSettingWelcomeVideo.setBackgroundResource(R.drawable.shape_button_black_alpha);
            vdb.llVersionMain.setBackgroundResource(R.drawable.shape_button_black_alpha);
            vdb.llCleanCache.setBackgroundResource(R.drawable.shape_button_black_alpha);
            vdb.llErrorLog.setBackgroundResource(R.drawable.shape_button_black_alpha);
            vdb.llAbout.setBackgroundResource(R.drawable.shape_button_black_alpha);
            vdb.tvSettingWelcomeVideo.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvVersion.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvVersionValue.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvCleanCache.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvCacheValue.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvErrorLog.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvAbout.setTextColor(context.getResources().getColor(R.color.purple));
        } else if(rThemeId == R.id.ll_theme_orange) {
            vdb.clBg.setBackgroundResource(R.mipmap.ic_gradient_color7);
            vdb.ivBack.setBackgroundResource(R.drawable.ic_arrow_back_orange);
            vdb.ivArrowInto.setBackgroundResource(R.drawable.ic_arrow_into_orange);
            vdb.tvSettings.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvTheme.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvThemeNormal.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvThemeBlue.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvThemeDark.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvThemeWhite.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvThemeOrange.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvThemeLight.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.rlSettingsBar.setBackgroundResource(R.drawable.shape_button_white_alpha);
            vdb.llThemeMain.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llThemeNormal.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llThemeWhite.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSettingWelcomeVideo.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llVersionMain.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llCleanCache.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llErrorLog.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llAbout.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvSettingWelcomeVideo.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvVersion.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvVersionValue.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvCleanCache.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvCacheValue.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvErrorLog.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvAbout.setTextColor(context.getResources().getColor(R.color.orange_0b));
        } else if(rThemeId == R.id.ll_theme_light) {
            vdb.clBg.setBackgroundResource(R.mipmap.ic_gradient_color4);
            vdb.ivBack.setBackgroundResource(R.drawable.ic_arrow_back_light);
            vdb.ivArrowInto.setBackgroundResource(R.drawable.ic_arrow_into_light);
            vdb.tvSettings.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvTheme.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvThemeNormal.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvThemeBlue.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvThemeDark.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvThemeWhite.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvThemeOrange.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvThemeLight.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.rlSettingsBar.setBackgroundResource(R.drawable.shape_button_white_alpha);
            vdb.llThemeMain.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llThemeNormal.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llThemeWhite.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llSettingWelcomeVideo.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llVersionMain.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llErrorLog.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llCleanCache.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.llAbout.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvSettingWelcomeVideo.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvVersion.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvVersionValue.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvCleanCache.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvCacheValue.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvErrorLog.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvAbout.setTextColor(context.getResources().getColor(R.color.light_ff));
        }
    }

    /**
     *  SettingActivity主题改变 - 下载进度 ui
     * */
    public void settingActivityProgressTheme(Context context, int rThemeId, HorizontalProgressBar hpvProgress) {
        if(rThemeId == R.id.ll_theme_normal) {
            hpvProgress.setLinearGradient(R.color.light_f9);
        } else if(rThemeId == R.id.ll_theme_blue) {
            hpvProgress.setLinearGradient(R.color.blue_0E);
        } else if(rThemeId == R.id.ll_theme_dark) {
            hpvProgress.setLinearGradient(R.color.white);
        } else if(rThemeId == R.id.ll_theme_white) {
            hpvProgress.setLinearGradient(R.color.purple);
        } else if(rThemeId == R.id.ll_theme_orange) {
            hpvProgress.setLinearGradient(R.color.orange_0b);
        } else if(rThemeId == R.id.ll_theme_light) {
            hpvProgress.setLinearGradient(R.color.light_b5);
        } else {
            hpvProgress.setLinearGradient(R.color.light_f9);
        }
    }

    /**
     * MainListFragment主题改变
     * */
    public void mainListFragmentTheme(Context context, int rThemeId, FragmentMainListBinding vdb) {
        if(rThemeId == R.id.ll_theme_normal) {
            vdb.tvGroup0.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvRecommend1.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvRecommend2.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvRecommend3.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvGroup1.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvGroup2.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvGroup3.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvGroup0.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup1.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup2.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup3.setBackgroundResource(R.drawable.selector_normal_selected);
        } else if(rThemeId == R.id.ll_theme_blue) {
            vdb.tvGroup0.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvRecommend1.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvRecommend2.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvRecommend3.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvGroup1.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvGroup2.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvGroup3.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvGroup0.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup1.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup2.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup3.setBackgroundResource(R.drawable.selector_normal_selected);
        } else if(rThemeId == R.id.ll_theme_dark) {
            vdb.tvGroup0.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvRecommend1.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvRecommend2.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvRecommend3.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvGroup1.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvGroup2.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvGroup3.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvGroup0.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup1.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup2.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup3.setBackgroundResource(R.drawable.selector_normal_selected);
        } else if(rThemeId == R.id.ll_theme_white) {
            vdb.tvGroup0.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvRecommend1.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvRecommend2.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvRecommend3.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvGroup1.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvGroup2.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvGroup3.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvGroup0.setBackgroundResource(R.drawable.shape_button_black_alpha);
            vdb.tvGroup1.setBackgroundResource(R.drawable.shape_button_black_alpha);
            vdb.tvGroup2.setBackgroundResource(R.drawable.shape_button_black_alpha);
            vdb.tvGroup3.setBackgroundResource(R.drawable.shape_button_black_alpha);
        } else if(rThemeId == R.id.ll_theme_orange) {
            vdb.tvGroup0.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvRecommend1.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvRecommend2.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvRecommend3.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvGroup1.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvGroup2.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvGroup3.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvGroup0.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup1.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup2.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup3.setBackgroundResource(R.drawable.selector_normal_selected);
        } else if(rThemeId == R.id.ll_theme_light) {
            vdb.tvGroup0.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvRecommend1.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvRecommend2.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvRecommend3.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvGroup1.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvGroup2.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvGroup3.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvGroup0.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup1.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup2.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup3.setBackgroundResource(R.drawable.selector_normal_selected);
        } else {
            vdb.tvGroup0.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvRecommend1.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvRecommend2.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvRecommend3.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvGroup1.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvGroup2.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvGroup3.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvGroup0.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup1.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup2.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvGroup3.setBackgroundResource(R.drawable.selector_normal_selected);
        }
    }

    /**
     * LocalListFragment主题改变 - 整体ui
     * */
    public void localListFragmentTheme(Context context, int rThemeId, FragmentLocalListBinding vdb) {
        if(rThemeId == R.id.ll_theme_normal) {
            vdb.llAppBar.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvLocalPlayListAll.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.ivLocalMusic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_local_music_light));
            vdb.tvLocalMusic.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvLocalMusicCount.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.ivFavoriteMusic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_light_ff));
            vdb.tvFavoriteMusic.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvFavoriteMusicCount.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvLocalPlayListAll.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvSingleMusic.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvMusicCount.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvNullLocalList.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvEditDelete.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.ivBack.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_back_light));
        } else if(rThemeId == R.id.ll_theme_blue) {
            vdb.llAppBar.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvLocalPlayListAll.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.ivLocalMusic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_local_music));
            vdb.tvLocalMusic.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvLocalMusicCount.setTextColor(context.getResources().getColor(R.color.white));
            vdb.ivFavoriteMusic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite));
            vdb.tvFavoriteMusic.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvFavoriteMusicCount.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvLocalPlayListAll.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvSingleMusic.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvMusicCount.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvNullLocalList.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvEditDelete.setTextColor(context.getResources().getColor(R.color.white));
            vdb.ivBack.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_back));
        } else if(rThemeId == R.id.ll_theme_dark) {
            vdb.llAppBar.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvLocalPlayListAll.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.ivLocalMusic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_local_music));
            vdb.tvLocalMusic.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvLocalMusicCount.setTextColor(context.getResources().getColor(R.color.white));
            vdb.ivFavoriteMusic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite));
            vdb.tvFavoriteMusic.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvFavoriteMusicCount.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvLocalPlayListAll.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvSingleMusic.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvMusicCount.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvNullLocalList.setTextColor(context.getResources().getColor(R.color.white));
            vdb.tvEditDelete.setTextColor(context.getResources().getColor(R.color.white));
            vdb.ivBack.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_back));
        } else if(rThemeId == R.id.ll_theme_white) {
            vdb.llAppBar.setBackgroundResource(R.drawable.shape_button_black_alpha);
            vdb.tvLocalPlayListAll.setBackgroundResource(R.drawable.shape_button_black_alpha);
            vdb.ivLocalMusic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_local_music_purple));
            vdb.tvLocalMusic.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvLocalMusicCount.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.ivFavoriteMusic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_purple));
            vdb.tvFavoriteMusic.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvFavoriteMusicCount.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvLocalPlayListAll.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvSingleMusic.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvMusicCount.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvNullLocalList.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.tvEditDelete.setTextColor(context.getResources().getColor(R.color.purple));
            vdb.ivBack.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_back_purple));
        } else if(rThemeId == R.id.ll_theme_orange) {
            vdb.llAppBar.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvLocalPlayListAll.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.ivLocalMusic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_local_music_orange));
            vdb.tvLocalMusic.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvLocalMusicCount.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.ivFavoriteMusic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_orange));
            vdb.tvFavoriteMusic.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvFavoriteMusicCount.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvLocalPlayListAll.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvSingleMusic.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvMusicCount.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvNullLocalList.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.tvEditDelete.setTextColor(context.getResources().getColor(R.color.orange_0b));
            vdb.ivBack.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_back_orange));
        } else if(rThemeId == R.id.ll_theme_light) {
            vdb.llAppBar.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvLocalPlayListAll.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.ivLocalMusic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_local_music_light));
            vdb.tvLocalMusic.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvLocalMusicCount.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.ivFavoriteMusic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_light_ff));
            vdb.tvFavoriteMusic.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvFavoriteMusicCount.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvLocalPlayListAll.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvSingleMusic.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvMusicCount.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvNullLocalList.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvEditDelete.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.ivBack.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_back_light));
        } else {
            vdb.llAppBar.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.tvLocalPlayListAll.setBackgroundResource(R.drawable.selector_normal_selected);
            vdb.ivLocalMusic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_local_music_light));
            vdb.tvLocalMusic.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvLocalMusicCount.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.ivFavoriteMusic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_light_ff));
            vdb.tvFavoriteMusic.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvFavoriteMusicCount.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvLocalPlayListAll.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvSingleMusic.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvMusicCount.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvNullLocalList.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.tvEditDelete.setTextColor(context.getResources().getColor(R.color.light_ff));
            vdb.ivBack.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_back_light));
        }
    }

    /**
     * LocalListFragment主题改变 - 本地音乐列表ui
     * */
    public void localListTheme(Context context, int rThemeId, ItemLocalMusicListBinding binding) {
        if(rThemeId!=0) {
            if(rThemeId == R.id.ll_theme_normal) {
                binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.light_ff));
                binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.light_ff));
                binding.ivAdd.setBackgroundResource(R.drawable.ic_add_light);
                binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_light_ff);
            } else if(rThemeId == R.id.ll_theme_blue) {
                binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.white));
                binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.white));
                binding.ivAdd.setBackgroundResource(R.drawable.ic_add);
                binding.ivDelete.setBackgroundResource(R.drawable.ic_delete);
            } else if(rThemeId == R.id.ll_theme_dark) {
                binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.white));
                binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.white));
                binding.ivAdd.setBackgroundResource(R.drawable.ic_add);
                binding.ivDelete.setBackgroundResource(R.drawable.ic_delete);
            } else if (rThemeId == R.id.ll_theme_white) {
                binding.rlMusicAll.setBackgroundResource(R.drawable.selector_white_theme_selected2);
                binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.purple));
                binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.gray_purple_ac));
                binding.ivAdd.setBackgroundResource(R.drawable.ic_add_gray_purple);
                binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_purple_ac);
            } else if (rThemeId == R.id.ll_theme_orange) {
                binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.orange_0b));
                binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.orange_0b));
                binding.ivAdd.setBackgroundResource(R.drawable.ic_add_orange);
                binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_orange);
            } else if(rThemeId == R.id.ll_theme_light) {
                binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.light_ff));
                binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.light_ff));
                binding.ivAdd.setBackgroundResource(R.drawable.ic_add_light);
                binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_light_ff);
            } else {
                binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.light_ff));
                binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.light_ff));
                binding.ivAdd.setBackgroundResource(R.drawable.ic_add_light);
                binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_light_ff);
            }
        } else {
            binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
            binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.light_ff));
            binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.light_ff));
            binding.ivAdd.setBackgroundResource(R.drawable.ic_add_light);
            binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_light_ff);
        }
    }

    /**
     * LocalListFragment主题改变 - 本地音乐列表 添加按钮动画ui
     * */
    public void localListAddButtonAnimatorTheme(int rThemeId, ItemLocalMusicListBinding binding) {
        if(rThemeId!=0) {
            if (rThemeId == R.id.ll_theme_normal) {
                binding.ivAddAnimator.setVisibility(View.GONE);
                binding.ivAddAnimatorBlue.setVisibility(View.GONE);
                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                binding.ivAddAnimatorLight1.setVisibility(View.VISIBLE);
                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight1);
                animatorSet.start();
            } else if(rThemeId == R.id.ll_theme_blue) {
                binding.ivAddAnimator.setVisibility(View.GONE);
                binding.ivAddAnimatorBlue.setVisibility(View.VISIBLE);
                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorBlack);
                animatorSet.start();
            } else if(rThemeId == R.id.ll_theme_dark) {
                binding.ivAddAnimator.setVisibility(View.GONE);
                binding.ivAddAnimatorBlue.setVisibility(View.GONE);
                binding.ivAddAnimatorBlack.setVisibility(View.VISIBLE);
                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorBlack);
                animatorSet.start();
            } else if (rThemeId == R.id.ll_theme_white) {
                binding.ivAddAnimator.setVisibility(View.VISIBLE);
                binding.ivAddAnimatorBlue.setVisibility(View.GONE);
                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimator);
                animatorSet.start();
            } else if (rThemeId == R.id.ll_theme_orange) {
                binding.ivAddAnimator.setVisibility(View.GONE);
                binding.ivAddAnimatorBlue.setVisibility(View.GONE);
                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                binding.ivAddAnimatorOrange.setVisibility(View.VISIBLE);
                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorOrange);
                animatorSet.start();
            } else if(rThemeId == R.id.ll_theme_light) {
                binding.ivAddAnimator.setVisibility(View.GONE);
                binding.ivAddAnimatorBlue.setVisibility(View.GONE);
                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight2);
                animatorSet.start();
            } else {
                binding.ivAddAnimator.setVisibility(View.GONE);
                binding.ivAddAnimatorBlue.setVisibility(View.GONE);
                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight1);
                animatorSet.start();
            }
        } else {
            binding.ivAddAnimator.setVisibility(View.GONE);
            binding.ivAddAnimatorBlue.setVisibility(View.GONE);
            binding.ivAddAnimatorBlack.setVisibility(View.GONE);
            binding.ivAddAnimatorOrange.setVisibility(View.GONE);
            binding.ivAddAnimatorLight1.setVisibility(View.GONE);
            binding.ivAddAnimatorLight2.setVisibility(View.GONE);
            AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight1);
            animatorSet.start();
        }
    }

    /**
     * LocalListFragment主题改变 - 自建列表展示ui
     * */
    public void localPlayListTheme(Context context, int rThemeId, ItemLocalPlayListBinding binding) {
        if(rThemeId == R.id.ll_theme_normal) {
            binding.tvPlayListName.setTextColor(context.getResources().getColor(R.color.light_ff));
            binding.tvPlayListCount.setTextColor(context.getResources().getColor(R.color.light_ff));
            binding.ivMenu.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_more_light_ff));
        } else if(rThemeId == R.id.ll_theme_blue) {
            binding.tvPlayListName.setTextColor(context.getResources().getColor(R.color.white));
            binding.tvPlayListCount.setTextColor(context.getResources().getColor(R.color.white));
            binding.ivMenu.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_more));
        } else if(rThemeId == R.id.ll_theme_dark) {
            binding.tvPlayListName.setTextColor(context.getResources().getColor(R.color.white));
            binding.tvPlayListCount.setTextColor(context.getResources().getColor(R.color.white));
            binding.ivMenu.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_more));
        } else if(rThemeId == R.id.ll_theme_white) {
            binding.tvPlayListName.setTextColor(context.getResources().getColor(R.color.purple));
            binding.tvPlayListCount.setTextColor(context.getResources().getColor(R.color.purple));
            binding.ivMenu.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_more_purple));
        } else if(rThemeId == R.id.ll_theme_orange) {
            binding.tvPlayListName.setTextColor(context.getResources().getColor(R.color.orange_0b));
            binding.tvPlayListCount.setTextColor(context.getResources().getColor(R.color.orange_0b));
            binding.ivMenu.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_more_orange));
        } else if(rThemeId == R.id.ll_theme_light) {
            binding.tvPlayListName.setTextColor(context.getResources().getColor(R.color.light_ff));
            binding.tvPlayListCount.setTextColor(context.getResources().getColor(R.color.light_ff));
            binding.ivMenu.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_more_light_ff));
        } else {
            binding.tvPlayListName.setTextColor(context.getResources().getColor(R.color.light_ff));
            binding.tvPlayListCount.setTextColor(context.getResources().getColor(R.color.light_ff));
            binding.ivMenu.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_more_light_ff));
        }
    }

    /**
     * LocalListFragment主题改变 - 自建列表添加ui
     * */
    public void addLocalPlayListTheme(Context context, int rThemeId, ItemLocalPlayListAddBinding binding) {
        if(rThemeId == R.id.ll_theme_normal) {
            binding.ivPlayListAdd.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_light));
            binding.tvPlayListName.setTextColor(context.getResources().getColor(R.color.light_ff));
            binding.ivInto.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_into_light));

        } else if(rThemeId == R.id.ll_theme_blue) {
            binding.ivPlayListAdd.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add));
            binding.tvPlayListName.setTextColor(context.getResources().getColor(R.color.white));
            binding.ivInto.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_into));

        } else if(rThemeId == R.id.ll_theme_dark) {
            binding.ivPlayListAdd.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add));
            binding.tvPlayListName.setTextColor(context.getResources().getColor(R.color.white));
            binding.ivInto.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_into));

        } else if(rThemeId == R.id.ll_theme_white) {
            binding.ivPlayListAdd.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_purple));
            binding.tvPlayListName.setTextColor(context.getResources().getColor(R.color.purple));
            binding.ivInto.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_into_purple));

        } else if(rThemeId == R.id.ll_theme_orange) {
            binding.ivPlayListAdd.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_orange));
            binding.tvPlayListName.setTextColor(context.getResources().getColor(R.color.orange_0b));
            binding.ivInto.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_into_orange));

        } else if(rThemeId == R.id.ll_theme_light) {
            binding.ivPlayListAdd.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_light));
            binding.tvPlayListName.setTextColor(context.getResources().getColor(R.color.light_ff));
            binding.ivInto.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_into_light));

        } else {
            binding.ivPlayListAdd.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_light));
            binding.tvPlayListName.setTextColor(context.getResources().getColor(R.color.light_ff));
            binding.ivInto.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_into_light));

        }
    }
}
