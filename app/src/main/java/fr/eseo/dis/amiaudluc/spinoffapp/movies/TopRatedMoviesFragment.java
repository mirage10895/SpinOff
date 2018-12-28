package fr.eseo.dis.amiaudluc.spinoffapp.movies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import fr.eseo.dis.amiaudluc.spinoffapp.BaseFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.BaseMovieFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.CacheManager;
import fr.eseo.dis.amiaudluc.spinoffapp.common.EndlessRecyclerViewScrollListener;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.https.HttpsHandler;
import fr.eseo.dis.amiaudluc.spinoffapp.parser.WebServiceParser;

/**
 * Created by lucasamiaud on 03/03/2018.
 */

public class TopRatedMoviesFragment extends BaseMovieFragment {
    private Context ctx;
    private MoviesAdapter moviesAdapter;
    private View topRatedMoviesView;
    private TopRatedMoviesFragment.GetMovies mGetMovTask;
    private AppDatabase db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        topRatedMoviesView = inflater.inflate(R.layout.layout_main, container, false);
        ctx = topRatedMoviesView.getContext();

        db = AppDatabase.getAppDatabase(ctx);

        final RecyclerView recycler = (RecyclerView) topRatedMoviesView.findViewById(R.id.cardList);
        recycler.setHasFixedSize(true);
        int columns = getResources().getInteger(R.integer.scripts_columns);
        recycler.setLayoutManager(new GridLayoutManager(ctx, columns));

        moviesAdapter = new MoviesAdapter(ctx,this,Content.movies);
        recycler.setAdapter(moviesAdapter);

        initializeSwipeContainer();

        this.endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager)recycler.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                GetMovies mGetMovies = new GetMovies();
                mGetMovies.setNo(page+1);
                mGetMovies.execute();
            }
        };
        recycler.addOnScrollListener(endlessRecyclerViewScrollListener);

        loadData();

        fKFragment.instanciateFrag(moviesAdapter);

        return topRatedMoviesView;
    }

    private void loadData(){
        String data = CacheManager.getInstance().read(ctx,CacheManager.CORE_TOP_MOV);

        if(data.isEmpty()) {
            mGetMovTask = new TopRatedMoviesFragment.GetMovies();
            mGetMovTask.setNo(1);
            mGetMovTask.execute();
        }else{
            topRatedMoviesView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            Content.movies.clear();
            Content.movies.addAll(WebServiceParser.multiMoviesParser(data));
            loadMovies();
        }
    }

    private void loadMovies() {
        moviesAdapter.setMovies(Content.movies);
        moviesAdapter.notifyDataSetChanged();
    }

    private void initializeSwipeContainer(){
        swipeContainer = (SwipeRefreshLayout) topRatedMoviesView.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            mGetMovTask = new GetMovies();
            mGetMovTask.setNo(1);
            mGetMovTask.execute();
        });

        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.white);
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetMovies extends android.os.AsyncTask<String, Void, String> {

        private int no;

        private int getNo(){
            return this.no;
        }

        private void setNo(int no){
            this.no = no;
        }
        @Override
        protected String doInBackground(String... urls) {
            HttpsHandler sh = new HttpsHandler();
            String args = "&language=en-US&page="+this.getNo()+"&region=FR";

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall("movie","top_rated",args);

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            swipeContainer.setRefreshing(false);
            topRatedMoviesView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            topRatedMoviesView.findViewById(R.id.cardList).setVisibility(View.VISIBLE);
            topRatedMoviesView.findViewById(R.id.no_media_display).setVisibility(View.GONE);
            if(!result.isEmpty()) {
                if(this.getNo() == 1){
                    Content.movies.clear();
                }
                CacheManager.getInstance().write(ctx,CacheManager.CORE_TOP_MOV,result);
                Content.movies.addAll(WebServiceParser.multiMoviesParser(result));
            }else{
                topRatedMoviesView.findViewById(R.id.cardList).setVisibility(View.GONE);
                topRatedMoviesView.findViewById(R.id.no_media_display).setVisibility(View.VISIBLE);
                Snackbar.make(topRatedMoviesView, R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            loadMovies();
        }

    }

}
