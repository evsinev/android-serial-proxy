package com.serial_proxy.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.serial_proxy.bluetooth.BluetoothManagerFactory;
import com.serialproxy.R;

import static com.serial_proxy.bluetooth.IBluetoothManager.DeviceInfo;

public class BindingDialog extends AlertDialog {

    private final OnClickListener onClickListener;

    EditText name;
    EditText port;
    Spinner devices;

    protected BindingDialog(Context context, DialogInterface.OnClickListener aDialogListener) {
        super(context);
        onClickListener = aDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.binding_dialog, null);
        setView(view);
        setInverseBackgroundForced(true);


        Context context = getContext();

        name = (EditText) view.findViewById(R.id.binding_name);
        port = (EditText) view.findViewById(R.id.binding_port);
        devices = (Spinner) view.findViewById(R.id.binding_device);

        ArrayAdapter<DeviceInfo> adapter = new ArrayAdapter<DeviceInfo>(context, android.R.layout.simple_spinner_item, BluetoothManagerFactory.create().getDevices());
        devices.setAdapter(adapter);

        setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.binding_cancel), onClickListener);
        setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.binding_add), onClickListener);
//        setTitle(R.string.binding_add);

        super.onCreate(null);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public BindingProfile getProfile() {
        BindingProfile profile = new BindingProfile();
        profile.title = name.getText().toString();
        profile.port = Integer.parseInt(port.getText().toString());
        DeviceInfo selectedItem = (DeviceInfo) devices.getSelectedItem();
        profile.bluetoothAddress = selectedItem.address;
        profile.bluetoothName = selectedItem.name;
        return profile;
    }
}
