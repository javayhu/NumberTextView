package me.javayhu.numbertextview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import me.javayhu.lib.NumberTextView;

public class MainActivity extends AppCompatActivity {

    private NumberTextView mNumberTextView;
    private int mClickCount = -1;
    private boolean[] mOperations = new boolean[]{true, true, true, true, false, false, false, false};
    //private int[] mColors = new int[]{Color.CYAN, Color.GREEN, Color.BLUE, Color.RED, Color.DKGRAY, Color.MAGENTA, Color.BLACK};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNumberTextView = (NumberTextView) findViewById(R.id.numberTextView);
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
    }
}
