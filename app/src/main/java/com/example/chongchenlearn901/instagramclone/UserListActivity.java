package com.example.chongchenlearn901.instagramclone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {
    private static final String TAG = "UserListActivity";
    private ArrayAdapter<String> adapter;
    private ArrayList<String> userList;
    public static final String INTENT_USERNAME = "INTENT_USERNAME";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        userList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        ListView lvUserList = findViewById(R.id.lvUserList);
        lvUserList.setAdapter(adapter);
        lvUserList.setOnItemClickListener(listViewOnItemListener);

        updateUsernameList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuShare:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }else{
                        openPhoto();
                    }
                }
                break;
            case R.id.menuLogout:
                ParseUser.logOut();
                this.finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            openPhoto();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] bytes = stream.toByteArray();

                ParseFile file = new ParseFile("image.png", bytes);

                ParseObject object = new ParseObject("images");
                object.put("image", file);
                object.put("username", ParseUser.getCurrentUser().getUsername());

                object.saveInBackground((e) -> {
                    String msg = "Shared successfully!";
                    if(e != null){
                        msg = e.getMessage();
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                Log.e(TAG, "onActivityResult: ", e);
            }
        }
    }

    private void openPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    private void updateUsernameList(){
        ParseUser.getQuery().whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername()).findInBackground((users, e) -> {
            if(e != null){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                this.finish();
                return;
            }
            for(ParseUser user : users){
                userList.add(user.getUsername());
            }
            adapter.notifyDataSetChanged();
        });
    }

    private AdapterView.OnItemClickListener listViewOnItemListener = (parent, view, position, id) -> {
        Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
        intent.putExtra(INTENT_USERNAME, userList.get(position));
        startActivity(intent);
    };
}
