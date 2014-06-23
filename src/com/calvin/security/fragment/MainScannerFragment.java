package com.calvin.security.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.calvin.security.R;
import com.calvin.security.view.TasksCompletedView;

/**
 * Created by calvin on 2014/6/21.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainScannerFragment extends Fragment {

    private IFragmentListener fragmentListener;
    private RelativeLayout rlStatusTaskView;
    private LinearLayout llStatusButton;
    /**圆形processbar上的文字*/
    private TextView tvStatusInfo;
    private TasksCompletedView taskView;
    private Button btnScan;

    @Override
    public void onAttach(Activity activity) {
        try {
            fragmentListener= (IFragmentListener) activity;
        } catch (Exception e) {
            throw new ClassCastException("必须实现IFragmentListener几接口");
        }
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_scanner_fragment, container, false);
        rlStatusTaskView= (RelativeLayout) rootView.findViewById(R.id.rl_status_taskview);
        llStatusButton= (LinearLayout) rootView.findViewById(R.id.ll_status_btn);
        tvStatusInfo= (TextView) rootView.findViewById(R.id.tv_main_status_info);
        taskView= (TasksCompletedView) rootView.findViewById(R.id.taskview);
        btnScan= (Button) rootView.findViewById(R.id.btn_status_scan);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //设置监听
        btnScan.setOnClickListener(new ScannerListener());
    }

    private class ScannerListener implements View.OnClickListener {

        /**扫描按钮点击事件*/
        @Override
        public void onClick(View v) {
            //隐藏文字显示区域
            llStatusButton.setVisibility(View.INVISIBLE);
            //位移动画,到父控件中间
            TranslateAnimation translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_PARENT, 0.275f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            //动画时长ms
            translateAnimation.setDuration(500);
            //留在动画最后
            translateAnimation.setFillAfter(true);
            //启动动画
            rlStatusTaskView.startAnimation(translateAnimation);
            //将processbar上文字清除
            tvStatusInfo.setText("");

            //扫描开始
            new Thread(){
                @Override
                public void run() {
                    int i=0;
                    while(i<100){
                        i++;
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {e.printStackTrace();}
                        taskView.setProgress(i);
                    }
                }
            }.start();
            //调用接口方法
            fragmentListener.onViewClick(v);
        }
    }
}
