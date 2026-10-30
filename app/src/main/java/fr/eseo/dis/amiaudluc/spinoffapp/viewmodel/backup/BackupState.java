package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.backup;

import androidx.annotation.Nullable;

import lombok.Getter;

/**
 * State of the backup screen: what is running (or what just happened) and, for a failure, the
 * cause. Carrying the {@link Operation} lets the screen show the message of the operation that
 * actually ran, instead of an ambiguous "success" shared by the export and the import.
 */
@Getter
public class BackupState {
    public enum Status { IDLE, LOADING, SUCCESS, ERROR }

    public enum Operation { EXPORT, IMPORT }

    private final Status status;
    private final Operation operation;
    private final String errorMessage;

    private BackupState(Status status, @Nullable Operation operation, @Nullable String errorMessage) {
        this.status = status;
        this.operation = operation;
        this.errorMessage = errorMessage;
    }

    public static BackupState idle() {
        return new BackupState(Status.IDLE, null, null);
    }

    public static BackupState loading(Operation operation) {
        return new BackupState(Status.LOADING, operation, null);
    }

    public static BackupState success(Operation operation) {
        return new BackupState(Status.SUCCESS, operation, null);
    }

    public static BackupState error(Operation operation, String message) {
        return new BackupState(Status.ERROR, operation, message);
    }
}
