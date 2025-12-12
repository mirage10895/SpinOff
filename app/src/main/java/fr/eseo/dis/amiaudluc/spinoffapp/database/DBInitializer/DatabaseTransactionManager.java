package fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer;

/**
 * Created by lucasamiaud on 30/12/2017.
 */

public class DatabaseTransactionManager {

    private static final String TAG = DatabaseTransactionManager.class.getName();

    public static void executeAsync(Runnable target) {
        new Thread(target).start();
    }
}
