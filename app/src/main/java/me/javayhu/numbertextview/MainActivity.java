package me.javayhu.numbertextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import me.javayhu.lib.NumberTextView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mContentContainer;

    private NumberTextView mNumberTextView;
    private int mClickCount = -1;
    private boolean[] mOperations = new boolean[]{true, true, true, true, false, false, false, false};
    //private int[] mColors = new int[]{Color.CYAN, Color.GREEN, Color.BLUE, Color.RED, Color.DKGRAY, Color.MAGENTA, Color.BLACK};

    private int mClickCount2 = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContentContainer = (LinearLayout) findViewById(R.id.content);

        mNumberTextView = (NumberTextView) findViewById(R.id.numberTextView);//这个NumberTextView会使用第二个构造方法
        mNumberTextView.setCount(98);
        mNumberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++mClickCount;
                if (mOperations[mClickCount % mOperations.length]) {
                    mNumberTextView.plusOne();
                } else {
                    mNumberTextView.minusOne();
                }
                //mNumberTextView.setTextColor(mColors[mClickCount % mColors.length]);//测试颜色变化
            }
        });

        //final NumberTextView numberTextView2 = new NumberTextView(this, null, 0, R.style.NumberTextViewStyle);//这个NumberTextView会使用第四个构造方法，但是在Android 5.0及以下版本的系统中会crash
        final NumberTextView numberTextView2 = new NumberTextView(this, null, R.style.Widget_NumberTextViewStyle);//这个NumberTextView会使用第三个构造方法
        numberTextView2.setCount(998);
        numberTextView2.setTextSize(60);
        numberTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++mClickCount2;
                if (mOperations[mClickCount2 % mOperations.length]) {
                    numberTextView2.plusOne();
                } else {
                    numberTextView2.minusOne();
                }
            }
        });
        mContentContainer.addView(numberTextView2, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
