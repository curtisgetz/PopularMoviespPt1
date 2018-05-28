package com.curtisgetz.popularmoviesppt1;


import android.media.midi.MidiOutputPort;
import android.support.annotation.NonNull;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PosterGridAdapter extends RecyclerView.Adapter{


    private static final String TAG = PosterGridAdapter.class.getSimpleName();

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private boolean mIsSW600;

    private List<Movie> mMovieList;
    final private PosterClickListener mOnClickListener;
    private OnLoadMoreListener mOnLoadMoreListener;



    PosterGridAdapter(PosterClickListener clickListener, List<Movie> movieList, RecyclerView recyclerView, boolean isSW600) {
        this.mOnClickListener = clickListener;
        this.mMovieList = movieList;
        this.mIsSW600 = isSW600;


        //below also from https://medium.com/@programmerasi/how-to-implement-load-more-in-recyclerview-3c6358297f4
        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if(!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if(mOnLoadMoreListener != null){
                            mOnLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }

                }
            });



        }
    }


/*
    public void setData(List<Movie> movieList){
        mMovieList = movieList;
        notifyDataSetChanged();
    }
*/


    public interface PosterClickListener {
        void onPosterClick(int clickedPosterIndex);
    }

    @Override
    public int getItemCount() {
       return mMovieList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.poster_list_item, viewGroup, false);

        return new MovieViewHolder(view);

    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof MovieViewHolder){
            String posterUrl = mMovieList.get(position).getFullPosterUrl(mIsSW600);

            Picasso.get().load(posterUrl)
                    .placeholder(R.drawable.posterloadingplaceholder185)
                    .error(R.drawable.posterplaceholder185)
                    .into(((MovieViewHolder) holder).posterIv);

        }
        //else {
           // ((ProgressViewHolder) holder).mProgressBar.setIndeterminate(true);
        //}
    }


    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.poster_iv) ImageView posterIv;

        MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            //call onPosterClick() passing the clicked poster position.
            mOnClickListener.onPosterClick(getAdapterPosition());
        }
    }




    /* used
    *https://medium.com/@programmerasi/how-to-implement-load-more-in-recyclerview-3c6358297f4
    * for how to get 'endless' scrolling
    */

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    @Override
    public int getItemViewType(int position) {
        return mMovieList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder{
        // @BindView(R.id.scroll_progress_bar) ProgressBar mProgressBar;

         ProgressViewHolder(View view) {
            super(view);
          //  ButterKnife.bind(this, view);
            //mProgressBar = (ProgressBar) view.findViewById(R.id.scroll_progress_bar);
        }
    }

    public void setLoaded() {
        loading = false;
    }
}
