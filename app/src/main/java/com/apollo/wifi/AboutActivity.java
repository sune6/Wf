package com.apollo.wifi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 关于
 */
public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tvTopBarTitle = (TextView) findViewById(R.id.tv_top_bar_title);
        tvTopBarTitle.setText(R.string.title_activity_about);

        ImageView ivTopBarBack = (ImageView) findViewById(R.id.iv_top_bar_back);
        ivTopBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.finish();
            }
        });

    }

}
