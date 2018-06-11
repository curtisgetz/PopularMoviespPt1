package com.curtisgetz.popularmoviesppt1.data;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.curtisgetz.popularmoviesppt1.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieVideoListAdapter extends RecyclerView.Adapter<MovieVideoListAdapter.VideoViewHolder>{

    private static final String TAG = MovieVideoListAdapter.class.getSimpleName();

    final private VideoItemClickListener mOnClickListener;

    private List<MovieVideo> mVideoList;




    public MovieVideoListAdapter(VideoItemClickListener clickListener, List<MovieVideo> mVideoList) {
        this.mOnClickListener = clickListener;
        this.mVideoList = mVideoList;
    }


    public interface VideoItemClickListener{
        void onMovieItemClick(int clickedMovieIndex);
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }


    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.trailer_list_item, viewGroup, false);

        return new VideoViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {

        MovieVideo currentVideo = mVideoList.get(position);
        //Log.v(TAG, currentVideo.getmName());
        holder.mVideoNameTV.setText(currentVideo.getmName());
        holder.mVideoTypeTV.setText(currentVideo.getmType());


    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_video_name)
        TextView mVideoNameTV;

        @BindView(R.id.tv_video_type)
        TextView mVideoTypeTV;


        VideoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPos = getAdapterPosition();
            mOnClickListener.onMovieItemClick(clickedPos);
        }
    }

}
