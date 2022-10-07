package org.foliage.zdjxzz.page.web;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.foliage.zdjxzz.R;
import org.foliage.zdjxzz.base.BaseFragmentActivity;
import org.foliage.zdjxzz.infrastructure.utils.IntentTool;

import butterknife.BindView;

public class WebViewActivity extends BaseFragmentActivity {

    @BindView(R.id.webview)
    WebView vWebView;

    @BindView(R.id.progress)
    LinearProgressIndicator vProgress;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_web_view;
    }

    @Override
    protected String getActionBarTitle() {
        return "加载中...";
    }

    @Override
    protected boolean hasBindEventBus() {
        return false;
    }

    @Override
    protected void initView() {
        super.initView();

        Intent intent = this.getIntent();
        String url = intent.getStringExtra("url");
        vWebView.loadUrl(url);
    }

    @Override
    protected void setListener() {
        super.setListener();
        vWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title != null) {
                    ActionBar actionBar = getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setTitle(title);
                    }
                }
            }
        });
        vWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (vProgress != null)
                    vProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        IntentTool.startActivity(context, intent);
    }
}
