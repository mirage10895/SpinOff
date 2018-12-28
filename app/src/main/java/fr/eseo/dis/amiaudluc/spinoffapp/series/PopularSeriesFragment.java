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
import android.view.View;
import android.view.ViewGroup;

import fr.eseo.dis.amiaudluc.spinoffapp.BaseFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.BaseSerieFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.CacheManager;
import fr.eseo.dis.amiaudluc.spinoffapp.common.EndlessRecyclerViewScrollListener;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.https.HttpsHandler;
import fr.eseo.dis.amiaudluc.spinoffapp.parser.WebServiceParser;

/**
 * Created by lucasamiaud on 01/03/2018.
 */

public class PopularSeriesFragment extends BaseSerieFragment{

    private Context ctx;
    private SeriesAdapter seriesAdapter;
    private View popularSeriesView;
    private PopularSeriesFragment.GetSeries mGetSerTask;
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        popularSeriesView = inflater.inflate(R.layout.layout_main, container, false);
        ctx = popularSeriesView.getContext();

        db = AppDatabase.getAppDatabase(ctx);

        RecyclerView recycler = (RecyclerView) popularSeriesView.findViewById(R.id.cardList);
        recycler.setHasFixedSize(true);
        int columns = getResources().getInteger(R.integer.scripts_columns);
        recycler.setLayoutManager(new GridLayoutManager(ctx, columns));

        seriesAdapter = new SeriesAdapter(ctx,this,Content.series);
        recycler.setAdapter(seriesAdapter);

        initializeSwipeContainer();

        String data = CacheManager.getInstance().read(ctx,CacheManager.CORE_POP_SER);
        
        if(data.isEmpty()) {
            mGetSerTask = new PopularSeriesFragment.GetSeries();
            mGetSerTask.setNo(1);
            mGetSerTask.execute();
        }else{
            popularSeriesView.findViewById(R.id.progressBar).setVisibility(View.GONE);
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

        return popularSeriesView;
    }

    private void loadSeries(){
        seriesAdapter.setSeries(Content.series);
        seriesAdapter.notifyDataSetChanged();
    }

    private void initializeSwipeContainer(){
        swipeContainer = (SwipeRefreshLayout) popularSeriesView.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mGetSerTask = new PopularSeriesFragment.GetSeries();
                mGetSerTask.setNo(1);
                mGetSerTask.execute();
            }
        });

        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.white);
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetSeries extends android.os.AsyncTask<String, Void, String> {

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

            return sh.makeServiceCall("tv","popular",args);
        }

        @Override
        protected void onPostExecute(String result) {
            swipeContainer.setRefreshing(false);
            popularSeriesView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            popularSeriesView.findViewById(R.id.cardList).setVisibility(View.VISIBLE);
            popularSeriesView.findViewById(R.id.no_media_display).setVisibility(View.GONE);
            if(!result.isEmpty()) {
                if(this.no == 1){
                    Content.series.clear();
                }
                CacheManager.getInstance().write(ctx,CacheManager.CORE_POP_SER,result);
                Content.series.addAll(WebServiceParser.multiSeriesParser(result));
            }else{
                popularSeriesView.findViewById(R.id.cardList).setVisibility(View.GONE);
                popularSeriesView.findViewById(R.id.no_media_display).setVisibility(View.VISIBLE);
                Snackbar.make(popularSeriesView, R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Refresh", null).show();
            }
            loadSeries();
        }

    }
}
