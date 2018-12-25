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
import fr.eseo.dis.amiaudluc.spinoffapp.https.HttpsHandler;
import fr.eseo.dis.amiaudluc.spinoffapp.parser.WebServiceParser;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

public class PopularMoviesFragment extends BaseMovieFragment {

    private Context ctx;
    private MoviesAdapter moviesAdapter;
    private View popularMoviesView;
    private SwipeRefreshLayout swipeContainer;
    private PopularMoviesFragment.GetMovies mGetMovTask;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private AppDatabase db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        popularMoviesView = inflater.inflate(R.layout.layout_main, container, false);
        ctx = popularMoviesView.getContext();

        db = AppDatabase.getAppDatabase(ctx);

        RecyclerView recycler = (RecyclerView) popularMoviesView.findViewById(R.id.cardList);
        recycler.setHasFixedSize(true);
        int columns = getResources().getInteger(R.integer.scripts_columns);
        recycler.setLayoutManager(new GridLayoutManager(ctx, columns));

        moviesAdapter = new MoviesAdapter(ctx,this,Content.movies);
        recycler.setAdapter(moviesAdapter);

        initializeSwipeContainer();

        String data = CacheManager.getInstance().read(ctx,CacheManager.CORE_POP_MOV);

        if(data.isEmpty()) {
            mGetMovTask = new GetMovies();
            mGetMovTask.setNo(1);
            mGetMovTask.execute();
        }else{
            popularMoviesView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            Content.movies.clear();
            Content.movies.addAll(WebServiceParser.multiMoviesParser(data));
            loadMovies();
        }

        this.endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager)recycler.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                GetMovies mGetMovies = new GetMovies();
                mGetMovies.setNo(page+1);
                mGetMovies.execute();
            }
        };

        recycler.addOnScrollListener(endlessRecyclerViewScrollListener);

        fKFragment.instanciateFrag(moviesAdapter);

        return popularMoviesView;
    }

    private void loadMovies() {
        moviesAdapter.setMovies(Content.movies);
        moviesAdapter.notifyDataSetChanged();
    }

    private void initializeSwipeContainer(){
        swipeContainer = (SwipeRefreshLayout) popularMoviesView.findViewById(R.id.swipeContainer);
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu_add:
                if (!db.moviesDAO().getAllIds().contains(Content.currentMovie.getId())) {
                    db.moviesDAO().insertMovie(Content.currentMovie);
                }else{
                    Snackbar.make(getView(), "Already added to library", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onItemClick(int position) {
        Content.currentMovie = Content.movies.get(position);
        Intent intent = new Intent(getContext(), MovieActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, int position) {
        Content.currentMovie = Content.movies.get(position);
        onCreateContextMenu(contextMenu,v,menuInfo);
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
            String jsonStr = sh.makeServiceCall("movie","popular",args);

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            swipeContainer.setRefreshing(false);
            popularMoviesView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            popularMoviesView.findViewById(R.id.cardList).setVisibility(View.VISIBLE);
            popularMoviesView.findViewById(R.id.no_media_display).setVisibility(View.GONE);
            if(!result.isEmpty()) {
                if(this.getNo() ==1){
                    Content.movies.clear();
                }
                CacheManager.getInstance().write(ctx,CacheManager.CORE_POP_MOV,result);
                Content.movies.addAll(WebServiceParser.multiMoviesParser(result));
            }else{
                popularMoviesView.findViewById(R.id.cardList).setVisibility(View.GONE);
                popularMoviesView.findViewById(R.id.no_media_display).setVisibility(View.VISIBLE);
                Snackbar.make(popularMoviesView, R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            loadMovies();
        }

    }
}
