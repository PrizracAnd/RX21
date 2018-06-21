package demjanov.av.ru.rx21;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

public class MyDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.is_progress)
                .setNegativeButton(R.string.cancel, (dialog, which) -> ((MainActivity) getActivity()).dialogCancelClicked());
        return builder.create();
    }
}
