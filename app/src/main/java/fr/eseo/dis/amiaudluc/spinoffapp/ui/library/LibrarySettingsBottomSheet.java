package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.BottomSheetLibrarySettingsBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.NotificationPreferences;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.backup.BackupState;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.backup.BackupViewModel;

/**
 * Modal settings of the library: backup export/import and the release notification preference.
 * Asks for the notification permission only when the user opts in, so a refusal stays reversible.
 */
public class LibrarySettingsBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "LibrarySettingsBottomSheet";

    private BottomSheetLibrarySettingsBinding binding;
    private BackupViewModel viewModel;
    private NotificationPreferences notificationPreferences;

    private final ActivityResultLauncher<String> notificationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                binding.switchReleaseNotifications.setChecked(granted);
                notificationPreferences.setReleasesEnabled(granted);
                if (!granted) {
                    showNotificationSettingsHint();
                }
            }
    );

    private final ActivityResultLauncher<String> exportLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("application/json"),
            (Uri uri) -> {
                if (uri != null) {
                    viewModel.exportLibrary(uri);
                }
            }
    );

    private final ActivityResultLauncher<String[]> importLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            (Uri uri) -> {
                if (uri != null) {
                    viewModel.importLibrary(uri);
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BentoBottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetLibrarySettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BackupViewModel.class);
        notificationPreferences = new NotificationPreferences(requireContext());

        setupNotificationSwitch();

        binding.rowReleaseNotifications.setOnClickListener(v ->
                binding.switchReleaseNotifications.toggle());
        binding.rowExport.setOnClickListener(v -> exportLauncher.launch("spinoff_backup.json"));
        binding.rowImport.setOnClickListener(v ->
                importLauncher.launch(new String[]{"application/json"}));

        viewModel.getState().observe(getViewLifecycleOwner(), this::handleState);
    }

    private void setupNotificationSwitch() {
        binding.switchReleaseNotifications.setChecked(
                notificationPreferences.areReleasesEnabled() && hasNotificationPermission());
        binding.switchReleaseNotifications.setOnCheckedChangeListener(
                (button, isChecked) -> onReleaseNotificationsToggled(isChecked));
    }

    /**
     * Persist the preference and, when opting in, request the runtime permission if needed. The
     * permission callback realigns the switch when the request is denied.
     */
    private void onReleaseNotificationsToggled(boolean isChecked) {
        if (!isChecked) {
            notificationPreferences.setReleasesEnabled(false);
            return;
        }
        if (hasNotificationPermission()) {
            notificationPreferences.setReleasesEnabled(true);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private boolean hasNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * The permission is denied: explain where to re-enable it, the toggle alone cannot.
     */
    private void showNotificationSettingsHint() {
        Snackbar.make(binding.getRoot(), R.string.setting_release_notifications_permission_denied,
                        Snackbar.LENGTH_LONG)
                .setAction(R.string.setting_release_notifications_open_settings,
                        v -> openApplicationSettings())
                .show();
    }

    private void openApplicationSettings() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName());
        startActivity(intent);
    }

    private void handleState(BackupState state) {
        if (state == null) return;

        switch (state.getStatus()) {
            case LOADING:
                setActionsEnabled(false);
                break;
            case SUCCESS:
                Snackbar.make(binding.getRoot(), successMessage(state.getOperation()),
                        Snackbar.LENGTH_SHORT).show();
                viewModel.resetState();
                dismiss();
                break;
            case ERROR:
                setActionsEnabled(true);
                Snackbar.make(binding.getRoot(), errorMessage(state), Snackbar.LENGTH_LONG).show();
                viewModel.resetState();
                break;
            case IDLE:
                setActionsEnabled(true);
                break;
        }
    }

    private int successMessage(BackupState.Operation operation) {
        return operation == BackupState.Operation.IMPORT
                ? R.string.import_library_success
                : R.string.export_library_success;
    }

    private String errorMessage(BackupState state) {
        int message = state.getOperation() == BackupState.Operation.IMPORT
                ? R.string.import_library_error
                : R.string.export_library_error;
        return getString(message, state.getErrorMessage());
    }

    private void setActionsEnabled(boolean enabled) {
        binding.rowExport.setEnabled(enabled);
        binding.rowImport.setEnabled(enabled);
        binding.rowExport.setAlpha(enabled ? 1f : 0.5f);
        binding.rowImport.setAlpha(enabled ? 1f : 0.5f);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
