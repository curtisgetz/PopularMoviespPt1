package com.curtisgetz.popularmoviesppt1.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.curtisgetz.popularmoviesppt1.R;
import com.curtisgetz.popularmoviesppt1.data.movie_review.MovieReview;
import com.curtisgetz.popularmoviesppt1.data.movie_review.MovieReviewAdapter;
import com.curtisgetz.popularmoviesppt1.utils.FetchReviewsLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieReviewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MovieReview>>{


    private static final String testReviewJson = "{\n" +
            "  \"id\": 372058,\n" +
            "  \"page\": 1,\n" +
            "  \"results\": [\n" +
            "    {\n" +
            "      \"author\": \"Gimly\",\n" +
            "      \"content\": \"I would never take it away from anyone, but I was underwhelmed.\\r\\n\\r\\n_Final rating:★★ - Definitely not for me, but I sort of get the appeal._\",\n" +
            "      \"id\": \"5898288d9251417a85005c47\",\n" +
            "      \"url\": \"https://www.themoviedb.org/review/5898288d9251417a85005c47\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"author\": \"BecauseImBatman\",\n" +
            "      \"content\": \"It has beautiful animation and beautiful characters. It is a funny, sweet and emotional roller coaster of a crowd-pleaser that manages to win your heart.\",\n" +
            "      \"id\": \"58afc9c1c3a3682cd0007c1a\",\n" +
            "      \"url\": \"https://www.themoviedb.org/review/58afc9c1c3a3682cd0007c1a\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"author\": \"Reno\",\n" +
            "      \"content\": \"**They're no strangers, yet they've never met.**\\r\\n\\r\\nProbably the most anticipated anime since the final film of Mr. Miyazaki. Because people know what these guys are capable of. This is one per cent sci-fi and 99 per cent fantasy. The concept is not new for us. From 'Freaky Friday' to 'The Lake House', even the recent American television film 'The Swap', you will remember a handful of names once you read this film's synopsis. Yet the film was so good, because of how well the theme was utilised.\\r\\n\\r\\nWe all know the technical brilliance of '5 Centimeters Per Second' and the beautiful romance from 'The Garden of Words'. This is an excellent mix of those two. If love either of them, or both, then you will love it as well. But if you ask me, my favourite is still 'The Garden of Words' from this director. I liked this film, enjoyed thoroughly, so liking it less than that means not a bad flick. Surely a film to recommend for anime fans, as well as for animation's (western audience).\\r\\n\\r\\nThe story of two teenagers, one from the rural Japan mountain region and the other one from Tokyo. The plot revolves around a comet that is to pass by very close to the earth. But before that event, the girl is bored with her life, so she dreams of living in a big city and that too as a boy. One day her wish comes true when she wakes up as she swapped bodies, including most of the memories with a boy from Tokyo.\\r\\n\\r\\nFor her, it is to live as she wanted and make use of the opportunity. But for him, it is like a curse, hence becomes rebellious. This thing keeps happening frequently for days and weeks. After some times, realising the phenomena is between two, they make a deal and strict order of Dos and Don'ts while away. All this until the day of comet and everything changes forever. Searching for the truth and the result of it leads to the end of the tale.\\r\\n\\r\\n> ❝Treasure the experience. Dreams fade away after you wake up.❞\\r\\n\\r\\nUnlike Miyazaki, Makot Shinkai's storytelling is quite for the matured audience. It targets adults more than the children. Particularly for not shying away to include naturally occurring events in such situation as the story develops, despite/even though the film revolves around the teens/kids. As for this flick, the concern is not big, but it depends on the nation and its culture you belong to. It is very much acceptable and as the story progress, the best things keep replacing another till the finale. So hooked to it is assured.\\r\\n\\r\\nThe film length was good and so the pace of the narration. Well written screenplay, which was based on the book of the same name. But the director admits the inspiration from the previous flicks with similar themes. They have used the real places of the anime version with fictional names, but most of the Tokyo remains same and they all were awesome.\\r\\n\\r\\nIn many scenes, the camera panning was a treat. Especially the framerate was high, so no jerks, hence very pleasurable visual experience. Like I mentioned earlier, this is a unique film, despite not a original idea. The film proved the Japan is not overshadowed by Hollywood, particularly after the decline of animes in the recent years. It's up to the quality of the product, if so, then those glory days can be brought back for animes. Anyway, now this film holds the record for highest grossed anime.\\r\\n\\r\\nApart from technical dominance, the story is also enchanting. If you like romance theme, this will serve you well. A good film for date night. But usually people would skip animation for such occasion and one should not mix up that with anime. Because why I always put apart the anime and animation is, the animes are good at beautiful romance, while its counterpart from the west focus more on comedy and adventure. But the common thing from them is the well explored fantasy theme.\\r\\n\\r\\nUsually the ends are guessable for a film like this and the same case for this in here. Only as a result of the story, you would know what's coming, but not the frame by frame scenes of it. Yeah, I liked this conclusion, the final lines said might bring you tears in your eyes, romantically, if you are an emotional person. One of the best endings, even though it was clichéd. And the credits start to roll up, while you begin to recall everything you just saw in the last 100 minutes. A must see film, so highly recommended by my side. Now I've to wait for another couple of years for such film.\\r\\n\\r\\n_8/10_\",\n" +
            "      \"id\": \"5901278dc3a36810c600301c\",\n" +
            "      \"url\": \"https://www.themoviedb.org/review/5901278dc3a36810c600301c\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"total_pages\": 1,\n" +
            "  \"total_results\": 3\n" +
            "}";


    private final static int REVIEWS_LOADER_ID = 4;
    private MovieReviewAdapter mAdapter;
    private List<MovieReview> mReviewList;
    private int mMovieID;

    @BindView(R.id.review_recyclerview) RecyclerView mReviewRecyclerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_reviews);

        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mReviewRecyclerView.setLayoutManager(layoutManager);

       /* List<MovieReview> testList = JsonUtils.getMovieReviewList(testReviewJson);
        mAdapter = new MovieReviewAdapter(testList);
        mReviewRecyclerView.setAdapter(mAdapter);
        */


//TODO finish this stuff
        Intent intent = getIntent();
        if(intent != null){
            int movieID = intent.getIntExtra(getString(R.string.movie_id_to_pass), -1);
            if(movieID >= 0){
                mMovieID = movieID;

                LoaderManager loaderManager = getSupportLoaderManager();
                Loader<List<MovieReview>> reviewsLoader = loaderManager.getLoader(REVIEWS_LOADER_ID);
                if(reviewsLoader == null){
                    loaderManager.initLoader(REVIEWS_LOADER_ID, null, this);
                }else{
                    loaderManager.restartLoader(REVIEWS_LOADER_ID, null , this);
                }
            }
        }



    }


    @NonNull
    @Override
    public Loader<List<MovieReview>> onCreateLoader(int id, @Nullable Bundle args) {
        return new FetchReviewsLoader(this, mMovieID);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<MovieReview>> loader, List<MovieReview> data) {
        //mReviewList = data;
        mAdapter = new MovieReviewAdapter(data);
        mReviewRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<MovieReview>> loader) {

    }
}
