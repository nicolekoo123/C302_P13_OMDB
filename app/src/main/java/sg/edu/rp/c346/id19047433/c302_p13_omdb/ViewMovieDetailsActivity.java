package sg.edu.rp.c346.id19047433.c302_p13_omdb;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static android.content.ContentValues.TAG;

public class ViewMovieDetailsActivity extends AppCompatActivity {

    private EditText etTitle, etRated, etReleased, etRuntime, etGenre, etActors, etPlot, etLanguage, etPoster;
    private Button btnUpdate, btnDelete;
    private String movieId;

    // TODO: Task 1 - Declare Firebase variables
    private FirebaseFirestore db;
    private CollectionReference colRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_movie_details);

        etTitle = findViewById(R.id.etTitle);
        etRated = findViewById(R.id.etRated);
        etReleased = findViewById(R.id.etReleased);
        etRuntime = findViewById(R.id.etRuntime);
        etGenre = findViewById(R.id.etGenre);
        etActors = findViewById(R.id.etActors);
        etPlot = findViewById(R.id.etPlot);
        etLanguage = findViewById(R.id.etLanguage);
        etPoster = findViewById(R.id.etPoster);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        Intent intent = getIntent();
        movieId = intent.getStringExtra("movie_id");

        // TODO: Task 2: Get FirebaseFirestore instance
        db = FirebaseFirestore.getInstance();

        colRef = db.collection("movies");

	//TODO: get the movie record from Firestore based on the movieId
	// set the edit fields with the detail
        // TODO: Task 3: Get document reference by the student's id and set the name and age to EditText
        DocumentReference docRef = db.collection("movies").document(movieId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (value != null && value.exists()) {
                    Log.d(TAG, "Current data: " + value.getData());
                    String title = value.getString("title");
                    String rated = value.getString("rating");
                    String release = value.getString("released");
                    String runtime = value.getString("runtime");
                    String genre = value.getString("genre");
                    String actor = value.getString("actors");
                    String plot = value.getString("plot");
                    String language = value.getString("language");
                    String poster = value.getString("poster");
                    etTitle.setText(title);
                    etRated.setText(rated);
                    etReleased.setText(release);
                    etRuntime.setText(runtime);
                    etGenre.setText(genre);
                    etActors.setText(actor);
                    etPlot.setText(plot);
                    etLanguage.setText(language);
                    etPoster.setText(poster);

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpdateOnClick(v);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDeleteOnClick(v);
            }
        });
    }//end onCreate

    
    private void btnUpdateOnClick(View v) {
		//TODO: create a Movie object and populate it with the values in the edit fields
		//save it into Firestore based on the movieId
        //TODO: Task 4: Update Student record based on input given
        colRef.document(movieId)
                .update("title", etTitle.getText().toString(), "rating", etRated.getText().toString(), "released", etReleased.getText().toString(), "runtime", etRuntime.getText().toString(), "genre", etGenre.getText().toString(), "actors", etActors.getText().toString(), "plot", etPlot.getText().toString(), "language", etLanguage.getText().toString(), "poster", etPoster.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Movie item = new Movie(etTitle.getText().toString(), etRated.getText().toString(), etReleased.getText().toString(), etRuntime.getText().toString(), etGenre.getText().toString(), etActors.getText().toString(), etPlot.getText().toString(), etLanguage.getText().toString(), etPoster.getText().toString());
                        colRef.document(movieId).set(item);
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        Toast.makeText(getApplicationContext(), "Item record updated successfully", Toast.LENGTH_SHORT).show();

        finish();
    }//end btnUpdateOnClick

    private void btnDeleteOnClick(View v) {
		//TODO: delete from Firestore based on the movieId
        //TODO: Task 5: Delete Student record based on student id
        colRef.document(movieId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
        Toast.makeText(getApplicationContext(), "Item record deleted successfully", Toast.LENGTH_SHORT).show();

        finish();
    }//end btnDeleteOnClick

}//end class