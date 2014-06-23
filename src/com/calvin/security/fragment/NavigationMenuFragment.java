package com.calvin.security.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.calvin.security.R;

/**
 * Created by calvin on 2014/6/12.
 */
public class NavigationMenuFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout rlNavAboutItem;
    private TextView tvAboutContent;
    private CheckBox cbAboutShow;
    private ImageView ivNavClose;

    private IFragmentListener fragmentListener;

    @Override
    public void onAttach(Activity activity) {
        try {
            fragmentListener = (IFragmentListener) activity;
        } catch (Exception e) {
            throw new ClassCastException("必须实现OnNavClickListener接口");
        }
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_nav_fragment, container, false);
        rlNavAboutItem = (RelativeLayout) rootView.findViewById(R.id.rl_nav_about);
        ivNavClose= (ImageView) rootView.findViewById(R.id.iv_nav_close);
        ivNavClose.setOnClickListener(this);

        tvAboutContent = (TextView) rootView.findViewById(R.id.tv_nav_about_content);
        tvAboutContent.setVisibility(View.GONE);
        cbAboutShow = (CheckBox) rootView.findViewById(R.id.cbox_about_item);

        rlNavAboutItem.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_nav_about:
                if(!cbAboutShow.isChecked()){
                    tvAboutContent.setVisibility(View.VISIBLE);
                    cbAboutShow.setChecked(true);
                }else {
                    tvAboutContent.setVisibility(View.GONE);
                    cbAboutShow.setChecked(false);
                }
                break;
            //如果是关闭按钮,通过内部接口传递给出去
            case R.id.iv_nav_close:
                fragmentListener.onViewClick(v);
                break;

            default:

                //当点击其他按钮时候,任何情况都将说明文档收起来
                tvAboutContent.setVisibility(View.GONE);
                cbAboutShow.setChecked(false);


                break;
        }

    }
}
