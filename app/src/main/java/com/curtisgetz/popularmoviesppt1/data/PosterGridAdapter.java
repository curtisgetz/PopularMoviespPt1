package com.curtisgetz.popularmoviesppt1.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import com.curtisgetz.popularmoviesppt1.R;
import com.curtisgetz.popularmoviesppt1.utils.DbBitmapUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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
    private boolean mIsSW600, mIsFavorites;


    private ArrayList<Movie> mMovieList;
    final private PosterClickListener mOnClickListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView mRecyclerView;



    public PosterGridAdapter(PosterClickListener clickListener, RecyclerView recyclerView,
                             boolean isSW600, ArrayList<Movie> movies) {
        this.mOnClickListener = clickListener;
        this.mIsSW600 = isSW600;
        this.mRecyclerView = recyclerView;
        this.mMovieList = movies;
        mIsFavorites = false;
        setScrollListener();

    }


    public PosterGridAdapter(PosterClickListener clickListener, RecyclerView recyclerView, boolean isSW600) {
        this.mOnClickListener = clickListener;
//        this.mMovieList = movieList;
        this.mIsSW600 = isSW600;
        this.mRecyclerView = recyclerView;
        mIsFavorites = false;
        setScrollListener();

    }


    private void setScrollListener(){

        //below also from https://medium.com/@programmerasi/how-to-implement-load-more-in-recyclerview-3c6358297f4
        if(mRecyclerView.getLayoutManager() instanceof LinearLayoutManager){
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if(!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if(mOnLoadMoreListener != null && !mIsFavorites){
                            mOnLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });

        }
    }


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
        //Context context = holder.itemView.getContext();
        if(holder instanceof MovieViewHolder){
            String posterUrl = mMovieList.get(position).getFullPosterUrl(mIsSW600);
          //implement offline loading later...
           // byte[] imageBytes = mMovieList.get(position).getmImageBytes();
           /* if(imageBytes != null && imageBytes.length > 0){
                Bitmap posterBitmap = DbBitmapUtil.getImage(imageBytes);

            String path = MediaStore.Images.Media
                    .insertImage(context.getContentResolver(), posterBitmap,"Title", null );
            /// /((MovieViewHolder) holder).posterIv.setImageBitmap(posterBitmap);
                Uri uri = Uri.parse(path);
            Log.v(TAG, "Loading image from DB");

            Picasso.get().load(uri)
                    .placeholder(R.drawable.posterloadingplaceholder185)
                    .error(R.drawable.posterplaceholder185)
                    .into(((MovieViewHolder) holder).posterIv);
            }else {*/

                Picasso.get().load(posterUrl)
                        .placeholder(R.drawable.posterloadingplaceholder185)
                        .error(R.drawable.posterplaceholder185)
                        .into(((MovieViewHolder) holder).posterIv);
            //}
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

         ProgressViewHolder(View view) {
            super(view);

        }
    }

    public void setLoaded() {
        loading = false;
    }


    public void setIsFavorties(){
        mIsFavorites = true;
    }

    public void setIsNotFavorites(){
        mIsFavorites = false;
    }




    public void setData(ArrayList<Movie> movieList){
        this.mMovieList =new ArrayList<>(movieList);
        notifyDataSetChanged();
    }

    public void addData(ArrayList<Movie> movieList){
        int currentSize = mMovieList.size();
        int addSize = movieList.size();
        mMovieList.addAll(movieList);
        notifyItemRangeInserted(currentSize, addSize);
    }

    public ArrayList<Movie> getmMovieList() {
        return mMovieList;
    }

    public void clearData(){
        mMovieList.clear();
        notifyItemRangeRemoved(mMovieList.size(), mMovieList.size());
    }








}
