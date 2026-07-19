package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.backup;

import lombok.Getter;

@Getter
public class BackupState {
    public enum Status { IDLE, LOADING, SUCCESS, ERROR }
    
    private final Status status;
    private final String errorMessage;

    private BackupState(Status status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public static BackupState idle() { return new BackupState(Status.IDLE, null); }
    public static BackupState loading() { return new BackupState(Status.LOADING, null); }
    public static BackupState success() { return new BackupState(Status.SUCCESS, null); }
    public static BackupState error(String message) { return new BackupState(Status.ERROR, message); }
}
