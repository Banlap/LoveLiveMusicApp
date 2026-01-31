import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("com.android.application")
}

android {
    namespace = "com.banlap.llmusic"
    compileSdk = 33

    //AGP8.0以上默认关闭BuildConfig
    buildFeatures {
        buildConfig = true
        dataBinding = true
    }

    defaultConfig {
        applicationId = "com.banlap.llmusic"
        minSdk = 24
        targetSdk = 33
        //修改版本号
        versionCode = 186
        versionName = "1.0"
        //1.设置连接音乐数据库、实现播放音乐
        //2.实现各种显示效果、播放控制器各种功能
        versionName = "1.0.1"
        //1.修正点击音乐列表其中一首，即时播放并添加到播放列表
        //2.修正播放模式：顺序播放、随机播放和单曲循环逻辑
        //3.修正播放列表显示效果
        versionName = "1.0.2"
        //1.新增保存已添加歌曲的列表数据、一键删除功能
        //2.修正app后台播放被清除进程问题
        //3.修改部分ui显示
        versionName = "1.1"
        //Beta version
        //1.新增歌词显示，根据歌曲时间显示对应歌词并颜色改变
        //(歌词滚动显示功能存在一定问题 后续对该功能继续优化)
        //2.新增通知栏控制歌曲播放
        //3.修正列表清空时点击添加歌曲显示异常
        versionName = "1.1.1"
        //1.修复播放音乐时使用前台服务提高存活性
        versionName = "1.1.2"
        //1.添加蓝牙断开时播放暂停功能
        //2.调整部分ui颜色
        versionName = "1.1.2.1"
        //1.解决8.0系统以下前台通知导致闪退问题
        versionName = "1.1.3"
        //1.加入欢迎页面
        versionName = "1.2.0"
        //1.新增播放全部歌曲、查询列表歌曲、排序等功能
        //2.修改循环播放逻辑
        versionName = "1.2.1"
        //1.新增主题设置
        //2.优化界面交互效果
        //3.新增保存播放模式、主题参数
        //4.修复播放列表显示问题
        versionName = "1.2.2"
        //1.添加歌曲分类模块
        //2.优化界面交互效果，调整部分内容
        versionName = "1.2.3"
        //1.添加新分类
        //2.UI界面交互优化
        versionName = "1.2.4"
        //1.修改分类图ui、修正排序弹框显示位置、调整删除所有歌曲弹框样式
        //2.添加载入歌曲loading显示、添加橙色主题
        //3.修正随机播放模式下按下下一首功能
        //4.添加按歌手排序功能
        versionName = "1.2.5"
        //1.修改部分ui分类图
        //2.添加新分类
        versionName = "1.2.6"
        //1.修复图标无法正常切换问题
        //2.更改播放全部按钮处理逻辑
        //3.在播放页面添加播放模式功能
        //4.修正部分ui图
        versionName = "1.3"
        //1.beta版本 - 添加角色功能
        versionName = "1.3.1"
        //1.角色功能运行于服务、在桌面显示
        //2.角色中添加新状态、添加播放控制
        //3.修正通知栏显示图片质量显示问题
        //4.修正点击后退判断是否到app主界面
        //4.修正app销毁后音乐暂停并销毁所有服务
        versionName = "1.3.2"
        //1.添加新主题：浅色主题
        //2.调整播放栏UI问题、调整分类UI图
        //3.新增功能：版本更新推送以及app下载功能
        versionName = "1.4"
        //1.重新设计部分ui: 播放控制区域、列表局部滚动效果、图片大小、主题ui调整
        //2.新增黑色主题、新增升/降序排列歌曲
        versionName = "1.4.1"
        //1.修复app下载更新失败问题
        versionName = "1.4.2"
        //1.重新设计滚动歌词功能
        //2.添加新角色状态：涩谷香音
        //3.调整橙色主题内容
        versionName = "1.4.3"
        //1.消息通知添加点击返回app界面
        //2.修正部分文字内容、调整歌曲名称显示过长问题
        //3.歌曲控制栏添加进度条、调整部分主题UI
        versionName = "1.4.4"
        //1.修正授权开启显示角色后状态点显示问题
        //2.设置按钮和角色按钮整合到主页菜单、菜单框样式调整
        //3.添加关于app内容
        versionName = "1.4.5"
        //1.适配蓝牙耳机控制app播放、暂停、上一首、下一首
        //2.设置壁纸功能
        versionName = "1.4.6"
        //1.解决app进入分屏、小屏幕模式下app重启并播放失败
        //2.解决播放部分歌曲时通知没及时更新成功
        //3.添加本地歌曲导入
        versionName = "1.4.7"
        //1.修复首次点击本地文件时权限允许后问题
        //2.修复选择本地文件的下载目录后，选择文件闪退问题
        versionName = "1.4.8"
        //1.微调通知栏ui布局
        //2.修复部分机型在选择文件路径时获取文件路径问题
        versionName = "1.5.0"
        //1.调整部分ui
        //2.新增桌面音乐小组件
        versionName = "1.5.1"
        //1.调整ui：欢迎页视频、首页分类图
        //2.使用网络框架管理url请求
        //3.更新小组件ui
        versionName = "1.5.2"
        //1.小组件更新：添加4x1透明组件、更新4x2组件UI
        //2.新增功能：播放歌曲预缓存功能，缓存后的歌曲可离线播放，同时在设置中添加清理缓存功能
        //3.修复问题：本地歌曲显示图片质量过大闪退
        versionName = "1.5.3"
        //1.修复问题：部分ui修正、小组件ui修正
        //2.新增功能：添加莲之空女学园偶像俱乐部企划、设置启动动画
        versionName = "1.5.4"
        //1.更改问题：ui播放列表添加logo、调整部分ui、代码优化
        //2.修复问题：切换播放本地歌曲图片名称不同步、部分bug
        versionName = "1.5.5"
        //1.修复问题：歌词显示颜色问题、4x1透明组件图片问题
        //2.新增功能：自建歌单、收藏，并重新设计ui
        versionName = "1.5.6"
        //1.更改问题：重新设计主页面ui
        //2.新增功能：新增每日推荐功能
        versionName = "1.6.0"
        //1.修复问题：切换主题ui每日推荐字体、调整部分主题颜色、歌词显示调整
        //2.新增功能：新音乐界面切换、新增深蓝色主题
        versionName = "1.6.1"
        //1.修复小组件点击进入时闪退问题、android13通知栏推送修复、查询歌曲UI更正
        //2.更新歌词文本效果变化、添加错误日志输出及查看、添加歌词字体大小设置
        versionName = "1.6.2"
        //1.修复本地歌曲添加多个后卡顿问题
        //2.新增定时关闭歌曲任务，移除错误日志输出，添加全局常报错框架
        versionName = "1.6.3"
        //1.更改数据源服务连接
        //2.修复部分版本下载更新后安装问题
        //3.修复部分ui显示问题
        versionName = "1.6.4"
        //1.修复歌曲列表显示问题
        versionName = "1.6.5"
        //1.播放列表能长按拖动指定歌曲顺序
        //2.新增红色主题，修复部分ui、动画效果
        //3.添加新启动动画选择
        versionName = "1.6.6"
        //1.解决首次进入音乐列表加载图片时滑动列表导致卡顿
        //2.冷启动app时加入过渡动画
        //3.优化部分ui
        versionName = "1.7.0"
        //1.优化部分ui
        //2.角色功能添加小游戏内容
        versionName = "1.7.1"
        //1.优化音乐框架部分内容、优化通知栏显示问题
        //2.调整界面模式开启位置
        //3.全新适配平板模式
        versionName = "1.7.2"
        //1.修复平板模式后退到系统桌面再次进入数据丢失
        //2.修复点击或拖动播放进度条闪跳问题
        versionName = "1.7.3"
        //1.修复拖动播放进度条时间和歌词UI刷新问题
        //2.音乐详情中调整更多设置菜单，添加歌曲详细信息
        //3.根据底部栏调整部分ui位置
        versionName = "1.7.4"
        //1.显示歌曲文件质量
        //2.添加下载歌曲功能
        //3.添加星空主题，并修复部分主题ui
        versionName = "1.7.5"
        //1.修复界面模式下的控制条位置、调整加载圈显示
        //2.修复歌曲数刷新问题、调整歌词滚动速度
        //3.添加桌面悬浮歌词功能、添加其他分组
        versionName = "1.7.6"
        //1.更新部分ui
        versionName = "1.8.0"
        //1.更换新图标、角色功能添加关闭功能
        //2.修正清理缓存功能：同时将歌曲缓存清除
        //3.修正本地歌曲删除时刷新性能问题
        versionName = "1.8.1"
        //1.优化当前播放列表删除卡顿问题
        //2.优化蓝牙播放点击问题、部分机器组件状态显示问题
        //3.添加新企划: BLUEBIRD
        versionName = "1.8.2"
        //1.替换播放库：使用ExoPlayer
        //2.变更服务源地址：旧节点下架
        //3.重新优化部分代码及调整部分问题
        versionName = "1.8.3"
        //1.优化问题：点击播放全部歌曲问题、优化点击收藏按钮问题
        //2.修复问题：清空当前列表后ui更新、删除当前列表一首歌曲时清除状态问题
        versionName = "1.8.4"
        //1.修复歌词显示卡顿问题
        //2.修复小组件不显示歌曲图片
        versionName = "1.8.5"
        //1.修复当前列表bluebird歌曲显示icon问题
        //2.修复播放进度条ui问题
        //3.修复车机首次点击播放暂停按钮逻辑
        versionName = "1.8.6"
        //1.替换缓存数据方式：使用room持久化库
        //2.优化UI更新问题：使用MutableLiveData管理ui
        //3.优化连续切换歌曲时信息不同步问题、优化关闭蓝牙后暂停歌曲
        //4.优化pad版本下点击切换页面缓慢问题

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "MYSQL_URL", "\"${project.property("MYSQL_URL")}\"")
        buildConfigField("String", "MYSQL_ACCOUNT","\"${project.property("MYSQL_ACCOUNT")}\"")
        buildConfigField("String", "MYSQL_PASSWORD", "\"${project.property("MYSQL_PASSWORD")}\"")
    }

    android {
        applicationVariants.configureEach {
            outputs.configureEach {
                val df = SimpleDateFormat("yyyy-MM-dd_HH-mm")
                val newName = "LLMusicPlayer_official_v${versionName}_${df.format(Date())}.apk"
                (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName = newName
            }
        }
    }

    buildTypes {
        debug {
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation ("androidx.appcompat:appcompat:1.3.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation ("com.google.ar:core:1.25.0")
    testImplementation ("junit:junit:4.13.2")
    //androidTestImplementation 'androidx.test.ext:junit:1.1.2'
    //androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
    /** mysql数据库 */
    implementation(files("libs/mysql-connector-java-5.1.49.jar"))
    /** Room持久化库 高版本2.6.0以上需要gradle8.0 */
    implementation("androidx.room:room-runtime:2.5.2")
    annotationProcessor("androidx.room:room-compiler:2.5.2")
    /** 闪屏页面 */
    implementation ("androidx.core:core-splashscreen:1.0.0-alpha01")

    implementation ("com.google.code.gson:gson:2.8.9")
    //compile 'com.alibaba:fastjson:2.0.19.android'

    implementation ("androidx.recyclerview:recyclerview:1.1.0")
    /** 图片加载框架 Glide*/
    implementation ("com.github.bumptech.glide:glide:4.4.0")
    /** 消息发布订阅*/
    implementation ("org.greenrobot:eventbus:3.2.0")
    /** 处理图片形状*/
    implementation ("jp.wasabeef:glide-transformations:4.3.0")
    // If you want to use the GPU Filters
    implementation ("jp.co.cyberagent.android:gpuimage:2.1.0")
    /** okhttp 框架*/
    implementation ("com.squareup.okhttp3:okhttp:3.12.0")
    /** 音频视频缓冲 */
    //implementation ("com.danikula:videocache:2.7.1")
    /** 图片裁剪 */
    implementation ("com.github.yalantis:ucrop:2.2.6")
    /** Toast新样式 */
    implementation ("com.github.GrenderG:Toasty:1.5.2")
    /** 响应式框架 异步数据流处理 */
    implementation ("io.reactivex.rxjava3:rxjava:3.1.0")
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.0")

    /** 崩溃异常处理 框架*/
    implementation ("cat.ereza:customactivityoncrash:2.3.0")

    /** tabLayout */
    implementation ("com.google.android.material:material:1.1.0")
    //implementation 'com.lzp:FlycoTabLayoutZ:1.3.3'

    /** 新音频框架 */
    implementation ("androidx.media3:media3-exoplayer:1.0.0")
    implementation ("androidx.media3:media3-ui:1.0.0")
    implementation ("androidx.media:media:1.4.3")
    implementation ("androidx.media3:media3-session:1.0.0")
    /** 磨砂背景处理 */
//    implementation 'com.eightbitlab:blurview:1.6.3'
//    implementation 'com.github.centerzx:ShapeBlurView:1.0.5'
    implementation ("com.github.Dimezis:BlurView:version-2.0.6")


    /** 内存泄露查询工具 使用后安装app之后会出现检测软件 */
    /*debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.7'
    releaseImplementation 'com.squareup.leakcanary:leakcanary-android-no-op:2.7'*/

}