package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScrollBehaviorHandler {

    private final EndlessRecyclerViewScrollListener scrollListener;
    private final RecyclerView recyclerView;

    public ScrollBehaviorHandler(@NonNull RecyclerView recyclerView, @NonNull OnScrollLoadMoreListener loadMoreListener) {
        this.recyclerView = recyclerView;
        
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (!(layoutManager instanceof GridLayoutManager)) {
            throw new IllegalArgumentException("LayoutManager must be an instance of GridLayoutManager");
        }

        this.scrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoreListener.onRecyclerLoadMore(page);
            }
        };
    }

    public void setup() {
        recyclerView.addOnScrollListener(scrollListener);
    }

    public void detach() {
        recyclerView.removeOnScrollListener(scrollListener);
        scrollListener.resetState();
    }

    public void resetState() {
        scrollListener.resetState();
    }
}
