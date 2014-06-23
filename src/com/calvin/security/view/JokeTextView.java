package com.calvin.security.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.calvin.security.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于滚动显示笑话的空间
 * Created by calvin on 2014/6/19.
 */
public class JokeTextView extends TextSwitcher implements ViewSwitcher.ViewFactory {
    private long time=6000;//显示的时间
    private List<String> jokes;//笑话文本的集合
    private int index=0;//集合的下标

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    setText(jokes.get(index));
                    break;
            }
        }
    };

    public JokeTextView(Context context) {
        super(context);
        init();
    }

    public JokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        if(jokes==null){
            jokes=new ArrayList<String>();
            jokes.add(0,"安全管家正在保护你的手机");
        }

        setFactory(this);

        Animation inAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.joke_in_anim);
        Animation outAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.joke_out_anim);
        setInAnimation(inAnimation);
        setOutAnimation(outAnimation);
    }

    @Override
    public View makeView() {
        View view = View.inflate(getContext(),R.layout.joke_textview,null);
        TextView tvJoke= (TextView) view.findViewById(R.id.tv_joke);
        tvJoke.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        tvJoke.setGravity(Gravity.CENTER_VERTICAL);
        return tvJoke;
    }

    class showThread implements Runnable{
        @Override
        public void run() {
            while(true){
                if(jokes!=null&&jokes.size()!=0)
                    mHandler.sendEmptyMessage(1);
                try {
                    Thread.sleep(time);
                    index++;
                    if(index==jokes.size()){
                        index=0;
                    }
                } catch (InterruptedException e) {}
            }
        }
    }

    public void startShow(){
        new Thread(new showThread()).start();
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setJokes(List<String> jokes) {
        this.jokes = jokes;
    }
}
