package com.filestack.android.demo;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.filestack.Config;
import com.filestack.android.FsActivity;
import com.filestack.android.FsConstants;
import com.filestack.android.Selection;
import com.filestack.android.internal.Util;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_FILESTACK = RESULT_FIRST_USER;
    private static final int REQUEST_SETTINGS = REQUEST_FILESTACK + 1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            IntentFilter intentFilter = new IntentFilter(FsConstants.BROADCAST_UPLOAD);
            TextView logView = findViewById(R.id.log);
            UploadStatusReceiver receiver = new UploadStatusReceiver(logView);
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Locale locale = Locale.getDefault();

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FILESTACK && resultCode == RESULT_OK) {
            Log.i(TAG, "received filestack selections");
            String key = FsConstants.EXTRA_SELECTION_LIST;
            ArrayList<Selection> selections = data.getParcelableArrayListExtra(key);
            for (int i = 0; i < selections.size(); i++) {
                Selection selection = selections.get(i);
                String msg = String.format(locale, "selection %d: %s", i, selection.getName());
                Log.i(TAG, msg);
            }
        }
    }

    public void settings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, REQUEST_SETTINGS);
    }

    public void launch(View view) {
        Intent intent = new Intent(this, FsActivity.class);
        Config config = new Config(
                getString(R.string.api_key),
                getString(R.string.return_url),
                getString(R.string.policy),
                getString(R.string.signature));
        intent.putExtra(FsConstants.EXTRA_CONFIG, config);
        intent.putExtra(FsConstants.EXTRA_AUTO_UPLOAD, true);
        String[] mimeTypes = {"application/pdf", "image/*", "video/*"};
        intent.putExtra(FsConstants.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, REQUEST_FILESTACK);
    }
}
