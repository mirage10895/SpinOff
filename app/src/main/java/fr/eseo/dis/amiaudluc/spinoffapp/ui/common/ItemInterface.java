package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import android.view.View;

/**
 * Interface to handle item actions in a list.
 */
public interface ItemInterface {
    void onItemClick(Integer id);
    void onRegisterContextMenu(View view, Integer id);
}
