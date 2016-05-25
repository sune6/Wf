package com.apollo.wifi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 免责声明
 */
public class DisclaimerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        TextView tvTopBarTitle = (TextView) findViewById(R.id.tv_top_bar_title);
        tvTopBarTitle.setText(R.string.title_activity_disclaimer);

        ImageView ivTopBarBack = (ImageView) findViewById(R.id.iv_top_bar_back);
        ivTopBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisclaimerActivity.this.finish();
            }
        });
    }

}
