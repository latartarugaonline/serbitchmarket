package it.ltm.scp.module.android.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import it.ltm.scp.module.android.R;
import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.api.printer.PrinterAPI;
import it.ltm.scp.module.android.devices.pos.PosUtils;
import it.ltm.scp.module.android.devices.terminal.TerminalManagerFactory;
import it.ltm.scp.module.android.model.devices.printer.gson.PrinterInfo;
import it.ltm.scp.module.android.model.devices.printer.gson.status.Status;
import it.ltm.scp.module.android.utils.Constants;

/**
 * Created by HW64 on 13/10/2016.
 */
public class MainDialogFragment extends DialogFragment {

    public interface MainDialogListener {
        void onRetryAuth();

        void onRetryPosInfo();

        void onCloseApp(Dialog dialog);

        void onRetryBarcode();

        void onPrinterReady();

        void onCredentialAcquired(String username, String password);
    }

    private void setupView(View view) {
        authLayout = view.findViewById(R.id.layout_dialog_auth);
        authMessage = view.findViewById(R.id.layout_dialog_auth_message);
        authButton = view.findViewById(R.id.layout_dialog_auth_button);
        printerLayout = view.findViewById(R.id.layout_dialog_printer);
        printerErrorMessageLayout = view.findViewById(R.id.layout_dialog_printer_errormessage);
        printerErrorMessage = view.findViewById(R.id.message_dialog_printer);
        printerErrorButton = view.findViewById(R.id.layout_dialog_printer_button);
        eventListView = view.findViewById(R.id.list_dialog_events);
        barcodeLayout = view.findViewById(R.id.layout_dialog_barcode);
        barcodeButtonLayout = view.findViewById(R.id.layout_dialog_barcode_button);
        barcodeMessage = view.findViewById(R.id.layout_dialog_barcode_message);
        updateLayout = view.findViewById(R.id.layout_dialog_update);
        updateMessage = view.findViewById(R.id.layout_dialog_update_message);
        globalLayout = view.findViewById(R.id.global_layout);
        rootLayout = view.findViewById(R.id.dialog_root);
        loginLayout = view.findViewById(R.id.layout_login);
        passwordEditText = view.findViewById(R.id.edit_text_pwd);
        userEditText = view.findViewById(R.id.edit_text_usr);
        userLayoutText = view.findViewById(R.id.layout_text_usr);
        pwdLayoutText = view.findViewById(R.id.layout_text_pwd);
        accediButton = view.findViewById(R.id.view_login_button_accedi);
        loginMessage = view.findViewById(R.id.text_login_error);
        abortBarcode = view.findViewById(R.id.layout_dialog_barcode_button_annulla);
        retryBarcode = view.findViewById(R.id.layout_dialog_barcode_button_riprova);
    }

    View authLayout;
    TextView authMessage;
    View authButton;
    View printerLayout;
    View printerErrorMessageLayout;
    TextView printerErrorMessage;
    View printerErrorButton;
    ListView eventListView;
    View barcodeLayout;
    View barcodeButtonLayout;
    TextView barcodeMessage;
    View updateLayout;
    TextView updateMessage;
    View globalLayout;
    View rootLayout;
    View loginLayout;
    ActionMenuDisabledEditText passwordEditText;
    ActionMenuDisabledEditText userEditText;
    TextInputLayout userLayoutText;
    TextInputLayout pwdLayoutText;
    View accediButton;
    TextView loginMessage;
    TextView abortBarcode;
    TextView retryBarcode;

    private HashMap<Integer, String> code2message;
    private DialogEventAdapter adapter;
    private boolean isAuthContext = false;

    private final String TAG = MainDialogFragment.class.getSimpleName();

    private MainDialogListener listener;

    public MainDialogFragment() {
    }

    public static MainDialogFragment newInstance() {
        MainDialogFragment f = new MainDialogFragment();
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        code2message = new HashMap<>();
        adapter = new DialogEventAdapter(getContext(), R.layout.dialog_list_row, new ArrayList<String>());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TerminalManagerFactory.get().getDeviceName().equals(Constants.DEVICE_P2_PRO)) {
            getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        } else {
            int width = getResources().getDimensionPixelSize(R.dimen.dialog_main_width);
            int height = getResources().getDimensionPixelSize(R.dimen.dialog_main_height);
            getDialog().getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout;
        if (TerminalManagerFactory.get().getDeviceName().equals(Constants.DEVICE_P2_PRO)) {
            layout = inflater.inflate(R.layout.dialog_main_p2pro, container);
        } else {
            layout = inflater.inflate(R.layout.dialog_main, container);
        }

        setupView(layout);

        setCancelable(false);
        eventListView.setAdapter(adapter);

        // chiudere la tastiera quando campi di input non in focus:
        View.OnFocusChangeListener closeKeyboardOnFocusLost = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    closeKeyboard(view);
                }
            }
        };
        rootLayout.setOnFocusChangeListener(closeKeyboardOnFocusLost);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    closeKeyboard(textView);
                    accedi();
                }
                return false;
            }
        });

        userEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    userEditText.setText(userEditText.getText().toString().trim());
                }
            }
        });

        accediButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accedi();
            }
        });

        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retryAuth();
            }
        });

        abortBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abortBarcode();
            }
        });

        retryBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retryBarcode();
            }
        });

        printerErrorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retryPrinterInfo();
            }
        });

        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (MainDialogListener) getActivity();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        listener = null;
    }

    public void accedi() {
        String user = userEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        if (verifyUsername(user)) {
            if (verifyPassword(password))
                listener.onCredentialAcquired(user, password);
        }
    }

    public void retryAuth() {
        if (isAuthContext)
            listener.onRetryAuth();
        else
            listener.onRetryPosInfo();
    }

    public void retryBarcode() {
        listener.onRetryBarcode();
    }

    public void abortBarcode() {
        barcodeLayout.setVisibility(View.GONE);
        dismissDialog();
    }

    public void closeApp() {
        listener.onCloseApp(getDialog());
    }

    public void retryPrinterInfo() {
        printerErrorButton.setVisibility(View.GONE);
        printerErrorMessage.setText("Nuovo tentativo in corso.");
        new PrinterAPI().getPrinterStatusV2(new APICallbackV2<PrinterInfo>() {
            @Override
            public void onResult(PrinterInfo result) {
                printerErrorMessageLayout.setVisibility(View.GONE);
                processPrinterStatus(result.getStatus());
            }

            @Override
            public void onError(int code, String message, Exception e) {
                String error = PosUtils.appendCodeToMessage(message, code);
                processPrinterError(error);
            }
        });
    }


    public void processPrinterStatus(Status status) {
        TerminalManagerFactory.get().parsePrinterStatus(code2message, status);
        adapter.updateItems(code2message.values());

        if (code2message.isEmpty()) {
            tryClosePrinterPanel();
        } else {
            printerLayout.setVisibility(View.VISIBLE);
        }

    }

    public void processPrinterError(String message) {
        printerLayout.setVisibility(View.VISIBLE);
        printerErrorMessageLayout.setVisibility(View.VISIBLE);
        printerErrorButton.setVisibility(View.VISIBLE);
        printerErrorMessage.setText(message);
    }

    private void tryClosePrinterPanel() {
        if (code2message.isEmpty() && !printerErrorMessageLayout.isShown()) {
            printerLayout.setVisibility(View.GONE);
            // printer is fixed, cut paper
            listener.onPrinterReady();
            dismissDialog();
        }
    }

    private void dismissDialog() {
        if (!authLayout.isShown() && !printerLayout.isShown()
                && !barcodeLayout.isShown() && !updateLayout.isShown() && !loginLayout.isShown()) {
            /*
            è possibile dismettere il dialog anche mentre l'applicazione è in pausa.
            Durante la ripresa è possibile che lo stato del dialog sia andato perso
             */
            dismissAllowingStateLoss();
        }
    }

    public void processAuthStatus(String message, boolean showReload, boolean finish) {
        isAuthContext = true;
        if (finish) {
            authLayout.setVisibility(View.GONE);
            if (barcodeLayout.isShown()) {
                listener.onRetryBarcode();
            }
            dismissDialog();
        } else {
            authLayout.setVisibility(View.VISIBLE);
            authMessage.setText(message);
            authButton.setVisibility(showReload ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void processICTStatus(String message, boolean showReload, boolean finish) {
        isAuthContext = false;
        if (finish) {
            authLayout.setVisibility(View.GONE);
            if (barcodeLayout.isShown()) {
                listener.onRetryBarcode();
            }
            dismissDialog();
        } else {
            authLayout.setVisibility(View.VISIBLE);
            authMessage.setText(message);
            authButton.setVisibility(showReload ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void processBarcodeStatus(String message, boolean showReload, boolean finish) {
        if (finish) {
            barcodeLayout.setVisibility(View.GONE);
            dismissDialog();
        } else {
            barcodeLayout.setVisibility(View.VISIBLE);
            barcodeMessage.setText(message);
            barcodeButtonLayout.setVisibility(showReload ? View.VISIBLE : View.GONE);
        }
    }

    public void processUpdateStatus(String message, boolean finish) {
        if (finish) {
            updateLayout.setVisibility(View.GONE);
            dismissDialog();
        } else {
            updateLayout.setVisibility(View.VISIBLE);
            updateMessage.setText(message);
        }
    }

    public void requestLoginCredential(String errorMessage, boolean finish) {
        Log.d(TAG, "requestLoginCredential() called with: errorMessage = [" + errorMessage + "], finish = [" + finish + "]");
        if (finish) {
            passwordEditText.setText(""); //reset campo pwd
            loginLayout.setVisibility(View.GONE);
            globalLayout.setVisibility(View.VISIBLE);
            // TODO: 22/09/2020 dismiss?
            dismissDialog();
        } else {
            loginLayout.setVisibility(View.VISIBLE);
            globalLayout.setVisibility(View.GONE);
            userEditText.requestFocus();
            if (null != errorMessage
                    && !TextUtils.isEmpty(errorMessage)) {
//                loginMessage.setVisibility(View.VISIBLE);
                loginMessage.setText(errorMessage);
            } else {
//                loginMessage.setVisibility(View.INVISIBLE);
                loginMessage.setText(R.string.login_message);
            }
        }
    }

    public boolean isAuthShown() {
        return authLayout.isShown() || loginLayout.isShown();
    }

    public boolean isUpdateShown() {
        return updateLayout.isShown();
    }

    public boolean isPrinterShown() {
        return printerLayout.isShown();
    }

    private void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private boolean verifyUsername(String username) {
        if (TextUtils.isEmpty(username)) {
            userLayoutText.setError("Campo obbligatorio");
            return false;
        } else {
            userLayoutText.setError(null);
            return true;
        }
    }

    private boolean verifyPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            pwdLayoutText.setError("Campo obbligatorio");
            return false;
        } else {
            pwdLayoutText.setError(null);
            return true;
        }
    }
}
