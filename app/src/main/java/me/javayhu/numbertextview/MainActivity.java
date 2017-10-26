package me.javayhu.numbertextview;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import me.javayhu.lib.NumberTextView;

public class MainActivity extends AppCompatActivity {

    private NumberTextView mNumberTextView1;
    private NumberTextView mNumberTextView2;
    private NumberTextView mNumberTextView3;

    private boolean isNight = false;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = getSharedPreferences("demo", MODE_PRIVATE);
        isNight = mSharedPreferences.getBoolean("night", false);

        mNumberTextView1 = (NumberTextView) findViewById(R.id.numberTextView1);
        mNumberTextView1.setCount(98);
        mNumberTextView1.setOnClickListener(new NumberClickListener());

        mNumberTextView2 = (NumberTextView) findViewById(R.id.numberTextView2);
        mNumberTextView2.setCount(998);
        mNumberTextView2.setOnClickListener(new NumberClickListener());

        mNumberTextView3 = (NumberTextView) findViewById(R.id.numberTextView3);
        mNumberTextView3.setCount(9998);
        mNumberTextView3.setOnClickListener(new NumberClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.changeTheme) {
            changeTheme();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeTheme() {
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            mSharedPreferences.edit().putBoolean("night", false).commit();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            mSharedPreferences.edit().putBoolean("night", true).commit();
        }
        recreate();
    }

    private class NumberClickListener implements View.OnClickListener {

        private int mClickCount = -1;
        private boolean[] mOperations = new boolean[]{true, true, true, true, false, false, false, false};

        @Override
        public void onClick(View v) {
            if (!(v instanceof NumberTextView)) {
                return;
            }
            NumberTextView numberTextView = (NumberTextView) v;
            ++mClickCount;
            if (mOperations[mClickCount % mOperations.length]) {
                numberTextView.plusOne();
            } else {
                numberTextView.minusOne();
            }
        }
    }

}
