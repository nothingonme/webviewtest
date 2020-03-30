package com.koubei.webviewtest;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String URL_BAIDU = "https://www.baidu.com/";
    private static final String URL_PAY = "https://market.m.taobao.com/app/alsc-saas/paying-pos-sdk/index.html/";

    private RadioGroup mPageRadioGroup;

    private WebPageFragment mCachedWebPageFragment;

    private WebPageFragment mCreateWebPageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_cache_no_delay).setOnClickListener(this);
        findViewById(R.id.btn_no_cache_no_delay).setOnClickListener(this);
        findViewById(R.id.btn_cache_delay).setOnClickListener(this);
        findViewById(R.id.btn_no_cache_delay).setOnClickListener(this);

        mPageRadioGroup = findViewById(R.id.rg_page);

        // add cached web page first
        mCachedWebPageFragment = WebPageFragment.newInstance(null, "CACHED");
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.root_view, mCachedWebPageFragment, null)
                .hide(mCachedWebPageFragment)
                .commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCachedWebPageFragment.isVisible()) {
                // hide cached webview
                getSupportFragmentManager().beginTransaction().hide(mCachedWebPageFragment).commit();
                return true;
            } else if (mCreateWebPageFragment != null) {
                // remove web page
                getSupportFragmentManager().beginTransaction().remove(mCreateWebPageFragment).commit();
                mCreateWebPageFragment = null;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        final boolean cached;
        final boolean delayed;
        final int id = v.getId();
        if (id == R.id.btn_cache_delay) {
            cached = true;
            delayed = true;
        } else if (id == R.id.btn_no_cache_delay) {
            cached = false;
            delayed = true;
        } else if (id == R.id.btn_no_cache_no_delay) {
            cached = false;
            delayed = false;
        } else if (id == R.id.btn_cache_no_delay) {
            cached = true;
            delayed =false;
        } else {
            cached = delayed = false;
        }
        String page = mPageRadioGroup.getCheckedRadioButtonId() == R.id.rb_baidu ? URL_BAIDU : URL_PAY;
        openUrl(page, cached, delayed);
    }

    private void openUrl(final String page, final boolean cached, final boolean delayed) {
        if (!cached) {
            FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
            if (mCreateWebPageFragment != null) {
                tr.remove(mCreateWebPageFragment);
            }
            mCreateWebPageFragment = WebPageFragment.newInstance(page, "NOT_CACHE", !delayed);
            tr.add(R.id.root_view, mCreateWebPageFragment, null).hide(mCreateWebPageFragment).commit();
        } else {
            mCachedWebPageFragment.loadPage(page, !delayed);
        }
    }

}
