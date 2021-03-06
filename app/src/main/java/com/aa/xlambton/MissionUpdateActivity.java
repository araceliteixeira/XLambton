package com.aa.xlambton;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.aa.xlambton.Model.Agent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MissionUpdateActivity extends AppCompatActivity {
    String dirAppPhoto;
    List<String> paths;
    GridView photoGrid;
    private static final int CAMERA_CODE = 990;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_update);

        final Agent agent = (Agent) getIntent().getSerializableExtra("agent");

        Button btnCamera = (Button) findViewById(R.id.mission_update_button_camera);
        Button btnSms = (Button) findViewById(R.id.mission_update_button_sms);

        paths = new ArrayList<>();
        MissionUpdateAdapter adapter = new MissionUpdateAdapter(this, R.layout.activity_mission_update, paths);
        photoGrid = findViewById(R.id.mission_update_grid);
        photoGrid.setAdapter(adapter);

        photoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MissionUpdateAdapter) photoGrid.getAdapter()).notifyDataSetChanged();
            }
        });
        registerForContextMenu(photoGrid);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dirAppPhoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
                File filePhoto = new File(dirAppPhoto);

                if (filePhoto != null) {
                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intentCamera.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri uri = FileProvider.getUriForFile(view.getContext(), BuildConfig.APPLICATION_ID, filePhoto);
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    if (intentCamera.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intentCamera, CAMERA_CODE);
                    }
                }
            }
        });

        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = agent.getPhoneNumber();
                if (paths.isEmpty()) {
                    Toast.makeText(MissionUpdateActivity.this, "There are no photos to be sent.",
                            Toast.LENGTH_SHORT).show();
                } else if (phone == null || phone.equals("")) {
                    Toast.makeText(MissionUpdateActivity.this, "Cannot send photos. This agent has no phone number.",
                            Toast.LENGTH_LONG).show();
                } else {
                    ArrayList<Uri> uris = new ArrayList<>();
                    for (String path : paths) {
                        File filePhoto = new File(path);
                        uris.add(FileProvider.getUriForFile(MissionUpdateActivity.this,
                                BuildConfig.APPLICATION_ID, filePhoto));
                    }

                    Intent intentSms = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intentSms.putExtra("address", phone);
                    intentSms.setType("image/*");
                    intentSms.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    intentSms.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intentSms);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_CODE) {
            Bitmap bitmap = BitmapFactory.decodeFile(dirAppPhoto);
            if (bitmap != null) {
                paths.add(dirAppPhoto);
                ((MissionUpdateAdapter) photoGrid.getAdapter()).notifyDataSetChanged();
            } else {
                System.out.println("Null bitmap at " + dirAppPhoto);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem delete = menu.add("Delete");

        delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                MissionUpdateAdapter adapter = (MissionUpdateAdapter) photoGrid.getAdapter();
                adapter.remove(photoGrid.getItemAtPosition(info.position));
                Toast.makeText(MissionUpdateActivity.this, "Photo deleted", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_back:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
