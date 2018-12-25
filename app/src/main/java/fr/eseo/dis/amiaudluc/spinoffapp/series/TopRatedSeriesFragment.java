package fr.eseo.dis.amiaudluc.spinoffapp.series;

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

public class TopRatedSeriesFragment extends BaseFragment {
    
    private Context ctx;
    private SeriesAdapter seriesAdapter;
    private View topRatedSeriesView;
    private TopRatedSeriesFragment.GetSeries mGetSerTask;
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        topRatedSeriesView = inflater.inflate(R.layout.layout_main, container, false);
        ctx = topRatedSeriesView.getContext();

        db = AppDatabase.getAppDatabase(ctx);

        RecyclerView recycler = (RecyclerView) topRatedSeriesView.findViewById(R.id.cardList);
        recycler.setHasFixedSize(true);
        int columns = getResources().getInteger(R.integer.scripts_columns);
        recycler.setLayoutManager(new GridLayoutManager(ctx, columns));

        seriesAdapter = new SeriesAdapter(ctx,this,Content.series);
        recycler.setAdapter(seriesAdapter);

        initializeSwipeContainer();

        String data = CacheManager.getInstance().read(ctx,CacheManager.CORE_TOP_SER);

        if(data.isEmpty()) {
            mGetSerTask = new TopRatedSeriesFragment.GetSeries();
            mGetSerTask.setNo(1);
            mGetSerTask.execute();
        }else{
            topRatedSeriesView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            Content.series.clear();
            Content.series.addAll(WebServiceParser.multiSeriesParser(data));
            loadSeries();
        }

        this.endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager)recycler.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                GetSeries mGetSeries = new GetSeries();
                mGetSeries.setNo(page+1);
                mGetSeries.execute();
            }
        };
        recycler.addOnScrollListener(this.endlessRecyclerViewScrollListener);

        return topRatedSeriesView;
    }

    private void loadSeries(){
        seriesAdapter.setSeries(Content.series);
        seriesAdapter.notifyDataSetChanged();
    }

    private void initializeSwipeContainer() {
        swipeContainer = (SwipeRefreshLayout) topRatedSeriesView.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mGetSerTask = new TopRatedSeriesFragment.GetSeries();
                mGetSerTask.setNo(1);
                mGetSerTask.execute();
            }
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
                if (!db.seriesDAO().getAllIds().contains(Content.currentSerie.getId())) {
                    DatabaseTransactionManager.addSerieWithSeasons(db,Content.currentSerie);
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
        Content.currentSerie = Content.series.get(position);

        Intent intent = new Intent(getContext(), SerieActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, int position) {
        Content.currentSerie = Content.series.get(position);
        onCreateContextMenu(contextMenu,v,menuInfo);
    }

    @Override
    public void onDetach() {
        endlessRecyclerViewScrollListener.resetState();
        super.onDetach();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetSeries extends android.os.AsyncTask<String, Void, String> {

        private int no;
        private String type;
        private String id;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

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
            String jsonStr = sh.makeServiceCall("tv","top_rated",args);

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            swipeContainer.setRefreshing(false);
            topRatedSeriesView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            if(!result.isEmpty()) {
                if(this.getNo() == 1){
                    Content.series.clear();
                }
                CacheManager.getInstance().write(ctx,CacheManager.CORE_TOP_SER,result);
                Content.series.addAll(WebServiceParser.multiSeriesParser(result));
            }else{
                topRatedSeriesView.findViewById(R.id.cardList).setVisibility(View.GONE);
                topRatedSeriesView.findViewById(R.id.no_media_display).setVisibility(View.VISIBLE);
                Snackbar.make(topRatedSeriesView, R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            loadSeries();
        }

    }
}
