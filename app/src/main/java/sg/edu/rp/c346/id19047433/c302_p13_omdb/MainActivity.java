package sg.edu.rp.c346.id19047433.c302_p13_omdb;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Movie> list;
    private MovieAdapter adapter;

    // TODO: Task 1 - Declare Firebase variables
    private FirebaseFirestore db;
    private CollectionReference colRef;
    private DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listViewMovies);
        list = new ArrayList<Movie>();

        // TODO: Task 2: Get FirebaseFirestore instance and reference
        db = FirebaseFirestore.getInstance();

        colRef = db.collection("students");
        docRef = colRef.document("students");

        list = new ArrayList<Movie>();
        adapter = new MovieAdapter(getApplicationContext(), R.layout.movie_row, list);
        listView.setAdapter(adapter);

		//TODO: retrieve all documents from the "movies" collection in Firestore (realtime)
		//populate the movie objects into the ListView
        //TODO: Task 3: Get real time updates from firestore by listening to collection "students"
        db.collection("movies")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        list.clear();
                        //TODO: Task 4: Read from Snapshot and add into ArrayAdapter for ListView
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("title") != null) {
                                list.add(new Movie(doc.getId(), Integer.parseInt(doc.getLong("year").toString()), doc.getString("title"), doc.getString("rating"), doc.getString("released"), doc.getString("runtime"), doc.getString("genre"), doc.getString("director"), doc.getString("writer"), doc.getString("actors"), doc.getString("plot"), doc.getString("language"), doc.getString("poster")));
                            }
                        }
                        Log.d(TAG, "Movies: " + list);
                        adapter.notifyDataSetChanged();
                    }
                });
        

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movie selectedContact = list.get(position);
                Intent i = new Intent(getBaseContext(), ViewMovieDetailsActivity.class);
                i.putExtra("movie_id", selectedContact.getMovieId());
                startActivity(i);

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            Intent intent = new Intent(getApplicationContext(), CreateMovieActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}