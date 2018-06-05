package com.will.weiyue.ui.news.presenter;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.will.weiyue.bean.Artical;
import com.will.weiyue.bean.NewsDetail;
import com.will.weiyue.net.BaseObserver;
import com.will.weiyue.net.NewsApi;
import com.will.weiyue.net.NewsUtils;
import com.will.weiyue.net.RxSchedulers;
import com.will.weiyue.ui.base.BasePresenter;
import com.will.weiyue.ui.news.contract.DetailContract;
import com.will.weiyue.utils.DateUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;


/**
 * desc: .
 * author: Will .
 * date: 2017/9/8 .
 */
public class DetailPresenter extends BasePresenter<DetailContract.View> implements DetailContract.Presenter {
    private static final String TAG = "DetailPresenter";

    NewsApi mNewsApi;

    @Inject
    public DetailPresenter(NewsApi newsApi) {
        this.mNewsApi = newsApi;
    }

    @Override
    public void getData(final String id, final String action, int pullNum) {

        AVQuery<AVObject> avQuery = new AVQuery<>("Artical");
        avQuery.setLimit(20);
        avQuery.orderByDescending("RecordId");
        if (!id.equals("all")) {
            avQuery.whereContains("ArticalKeyWords", id);
        }
        /*
        if (action.equals(NewsApi.ACTION_UP)) {
            avQuery.whereLessThan("RecordId", pullNum);
        }
        */
        avQuery.findInBackground(new FindCallback<AVObject>(){
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {

                    ArrayList<NewsDetail.ItemBean> articals = new ArrayList<NewsDetail.ItemBean>();
                    for (AVObject o:list) {
                        final String title = o.get("Title") == null ? "" : o.get("Title").toString();
                        final String description = o.get("Description") == null ? "" : o.get("Description").toString();
                        final String link = o.get("Link") == null ? "" : o.get("Link").toString();
                        final String imageUrl = o.get("ImageUrl") == null ? "" : o.get("ImageUrl").toString();
                        final String enclosure = o.get("Enclosure") == null ? "" : o.get("Enclosure").toString();
                        final String PubDate = o.get("PubDate") == null ? "" : o.get("PubDate").toString();
                        final String Category = o.get("Category") == null ? "" : o.get("Category").toString();
                        final String Comments = o.get("Comments") == null ? "" : o.get("Comments").toString();
                        final String Author = o.get("Author") == null ? "" : o.get("Author").toString();
                        final String Source = o.get("Source") == null ? "" : o.get("Source").toString();
                        final String SourceLink = o.get("SourceLink") == null ? "" : o.get("SourceLink").toString();
                        final String ArticalKeyWords = o.get("ArticalKeyWords") == null ? "" : o.get("ArticalKeyWords").toString();


                        Date date = DateUtil.string2Date(PubDate, "EEE MMM dd HH:mm:ss z yyyy");
                        String updateTime = DateUtil.date2String(date.getTime(),"yyyy/MM/dd HH:mm:ss");
                        NewsDetail.ItemBean itemBean = new NewsDetail.ItemBean();
                        itemBean.setId(o.getObjectId());
                        itemBean.itemType = NewsDetail.ItemBean.TYPE_DOC_TITLEIMG;
                        itemBean.setTitle(title);
                        itemBean.setComments(Author);
                        itemBean.setThumbnail(imageUrl);
                        itemBean.setUpdateTime(updateTime);
                        itemBean.setSource(Source);

                        NewsDetail.ItemBean.LinkBean linkBean = new NewsDetail.ItemBean.LinkBean();
                        linkBean.setType("doc");
                        linkBean.setWeburl(link);

                        itemBean.setLink(linkBean);

                        NewsDetail.ItemBean.StyleBean styleBean = new NewsDetail.ItemBean.StyleBean();
                        styleBean.setView("titleimg");
                        styleBean.setBackreason(Arrays.asList("不感兴趣", "不想看", "旧闻、看过了", "内容质量差"));
                        itemBean.setStyle(styleBean);

                        articals.add(itemBean);
                    }

                    if (!action.equals(NewsApi.ACTION_UP)) {
                        mView.loadData(articals);
                    } else {
                        mView.loadMoreData(articals);
                    }

                } else {
                    Log.i(TAG, "onFail: " + e.getMessage().toString());
                    if (!action.equals(NewsApi.ACTION_UP)) {
                        mView.loadData(null);
                    } else {
                        mView.loadMoreData(null);
                    }
                }
            }
        });

        /*
        mNewsApi.getNewsDetail(id, action, pullNum)
                .compose(RxSchedulers.<List<NewsDetail>>applySchedulers())
                .map(new Function<List<NewsDetail>, NewsDetail>() {
                    @Override
                    public NewsDetail apply(List<NewsDetail> newsDetails) throws Exception {
                        for (NewsDetail newsDetail : newsDetails) {
                            if (NewsUtils.isBannerNews(newsDetail)) {
                                mView.loadBannerData(newsDetail);
                            }
                            if (NewsUtils.isTopNews(newsDetail)) {
                                mView.loadTopNewsData(newsDetail);
                            }
                        }
                        return newsDetails.get(0);
                    }
                })
                .map(new Function<NewsDetail, List<NewsDetail.ItemBean>>() {
                    @Override
                    public List<NewsDetail.ItemBean> apply(@NonNull NewsDetail newsDetail) throws Exception {
                        Iterator<NewsDetail.ItemBean> iterator = newsDetail.getItem().iterator();
                        while (iterator.hasNext()) {
                            try {
                                NewsDetail.ItemBean bean = iterator.next();
                                if (bean.getType().equals(NewsUtils.TYPE_DOC)) {
                                    if (bean.getStyle().getView() != null) {
                                        if (bean.getStyle().getView().equals(NewsUtils.VIEW_TITLEIMG)) {
                                            bean.itemType = NewsDetail.ItemBean.TYPE_DOC_TITLEIMG;
                                        } else {
                                            bean.itemType = NewsDetail.ItemBean.TYPE_DOC_SLIDEIMG;
                                        }
                                    }
                                } else if (bean.getType().equals(NewsUtils.TYPE_ADVERT)) {
                                    if (bean.getStyle() != null) {
                                        if (bean.getStyle().getView().equals(NewsUtils.VIEW_TITLEIMG)) {
                                            bean.itemType = NewsDetail.ItemBean.TYPE_ADVERT_TITLEIMG;
                                        } else if (bean.getStyle().getView().equals(NewsUtils.VIEW_SLIDEIMG)) {
                                            bean.itemType = NewsDetail.ItemBean.TYPE_ADVERT_SLIDEIMG;
                                        } else {
                                            bean.itemType = NewsDetail.ItemBean.TYPE_ADVERT_LONGIMG;
                                        }
                                    } else {
                                        //bean.itemType = NewsDetail.ItemBean.TYPE_ADVERT_TITLEIMG;
                                        iterator.remove();
                                    }
                                } else if (bean.getType().equals(NewsUtils.TYPE_SLIDE)) {
                                    if (bean.getLink().getType().equals("doc")) {
                                        if (bean.getStyle().getView().equals(NewsUtils.VIEW_SLIDEIMG)) {
                                            bean.itemType = NewsDetail.ItemBean.TYPE_DOC_SLIDEIMG;
                                        } else {
                                            bean.itemType = NewsDetail.ItemBean.TYPE_DOC_TITLEIMG;
                                        }
                                    } else {
                                        bean.itemType = NewsDetail.ItemBean.TYPE_SLIDE;
                                    }
                                } else if (bean.getType().equals(NewsUtils.TYPE_PHVIDEO)) {
                                    bean.itemType = NewsDetail.ItemBean.TYPE_PHVIDEO;
                                } else {
                                    // 凤凰新闻 类型比较多，目前只处理能处理的类型
                                    iterator.remove();
                                }
                            } catch (Exception e) {
                                iterator.remove();
                                e.printStackTrace();
                            }
                        }
                        return newsDetail.getItem();
                    }
                })
                .compose(mView.<List<NewsDetail.ItemBean>>bindToLife())
                .subscribe(new BaseObserver<List<NewsDetail.ItemBean>>() {
                    @Override
                    public void onSuccess(List<NewsDetail.ItemBean> itemBeen) {
                        if (!action.equals(NewsApi.ACTION_UP)) {
                            mView.loadData(itemBeen);
                        } else {
                            mView.loadMoreData(itemBeen);
                        }
                    }

                    @Override
                    public void onFail(Throwable e) {
                        Log.i(TAG, "onFail: " + e.getMessage().toString());
                        if (!action.equals(NewsApi.ACTION_UP)) {
                            mView.loadData(null);
                        } else {
                            mView.loadMoreData(null);
                        }
                    }
                });*/
    }
}
