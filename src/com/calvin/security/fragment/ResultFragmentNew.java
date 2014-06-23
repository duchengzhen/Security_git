package com.calvin.security.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.calvin.security.R;
import com.calvin.security.ui.LostProtectedActivity;
import com.calvin.security.ui.ProcessManagerActivity;

/**
 * Created by calvin on 2014/6/19.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ResultFragmentNew extends Fragment implements View.OnClickListener{
    private SharedPreferences sp;
    private RelativeLayout rlResultAnti;
    private RelativeLayout rlResultOpt;
    private RelativeLayout rlResultedAnti;
    private RelativeLayout rlResultedOpt;
    private IFragmentListener fragmentListener;
    private Button btnBack;
    private Button btnAnti;
    private Button btnOpt;


    @Override
    public void onAttach(Activity activity) {
        try {
            fragmentListener= (IFragmentListener) activity;
        } catch (Exception e) {
            throw new ClassCastException("必须实现IFragmentListener接口");
        }
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.result_scanner_fragment, container, false);
        //TODO 设置处理逻辑
        btnBack = (Button) rootView.findViewById(R.id.btn_result_back);
        btnBack.setOnClickListener(this);
        btnAnti= (Button) rootView.findViewById(R.id.btn_result_anti);
        btnAnti.setOnClickListener(this);
        btnOpt= (Button) rootView.findViewById(R.id.btn_result_opt);
        btnOpt.setOnClickListener(this);

        rlResultAnti = (RelativeLayout) rootView.findViewById(R.id.rl_result_anti);
        rlResultOpt = (RelativeLayout) rootView.findViewById(R.id.rl_result_opt);
        rlResultedAnti = (RelativeLayout) rootView.findViewById(R.id.rl_resulted_anti);
        rlResultedOpt = (RelativeLayout) rootView.findViewById(R.id.rl_resulted_opt);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Animation rightInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_right_in);
        rlResultOpt.startAnimation(rightInAnim);
        rlResultOpt.setVisibility(View.VISIBLE);

        sp=getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        String number = sp.getString("number", "");
        if(TextUtils.isEmpty(number)){
            rlResultAnti.startAnimation(rightInAnim);
            rlResultAnti.setVisibility(View.VISIBLE);
        }else{
            rlResultedAnti.startAnimation(rightInAnim);
            rlResultedAnti.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_result_back://返回按钮事件监听
                fragmentListener.onViewClick(v);
                break;
            case R.id.btn_result_anti:
                Intent intent = new Intent(getActivity(), LostProtectedActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_result_opt:
                Intent processIntent = new Intent(getActivity(), ProcessManagerActivity.class);
                startActivity(processIntent);
                getActivity().overridePendingTransition(R.anim.translate_up_in, R.anim.translate_up_out);
                break;
            default:
                break;
        }
    }
}
