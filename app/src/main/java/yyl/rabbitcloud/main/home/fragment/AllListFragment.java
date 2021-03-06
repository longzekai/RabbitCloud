package yyl.rabbitcloud.main.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.squareup.haha.perflib.Main;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import yyl.rabbitcloud.App;
import yyl.rabbitcloud.R;
import yyl.rabbitcloud.base.BaseFragment;
import yyl.rabbitcloud.di.component.AppComponent;
import yyl.rabbitcloud.di.component.DaggerFragmentComponent;
import yyl.rabbitcloud.livebycate.LiveTypeDetailActivity;
import yyl.rabbitcloud.livebycate.LiveTypeItemAdapter;
import yyl.rabbitcloud.livebycate.bean.LiveRoomListBean;
import yyl.rabbitcloud.liveroom.LiveRoomActivity;
import yyl.rabbitcloud.main.MainFragment;
import yyl.rabbitcloud.main.home.AllLiveListContract;
import yyl.rabbitcloud.main.home.AllLiveListPresenter;
import yyl.rabbitcloud.main.home.bean.AllLiveListBean;
import yyl.rabbitcloud.recyclerview.EndlessRecyclerOnScrollListener;
import yyl.rabbitcloud.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import yyl.rabbitcloud.recyclerview.HeaderSpanSizeLookup;
import yyl.rabbitcloud.recyclerview.LoadingFooter;
import yyl.rabbitcloud.recyclerview.RecyclerViewStateUtils;
import yyl.rabbitcloud.util.ScreenHelper;
import yyl.rabbitcloud.widget.SpaceItemDecoration;

/**
 * Created by yyl on 2017/6/18.
 */

public class AllListFragment extends MainFragment implements AllLiveListContract.View {

    @BindView(R.id.alllive_refresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.alllive_recyclerview)
    RecyclerView mRecyclerView;

    @Inject
    AllLiveListPresenter mListPresenter;

    //更新
    private final int REFRESH_STATE_UPDATE = 0;
    //加载更多
    private final int REFRESH_STATE_MORE = 1;
    //当前状态
    private int mRefreshState = 0;

    private int pagerNow = 1;
    private int pagerNum = 20;

    private int loadedItemDataNum;

    private HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter;

    //item数据
    private List<AllLiveListBean.DataBean.ItemsBean> mItemsBeen;
    private LiveTypeItemAdapter mTypeItemAdapter;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_all_list;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerFragmentComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        mRefreshState = REFRESH_STATE_UPDATE;
        pagerNow = 1;
        mListPresenter.getAllLiveList(pagerNow, pagerNum);
    }

    @Override
    protected void initUi() {

    }

    @Override
    protected void initData() {
        mItemsBeen = new ArrayList<>();
        mTypeItemAdapter = new LiveTypeItemAdapter(mMainActivity, LiveTypeItemAdapter.TYPE_ALL);

        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter
                (mTypeItemAdapter);
        mRecyclerView.setAdapter(mHeaderAndFooterRecyclerViewAdapter);

        GridLayoutManager manager = new GridLayoutManager(mMainActivity, 2);
        manager.setSpanSizeLookup(new HeaderSpanSizeLookup((HeaderAndFooterRecyclerViewAdapter)
                mRecyclerView.getAdapter(), manager.getSpanCount()));
        mRecyclerView.setLayoutManager(manager);

        int spacing = ScreenHelper.dp2px(mMainActivity, 15);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(2, spacing, spacing, false));

        mListPresenter.attachView(this);
        mListPresenter.getAllLiveList(pagerNow, pagerNum);
    }

    @Override
    protected void initListener() {
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        mTypeItemAdapter.setItemClickListener(new LiveTypeItemAdapter.
                OnLiveTypeItemClickListener() {

            @Override
            public void onClick(int position) {
                //添加了header后，RecyclerView 子item的点击位置往后偏移header的数量个位置
                AllLiveListBean.DataBean.ItemsBean bean;
                if (mHeaderAndFooterRecyclerViewAdapter.getHeaderViewsCount() != 0) {
                    bean = mItemsBeen.get(position - mHeaderAndFooterRecyclerViewAdapter.getHeaderViewsCount());
                } else {
                    bean = mItemsBeen.get(position);
                }
                LiveRoomActivity.toLiveRoomActivity(mMainActivity, bean.getId());
            }
        });

        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    private EndlessRecyclerOnScrollListener mOnScrollListener = new
            EndlessRecyclerOnScrollListener() {

                @Override
                public void onLoadNextPage(View view) {
                    super.onLoadNextPage(view);

                    LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(mRecyclerView);
                    if (state == LoadingFooter.State.Loading) {
                        return;
                    }

                    if (loadedItemDataNum < pagerNum) {
                        //the end
                        RecyclerViewStateUtils.setFooterViewState(App.getAppInstance(),
                                mRecyclerView, pagerNum, LoadingFooter.State.TheEnd, null);
                    } else {
                        // loading more
                        mRefreshState = REFRESH_STATE_MORE;
                        pagerNow = pagerNow + 1;
                        mListPresenter.getAllLiveList(pagerNow, pagerNum);
                        RecyclerViewStateUtils.setFooterViewState(App.getAppInstance(),
                                mRecyclerView, pagerNum, LoadingFooter.State.Loading, null);
                    }
                }
            };

    @Override
    public void showAllLiveListData(List<AllLiveListBean.DataBean.ItemsBean> data) {
        loadedItemDataNum = data.size();
        mRefreshLayout.setRefreshing(false);
        RecyclerViewStateUtils.setFooterViewState(mRecyclerView, LoadingFooter.State.Normal);
        switch (mRefreshState) {
            case REFRESH_STATE_UPDATE:
                mItemsBeen.clear();
                mItemsBeen.addAll(data);
                break;
            case REFRESH_STATE_MORE:
                mItemsBeen.addAll(data);
                break;
        }

        mTypeItemAdapter.setAllList(mItemsBeen);
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListPresenter != null) {
            mListPresenter.detachView();
        }
    }
}
