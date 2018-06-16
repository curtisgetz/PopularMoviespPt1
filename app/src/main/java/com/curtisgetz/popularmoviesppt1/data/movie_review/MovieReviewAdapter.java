package com.curtisgetz.popularmoviesppt1.data.movie_review;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.curtisgetz.popularmoviesppt1.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ReviewViewHolder>{

    private static final String TAG = MovieReviewAdapter.class.getSimpleName();

    private List<MovieReview> mReviewList;


    public MovieReviewAdapter(List<MovieReview> mReviewList) {
        this.mReviewList = mReviewList;
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }


    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.review_list_item, viewGroup, false);

        return new ReviewViewHolder(view);




    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {

        holder.mReviewAuthor.setText(mReviewList.get(position).getmReviewAuthor());
        holder.mReviewContent.setText(mReviewList.get(position).getmReviewContent());


    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_review_author) TextView mReviewAuthor;
        @BindView(R.id.tv_review_content) TextView mReviewContent;

        ReviewViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);

        }



    }



}
