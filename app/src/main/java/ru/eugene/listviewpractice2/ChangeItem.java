package ru.eugene.listviewpractice2;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ChangeItem extends Activity {
    Button save;
    EditText url;
    EditText name;
    Context context;
    String target;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_item);
        save = (Button) findViewById(R.id.save);
        name = (EditText) findViewById(R.id.name);
        url = (EditText) findViewById(R.id.url);
        context = this;
        target = this.getIntent().getStringExtra("target");
        if (target.equals("edit")) {
            name.setText(this.getIntent().getStringExtra("name"));
            url.setText(this.getIntent().getStringExtra("url"));
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                    String nameS = name.getText().toString().trim();
                    String urlS = url.getText().toString().trim();
                    if (!nameS.isEmpty() && !urlS.isEmpty() && URLUtil.isValidUrl(urlS)) {
                        resultIntent.putExtra("name", nameS);
                        resultIntent.putExtra("url", urlS);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(context, "             Incorrect url! \n format required http://bash.im/rss", Toast.LENGTH_SHORT).show();
                    }
            }
        });

    }

}
