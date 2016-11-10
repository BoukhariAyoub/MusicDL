package com.boukharist.musicdl.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.boukharist.musicdl.model.Item;
import com.boukharist.musicdl.R;
import com.boukharist.musicdl.networking.RestApi;
import com.boukharist.musicdl.networking.RestService;
import com.boukharist.musicdl.utils.Utils;
import com.boukharist.musicdl.ui.MyAudioPlayer;
import com.google.api.services.youtube.model.SearchResult;
import com.hugomatilla.audioplayerview.AudioPlayerView;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrateur on 18/12/2015.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> implements View.OnClickListener, AudioManager.OnAudioFocusChangeListener {


    private List<SearchResult> mResults;
    MyViewHolder mHolder;

    private final View vLoadingView;
    private final View vEmptyView;
    private final View vErrorView;

    public Activity mParentActivity;

    @Override
    public void onAudioFocusChange(int i) {
        Log.d("AudioFocus", "code = " + i);

    }

    @IntDef({STATE_NORMAL, STATE_LOADING, STATE_EMPTY, STATE_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public static final int STATE_NORMAL = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_EMPTY = 2;
    public static final int STATE_ERROR = 3;

    public static final int TYPE_LOADING = 1000;
    public static final int TYPE_EMPTY = 1001;
    public static final int TYPE_ERROR = 1002;


    private int state = STATE_NORMAL;
    public MyAudioPlayer mCurrentAudioPlayer;


    RecyclerViewAdapter(Activity parentActivity, @Nullable View loadingView, @Nullable View emptyView, @Nullable View errorView) {
        this.mResults = new ArrayList<>();
        mParentActivity = parentActivity;
        this.vLoadingView = loadingView;
        this.vEmptyView = emptyView;
        this.vErrorView = errorView;
    }


    public void setState(@State int state) {
        this.state = state;
        notifyDataSetChanged();
    }


    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        Utils.destroyPlayer(mCurrentAudioPlayer);
        mCurrentAudioPlayer = null;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case TYPE_LOADING:
                return new MyViewHolder(mParentActivity, this, vLoadingView, STATE_LOADING);
            case TYPE_EMPTY:
                return new MyViewHolder(mParentActivity, this, vEmptyView, STATE_EMPTY);
            case TYPE_ERROR:
                return new MyViewHolder(mParentActivity, this, vErrorView, STATE_ERROR);
        }
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_layout, viewGroup, false);
        mHolder = new MyViewHolder(mParentActivity, this, itemLayoutView, STATE_NORMAL);
        return mHolder;
    }

    public void setmCurrentAudioPlayer(MyAudioPlayer mCurrentAudioPlayer) {
        this.mCurrentAudioPlayer = mCurrentAudioPlayer;
    }

    public MyAudioPlayer getmCurrentAudioPlayer() {
        return mCurrentAudioPlayer;
    }

    @Override
    public int getItemViewType(int position) {
        switch (state) {
            case STATE_LOADING:
                return TYPE_LOADING;
            case STATE_EMPTY:
                return TYPE_EMPTY;
            case STATE_ERROR:
                return TYPE_ERROR;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        switch (state) {
            case STATE_LOADING:
                onBindLoadingViewHolder(holder, position);
                break;
            case STATE_EMPTY:
                onBindEmptyViewHolder(holder, position);
                break;
            case STATE_ERROR:
                onBindErrorViewHolder(holder, position);
                break;
            default:
                holder.bindPost(mResults.get(position));
                break;
        }
    }

    private void onBindErrorViewHolder(MyViewHolder holder, int position) {
    }

    private void onBindEmptyViewHolder(MyViewHolder holder, int position) {
    }

    private void onBindLoadingViewHolder(MyViewHolder holder, int position) {
        holder.startLoading();
    }


    @Override
    public int getItemCount() {
        switch (state) {
            case STATE_LOADING:
            case STATE_EMPTY:
            case STATE_ERROR:
                return 1;
        }
        return mResults.size();
    }

   public void addItem(SearchResult item) {
        mResults.add(item);
        notifyItemInserted(mResults.size() - 1);
    }

    @Override
    public void onClick(View v) {

    }


    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTitleTextView, mDurationTextView;
        ImageView mDownloadTextView;
        MyAudioPlayer mAudioPlayerView;
        View mItemView;
        SearchResult mSearchResult;
        Activity mParentActivity;
        AVLoadingIndicatorView mAvLoadingIndicatorView, mItemIndicatorView;
        RecyclerViewAdapter mParentAdapter;


        MyViewHolder(Activity parentActivity, RecyclerViewAdapter adapter, View itemView, @State int state) {
            super(itemView);
            if (state == STATE_NORMAL) {
                mParentAdapter = adapter;
                mParentActivity = parentActivity;
                mItemView = itemView;
                mTitleTextView = (TextView) itemView.findViewById(R.id.title);
                mDurationTextView = (TextView) itemView.findViewById(R.id.duration);
                mAudioPlayerView = (MyAudioPlayer) itemView.findViewById(R.id.player);
                mDownloadTextView = (ImageView) itemView.findViewById(R.id.download);
                mItemIndicatorView = (AVLoadingIndicatorView) itemView.findViewById(R.id.item_indicator);
                mItemIndicatorView.show();

            }
            if (state == STATE_LOADING) {
                mAvLoadingIndicatorView = (AVLoadingIndicatorView) itemView.findViewById(R.id.loading_indicator);
            }
        }


        void startLoading() {
            mAvLoadingIndicatorView.show();
        }

        void bindPost(final SearchResult searchResult) {
            mSearchResult = searchResult;

            final retrofit2.Call<Item> call = RestApi.get(RestService.class).getItem(searchResult.getId().getVideoId());
            call.enqueue(new Callback<Item>() {
                @Override
                public void onResponse(Response<Item> response) {
                    final Item item = response.body();
                    if (item.getStatus().equals("ok")) {
                        if (mAvLoadingIndicatorView != null && mAvLoadingIndicatorView.isShown()) {
                            mAvLoadingIndicatorView.hide();
                        }
                        mTitleTextView.setText(item.getTitle());
                        mDurationTextView.setText(item.getLength());
                        final String url = "http:" + item.getUrl();
                        Typeface iconFont = Typeface.createFromAsset(mParentActivity.getAssets(), "audio-player-view-font-custom.ttf");
                        mAudioPlayerView.setTypeface(iconFont);
                        mAudioPlayerView.withUrl(url);

                        mAudioPlayerView.setOnAudioPlayerViewListener(new AudioPlayerView.OnAudioPlayerViewListener() {
                            @Override
                            public void onAudioPreparing() {
                                //  mParentAdapter.getmCurrentAudioPlayer().toggleAudio();
                                Utils.destroyPlayer(mParentAdapter.getmCurrentAudioPlayer());
                                mParentAdapter.setmCurrentAudioPlayer(mAudioPlayerView);
                            }

                            @Override
                            public void onAudioReady() {


                            }

                            @Override
                            public void onAudioFinished() {
                                Utils.destroyPlayer(mAudioPlayerView);

                            }
                        });
                        mDownloadTextView.setVisibility(View.VISIBLE);
                        mDownloadTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Utils.downloadData(mParentActivity, url, item.getTitle());
                            }
                        });
                        //   mItemView.setVisibility(View.VISIBLE);
                        mItemIndicatorView.hide();


                    } else {
                        call.clone().enqueue(this);
                    }

                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }


        @Override
        public void onClick(View view) {

        }
    }


}
