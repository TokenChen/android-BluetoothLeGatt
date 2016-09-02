package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

public class CharactersiticDetailActivity extends Activity {

    private static final String TAG = "CharacteristicDetailActivity";
    TextView mCharUUID = null;
    TextView mCharReadable = null;
    TextView mCharWriteable = null;
    TextView mCharNotification = null;
    Button mCharWrite = null;
    EditText mCharWriteData = null;
    Button mCharRead = null;
    TextView mCharReadHis = null;

    String mDeviceAddress = null;
    BluetoothLeService mBluetoothLeService = null;
    UUID targetServiceUUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    UUID targetCharacteristicUUID = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characteristic_detail);
        mCharUUID = (TextView) findViewById(R.id.character_uuid);
        mCharReadable = (TextView) findViewById(R.id.character_readable);
        mCharWriteable = (TextView) findViewById(R.id.character_writeable);
        mCharNotification = (TextView) findViewById(R.id.character_notification);
        mCharWrite = (Button) findViewById(R.id.character_write);
        mCharWriteData = (EditText) findViewById(R.id.writedata);
        mCharRead = (Button) findViewById(R.id.character_read);
        mCharReadHis = (TextView) findViewById(R.id.character_read_his);
        mDeviceAddress = getIntent().getStringExtra(BluetoothLeService.EXTRA_DEVICE_ADDRESS);
        Intent bindBLEServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(bindBLEServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }
    private void getCharacteristicInfo(){
        List<BluetoothGattService> supportServices = mBluetoothLeService.getSupportedGattServices();
        for (BluetoothGattService service : supportServices){
           if (service.getUuid().equals(targetServiceUUID)){
               List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
               for (BluetoothGattCharacteristic characteristic : characteristics){

                   if (characteristic.getUuid().equals(targetCharacteristicUUID)){
                       mCharUUID.setText(characteristic.getUuid().toString());
                       mCharReadable.setText( (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) > 0 ? "Yes" : "No");
                       mCharWriteable.setText( (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0 ? "Yes":"No");
                       mCharNotification.setText((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0 ? "Yes" : "No");
                   }
               }
           }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}
