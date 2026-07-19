package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.BottomSheetLibrarySettingsBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.backup.BackupState;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.backup.BackupViewModel;

public class LibrarySettingsBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "LibrarySettingsBottomSheet";

    private BottomSheetLibrarySettingsBinding binding;
    private BackupViewModel viewModel;

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

        binding.btnExport.setOnClickListener(v -> exportLauncher.launch("spinoff_backup.json"));
        binding.btnImport.setOnClickListener(v -> importLauncher.launch(new String[]{"application/json"}));

        viewModel.getState().observe(getViewLifecycleOwner(), this::handleState);
    }

    private void handleState(BackupState state) {
        if (state == null) return;

        switch (state.getStatus()) {
            case LOADING:
                binding.btnExport.setEnabled(false);
                binding.btnImport.setEnabled(false);
                break;
            case SUCCESS:
                Snackbar.make(binding.getRoot(), getString(R.string.import_library_success), Snackbar.LENGTH_SHORT).show();
                viewModel.resetState();
                dismiss();
                break;
            case ERROR:
                binding.btnExport.setEnabled(true);
                binding.btnImport.setEnabled(true);
                Snackbar.make(binding.getRoot(), getString(R.string.import_library_error, state.getErrorMessage()), Snackbar.LENGTH_LONG).show();
                viewModel.resetState();
                break;
            case IDLE:
                binding.btnExport.setEnabled(true);
                binding.btnImport.setEnabled(true);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
