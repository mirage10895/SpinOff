package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import android.view.View;

/**
 * Interface to handle item actions in a list.
 */
public interface ItemInterface {
    void onItemClick(Integer id, FragmentType type);
    default void onRegisterContextMenu(View view, Integer id) {}
    default void onStatusClick(Integer id, FragmentType type) {}
}
