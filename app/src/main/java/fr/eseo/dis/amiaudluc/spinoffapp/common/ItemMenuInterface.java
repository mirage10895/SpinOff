package fr.eseo.dis.amiaudluc.spinoffapp.common;

import android.view.ContextMenu;
import android.view.View;

public interface ItemMenuInterface extends ItemInterface {
    void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer id);
}
