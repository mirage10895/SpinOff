package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library.WatchedMediaItem;

/**
 * Callback used by the watched list adapter: a tap on a tile opens the media, and the overflow
 * button of a tile opens its action menu anchored on that button.
 */
public interface WatchedTileListener {

    void onTileClick(WatchedMediaItem item);

    void onTileMenu(View anchor, WatchedMediaItem item);
}
