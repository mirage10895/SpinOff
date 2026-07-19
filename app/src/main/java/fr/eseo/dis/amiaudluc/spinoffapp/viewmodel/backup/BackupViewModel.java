package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.backup;

import android.app.Application;
import android.net.Uri;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.repository.BackupRepository;

public class BackupViewModel extends AndroidViewModel {

    private final BackupRepository repository;
    private final MutableLiveData<BackupState> state = new MutableLiveData<>(BackupState.idle());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public BackupViewModel(@NonNull Application application) {
        super(application);
        this.repository = new BackupRepository(
                application.getContentResolver(),
                AppDatabase.getAppDatabase(application)
        );
    }

    public LiveData<BackupState> getState() {
        return state;
    }

    public void exportLibrary(Uri uri) {
        state.setValue(BackupState.loading());
        executorService.execute(() -> {
            try {
                repository.exportLibrary(uri);
                state.postValue(BackupState.success());
            } catch (IOException e) {
                state.postValue(BackupState.error(e.getMessage()));
            }
        });
    }

    public void importLibrary(Uri uri) {
        state.setValue(BackupState.loading());
        executorService.execute(() -> {
            try {
                repository.importLibrary(uri);
                state.postValue(BackupState.success());
            } catch (IOException | RuntimeException e) {
                state.postValue(BackupState.error(e.getMessage()));
            }
        });
    }

    public void resetState() {
        state.setValue(BackupState.idle());
    }

    @Override
    protected void onCleared() {
        executorService.shutdown();
    }
}
