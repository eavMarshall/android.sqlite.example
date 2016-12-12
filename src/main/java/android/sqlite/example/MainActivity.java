package android.sqlite.example;

import android.content.ContentValues;
import android.elliott.database.Components.ListViewUpdater.ListViewUpdater;
import android.elliott.database.Components.ListViewUpdater.ViewListAdapter;
import android.elliott.database.DatabaseController;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private DatabaseController myDatabaseController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDatabaseController = new DatabaseController<MainActivity>(this, getPackageName(),
                getString(R.string.DATABASE_NAME), getResources().getInteger(R.integer.DATABASE_VERSION),
                getResources().getStringArray(R.array.CREATE_DATABASE)) {
            @Override
            public void sendErrorMessage(String errorMsg) {
                //send error message?
            }
        };
    }

    public void onDestroy() {
        super.onDestroy();
        myDatabaseController.killDatabase();
    }

    private Button addBtn;
    private EditText search;
    private ListViewUpdater listView;

    @Override
    protected void onStart() {
        super.onStart();
        addBtn = (Button) findViewById(R.id.addBtn);
        search = (EditText) findViewById(R.id.search);
        listView = (ListViewUpdater) findViewById(R.id.commentList);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search.getText().toString().equals("")) return;
                ContentValues cv = new ContentValues();
                cv.put(getString(R.string.CommentText), search.getText().toString());
                myDatabaseController.getStatementProcessor().insert(cv, getString(R.string.Comment));
                search.setText("");
            }
        });

        resetListView();

        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) resetListView();
                ViewListAdapter vla = new ViewListAdapter(
                        MainActivity.this,
                        myDatabaseController.getStatementProcessor().rawQuery(getString(R.string.searchComment), editable.toString()),
                        R.layout.comment,
                        listView
                );
                listView.setAdapter(vla);
            }
        });
    }

    public void resetListView() {
        ViewListAdapter vla = new ViewListAdapter(
                this,
                myDatabaseController.getStatementProcessor().rawQuery(getString(R.string.findAllComment)),
                R.layout.comment,
                listView
        );

        listView.setAdapter(vla);
    }
}
