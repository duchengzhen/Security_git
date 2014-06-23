package com.calvin.security.ui;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.calvin.security.MyApplication;
import com.calvin.security.R;
import com.calvin.security.fragment.*;
import com.calvin.security.utils.HtmlParse;
import com.calvin.security.view.JokeTextView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import java.util.List;

public class MainActivity extends SlidingFragmentActivity implements IFragmentListener {
    private ImageView ivTitlebarNav;
    private ImageView ivTitlebarEmail;
    private SlidingMenu slidingMenu;

    private JokeTextView tvJoke;
    private List<String> jokes;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    //更新UI
                    tvJoke.setJokes(jokes);
                    tvJoke.startShow();
                    break;
            }
        }
    };
    private FragmentTransaction fragmentTransaction;
    private MainGridViewFragment mainGridViewFragment;
    private PopupWindow popupWindow;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //初始化一些数据
        init();
        initPopupWindow();

        new Thread(){
            @Override
            public void run() {
                jokes= HtmlParse.getJokes();
                handler.sendEmptyMessage(1);
            }
        }.start();

        //设置侧滑布局
        setBehindContentView(R.layout.nav_sliding_menu);
        //获得SlidingMenu布局
        slidingMenu = getSlidingMenu();
        slidingMenu.setBehindWidthRes(R.dimen.nav_menu_width);
        //setBehindOffsetRes();设置剩余宽度
        slidingMenu.setFadeDegree(0.35f);
        //设置滑动手势模式, 边缘
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        //设置slidingmenu内容
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //侧滑fragment布局
        NavigationMenuFragment navigationMenuFragment = new NavigationMenuFragment();
        //设置MainGridViewFragment布局
        mainGridViewFragment = new MainGridViewFragment();

        fragmentTransaction.replace(R.id.fl_sliding_menu, navigationMenuFragment);
        fragmentTransaction.replace(R.id.fl_main_gridview, mainGridViewFragment);
        fragmentTransaction.commit();

        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //头部scanner区域
        MainScannerFragment mainScannerFragment = new MainScannerFragment();
        transaction.replace(R.id.ll_main_status, mainScannerFragment);
        transaction.commit();

    }

    private void initPopupWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.popupwindow, null);
        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        //popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //设置点击窗口外可消失
        popupWindow.setOutsideTouchable(true);//无用
        //为popupwindow设置动画
        popupWindow.setAnimationStyle(R.style.popup_anim_style);
        popupWindow.update();
        ivTitlebarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }else{
                    //设置显示的偏移量,正好处于程序标题栏+状态栏的正下方
                    int yOff=(int) getResources().getDimension(R.dimen.custom_titlebar_height)+getStatusBarHeight();
                    //popupWindow.showAsDropDown(view,0, yOff);
                    popupWindow.showAtLocation(view,Gravity.NO_GRAVITY,0,yOff);
                }
            }
        });
        //文本内容
        final EditText etContent= (EditText) view.findViewById(R.id.et_popup_content);
        //发送按钮
        Button btnSend= (Button) view.findViewById(R.id.btn_popup_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etContent.getText())){
                    Toast.makeText(MainActivity.this,"还是写两句吧...",Toast.LENGTH_SHORT).show();
                }else{
                    popupWindow.dismiss();
                    Toast.makeText(MainActivity.this,"谢谢你和反馈!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 初始化工作
     * sp=getSharedPreferences("config", MODE_PRIVATE);
     * gridView=(GridView) findViewById(R.id.gv_main);
     * adapter=new MainUiAdapter(this);
     */
    private void init() {
        tvJoke= (JokeTextView) findViewById(R.id.joketextview);
        ivTitlebarNav = (ImageView) findViewById(R.id.iv_titlebar_nav);
        //顶部导航按钮,点击事件
        ivTitlebarNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.showMenu(true);
            }
        });

        //发送邮件反馈
        ivTitlebarEmail= (ImageView) findViewById(R.id.iv_titlebar_mail);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onViewClick(View view) {
        switch (view.getId()){
            case R.id.iv_nav_close: //关闭slidingmenu事件
                slidingMenu.toggle(true);
                break;
            case R.id.btn_status_scan: //扫描按钮
                ResultFragmentNew resultFragmentNew = new ResultFragmentNew();
                showFragment(resultFragmentNew,R.id.fl_main_gridview);
                break;
            case R.id.btn_result_back://结果区返回按钮
                //扫描区域复原
                MainScannerFragment mainScannerFragment = new MainScannerFragment();
                showFragment(mainScannerFragment,R.id.ll_main_status);
                //恢复gridview
                MainGridViewFragmentNew mainGridViewFragmentNew = new MainGridViewFragmentNew();
                showFragment(mainGridViewFragmentNew,R.id.fl_main_gridview);
                break;
            case R.id.btn_result_anti://扫描结果防盗

                break;
            default:
                break;
        }
    }

    /**
     * 动画显示扫描结果fragment
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showFragment(Fragment fragment,int viewId) {
        //替换扫描结果fragment
        android.app.FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();

            /*ResultFragment resultFragment = new ResultFragment();
            //设置切换(移动)动画
            ObjectAnimator objAnimIn = ObjectAnimator.ofFloat(resultFragment,"Y",0);
            //objAnimIn.setInterpolator(new AccelerateDecelerateInterpolator());//动画加速-->减速
            objAnimIn.setDuration(2000);//时长
            //objAnimIn.start();
            //淡出动画
            ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(mainGridViewFragment, "alpha", 1.0f, 0.5f);
            alphaAnim.setDuration(2000);*/

        //设置动画
        fragmentTransaction.setCustomAnimations(R.animator.slide_up_in,R.animator.fade_out);
        fragmentTransaction.replace(viewId,fragment);
        //加入回退栈
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * 获取状态栏的高度
     * @return
     */
    private int getStatusBarHeight(){
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }
}
