package CodePath.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.io.FileUtils.readLines;
import static org.apache.commons.io.FileUtils.write;
import static org.apache.commons.io.FileUtils.writeLines;
import static org.apache.commons.io.FileUtils.writeStringToFile;

public class EditActivity extends AppCompatActivity {

    EditText etItem;
    Button btnSave;
    TextView textView;
    RecyclerView rvitems;
    ItemsAdapter itemsAdapter;
    private  int current_item = 0;

    private List<String> deletions;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws NullPointerException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        etItem = findViewById(R.id.etItem);
        btnSave = findViewById(R.id.btnSave);
        rvitems = findViewById(R.id.rvitems);

        loadItems();

        getSupportActionBar().setTitle("Edit item");

        etItem.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_TEXT));

        // When the user clicks on a deleted item, it populates the EditText line
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("EditActivity", "Single click at position " + position);
                etItem.setText(deletions.get(position));
                // Remove retrieved item from deleted list
                deletions.remove(position);
                itemsAdapter.notifyItemRemoved(position);
                // Save state of most recently deleted item
                saveItems();
            }
        };

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                ;
            }
        };

        itemsAdapter = new ItemsAdapter(deletions, onLongClickListener, onClickListener);
        rvitems.setAdapter(itemsAdapter);
        rvitems.setLayoutManager(new LinearLayoutManager(this));

        // When the user is done editing, they click on the update button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent which will contain the results
                Intent intent = new Intent();

                // Pass the results of editing (data)
                intent.putExtra(MainActivity.KEY_ITEM_TEXT, etItem.getText().toString());
                intent.putExtra(MainActivity.KEY_ITEM_POSITION, getIntent().getExtras().getInt(MainActivity.KEY_ITEM_POSITION));
                // Set the result of the intent
                setResult(RESULT_OK, intent);

                // Finish activity, close the screen and go back
                finish();
            }
        });
    }

    private File otherDataFile() {
        return new File(getFilesDir(), "deletions.txt");
    }
    // This function will load items by reading line of the data file
    private void loadItems() {
        try {
            current_item = 0;
            deletions = new ArrayList<>(readLines(otherDataFile(), Charset.defaultCharset()));
            // Recycler view will only showcase last 10 deleted items
            while(deletions.size() > 10) {
                deletions.remove(current_item);
                current_item++;
            }
        } catch (IOException e) {
            Log.e("EditActivity", "Error reading items", e);
            deletions = new ArrayList<>();
        }
    }

    private void saveItems() {
        try {
            writeLines(otherDataFile(), deletions);
        } catch (IOException e) {
            Log.e("Main Activity", "Error writing items", e);
        }
    }
}