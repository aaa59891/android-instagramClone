package com.example.chongchenlearn901.instagramclone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class UserFeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        String username = getIntent().getStringExtra(UserListActivity.INTENT_USERNAME);
        ParseQuery.getQuery("images").whereEqualTo("username", username).findInBackground((images, e) -> {
            if(e != null){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            for (ParseObject image : images) {
                ParseFile file = image.getParseFile("image");

                file.getDataInBackground((data, e1) -> {
                    if(e1 != null){
                        Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    imageView.setImageBitmap(bitmap);
                    linearLayout.addView(imageView);
                });
            }
        });
    }
}
