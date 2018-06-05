package com.will.weiyue.ui.news;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.will.weiyue.R;
import com.will.weiyue.bean.NewsArticleBean;
import com.will.weiyue.component.ApplicationComponent;
import com.will.weiyue.component.DaggerHttpComponent;
import com.will.weiyue.ui.base.BaseActivity;
import com.will.weiyue.ui.news.contract.ArticleReadContract;
import com.will.weiyue.ui.news.presenter.ArticleReadPresenter;
import com.will.weiyue.utils.DateUtil;
import com.will.weiyue.widget.ObservableScrollView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * desc: .
 * author: Will .
 * date: 2017/9/21 .
 */
public class ArticleReadActivity extends BaseActivity<ArticleReadPresenter> implements ArticleReadContract.View {
    private static final String TAG = "ArticleReadActivity";
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.iv_logo)
    ImageView mIvLogo;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_updateTime)
    TextView mTvUpdateTime;
    //    @BindView(R.id.tv_content)
//    TextView mTvContent;
    @BindView(R.id.webview)
    WebView mWebView;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.ScrollView)
    ObservableScrollView mScrollView;
    @BindView(R.id.ConstraintLayout)
    RelativeLayout mConstraintLayout;
    @BindView(R.id.rl_top)
    RelativeLayout mRlTop;
    @BindView(R.id.iv_topLogo)
    ImageView mIvTopLogo;
    @BindView(R.id.tv_topname)
    TextView mTvTopName;
    @BindView(R.id.tv_TopUpdateTime)
    TextView mTvTopUpdateTime;

    private Boolean loaded = false;

    @Override
    public int getContentLayout() {
        return R.layout.activity_artcleread;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {
        DaggerHttpComponent.builder()
                .applicationComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {
        setWebViewSetting();
        setStatusBarColor(Color.parseColor("#BDBDBD"), 30);

        mScrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int scrollY, int oldx, int oldy) {
                if (scrollY > mConstraintLayout.getHeight()) {
                    mRlTop.setVisibility(View.VISIBLE);
                } else {
                    mRlTop.setVisibility(View.GONE);

                }
            }
        });
    }

    private void setWebViewSetting() {
        addjs(mWebView);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setVerticalScrollbarOverlay(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setHorizontalScrollbarOverlay(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.loadUrl("file:///android_asset/ifeng/post_detail.html");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (loaded) {
                    return;
                }

                loaded = true;
                onRetry();
            }
        });
    }

    @Override
    public void initData() {
    }

    @Override
    public void onRetry() {
        String aid = getIntent().getStringExtra("aid");
        if (aid != null) {
            mPresenter.getData(aid);
        } else {
            NewsArticleBean articleBean = new NewsArticleBean();
            NewsArticleBean.BodyBean bodyBean = new NewsArticleBean.BodyBean();
            bodyBean.setTitle(getIntent().getStringExtra("title"));
            bodyBean.setUpdateTime(getIntent().getStringExtra("updateTime"));
            bodyBean.setSource(getIntent().getStringExtra("source"));
            bodyBean.setAuthor(getIntent().getStringExtra("author"));
            bodyBean.setWwwurl(getIntent().getStringExtra("url"));
            articleBean.setBody(bodyBean);
            loadData(articleBean);
        }
    }

    private static String getContent(String regex,String text) {
        String content = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while(matcher.find()) {
            content = matcher.group(1).toString();
        }
        return content;
    }

    @Override
    public void loadData(final NewsArticleBean articleBean) {
        mTvTitle.setText(articleBean.getBody().getTitle());
        mTvUpdateTime.setText(DateUtil.getTimestampString(DateUtil.string2Date(articleBean.getBody().getUpdateTime(), "yyyy/MM/dd HH:mm:ss")));
        if (articleBean.getBody().getSubscribe() != null) {
            Glide.with(this).load(articleBean.getBody().getSubscribe().getLogo())
                    .apply(new RequestOptions()
                            .transform(new CircleCrop())
                            //.placeholder()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(mIvLogo);
            Glide.with(this).load(articleBean.getBody().getSubscribe().getLogo())
                    .apply(new RequestOptions()
                            .transform(new CircleCrop())
                            //.placeholder()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(mIvTopLogo);
            mTvTopName.setText(articleBean.getBody().getSubscribe().getCateSource());
            mTvName.setText(articleBean.getBody().getSubscribe().getCateSource());
            mTvTopUpdateTime.setText(articleBean.getBody().getSubscribe().getCatename());
        } else {
            mTvTopName.setText(articleBean.getBody().getSource());
            mTvName.setText(articleBean.getBody().getSource());
            mTvTopUpdateTime.setText(!TextUtils.isEmpty(articleBean.getBody().getAuthor()) ? articleBean.getBody().getAuthor() : articleBean.getBody().getEditorcode());
        }

        String aid = getIntent().getStringExtra("aid");
        if (aid != null) {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    final String content = articleBean.getBody().getText();
                    String url = "javascript:show_content(\'" + content + "\')";
                    mWebView.loadUrl(url);
                    showSuccess();
                }
            });
        } else {
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    Connection.Response response;
                    try {
                        String wwwurl = articleBean.getBody().getWwwurl();
                        Document doc = Jsoup.connect(wwwurl).userAgent("Mozilla").timeout(3000).get();
                        String content = "";
                        wwwurl = wwwurl.toLowerCase();
                        if (wwwurl.contains(".qq.com"))  {
                            //新闻正文正则
                            Element element = doc.getElementById("Cnt-Main-Article-QQ");
                            content = element.outerHtml();
                        } else if (wwwurl.contains(".geekpark.net")) {
                            // Element element = doc.getElementsByTag("article").first();
                            Element element = doc.getElementById("article-body");
                            content = element.outerHtml();
                        } else if (wwwurl.contains(".techweb.com.cn")) {
                            Element element = doc.getElementById("content");
                            content = element.outerHtml();
                        } else {
                            content = doc.body().outerHtml();
                        }

                        getIntent().putExtra("content", content);
                        mWebView.post(new Runnable() {
                            @Override
                            public void run() {
                                String content = getIntent().getStringExtra("content");
                                String url = "javascript:show_content(\'" + content + "\')";
                                mWebView.loadUrl(url);
                                showSuccess();
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        }
    }

    private void addjs(final WebView webview) {

        class JsObject {
            @JavascriptInterface
            public void jsFunctionimg(final String i) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "run: " + i);
                    }
                });

            }

        }
        webview.addJavascriptInterface(new JsObject(), "jscontrolimg");

    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }


}
