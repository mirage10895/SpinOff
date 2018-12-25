package fr.eseo.dis.amiaudluc.spinoffapp.common;

import android.view.ContextMenu;
import android.view.View;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

public interface ItemInterface {
    // This function will simply return the position of the selected item in the RecyclerView.
    void onItemClick(int position);

    void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, int position);
}
