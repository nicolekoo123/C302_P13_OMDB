package sg.edu.rp.c346.id19047433.c302_p13_omdb;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class CreateMovieActivity extends AppCompatActivity {

    private EditText etTitle, etRated, etReleased, etRuntime, etGenre, etActors, etPlot, etLanguage, etPoster;
    private Button btnCreate, btnSearch;
    private ImageButton btnCamera;
    private String apikey;
    private SharedPreferences preferences;
    private AsyncHttpClient client;

    // TODO: Task 1 - Declare Firebase variables
    private FirebaseFirestore db;
    private CollectionReference colRef;
    private DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_movie);

        etTitle = findViewById(R.id.etTitle);
        etRated = findViewById(R.id.etRated);
        etReleased = findViewById(R.id.etReleased);
        etRuntime = findViewById(R.id.etRuntime);
        etGenre = findViewById(R.id.etGenre);
        etActors = findViewById(R.id.etActors);
        etPlot = findViewById(R.id.etPlot);
        etLanguage = findViewById(R.id.etLanguage);
        etPoster = findViewById(R.id.etPoster);
        btnCreate = findViewById(R.id.btnCreate);
        btnSearch = findViewById(R.id.btnSearch);
        btnCamera = findViewById(R.id.btnCamera);

        client = new AsyncHttpClient();

        // TODO: Task 2: Get FirebaseFirestore instance and collection reference to "students"
        db = FirebaseFirestore.getInstance();

        colRef = db.collection("movies");

        //TODO: Retrieve the apikey from SharedPreferences
		//If apikey is empty, redirect back to LoginActivity
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        apikey = preferences.getString("apiKey", "").trim();
        if (apikey.equalsIgnoreCase("")){
            Intent i = new Intent(CreateMovieActivity.this, LoginActivity.class);
            startActivity(i);
        }

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCreateOnClick(v);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSearchOnClick(v);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCameraOnClick(v);
            }
        });

    }//end onCreate

	//TODO: extract the fields and populate into a new instance of Movie class
	// Add the new movie into Firestore
    private void btnCreateOnClick(View v) {
        //TODO: Task 3: Retrieve name and age from EditText and instantiate a new Student object
        String title = etTitle.getText().toString();
        String rated = etRated.getText().toString();
        String released = etReleased.getText().toString();
        String runtime = etRuntime.getText().toString();
        String genre = etGenre.getText().toString();
        String actors = etActors.getText().toString();
        String plot = etPlot.getText().toString();
        String language = etLanguage.getText().toString();
        String poster = etPoster.getText().toString();

        Movie msg = new Movie(title, rated, released, runtime, genre, actors, plot, language, poster);
        //TODO: Task 4: Add student to database and go back to main screen
        colRef.add(msg);
        finish();
    }

	//TODO: Call www.omdbapi.com passing the title and apikey as parameters
	// extract from JSON response and set into the edit fields
    private void btnSearchOnClick(View v) {
        RequestParams params = new RequestParams();
        params.add("apikey", apikey);
        params.add("t", etTitle.getText().toString());
        System.out.println(apikey);

        client.get("http://www.omdbapi.com/",params ,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    etTitle.setText(response.getString("Title"));
                    etRated.setText(response.getString("Rated"));
                    etReleased.setText(response.getString("Released"));
                    etRuntime.setText(response.getString("Runtime"));
                    etGenre.setText(response.getString("Genre"));
                    etActors.setText(response.getString("Actors"));
                    etPlot.setText(response.getString("Plot"));
                    etLanguage.setText(response.getString("Language"));
                    etPoster.setText(response.getString("Poster"));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CreateMovieActivity.this,"Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void btnCameraOnClick(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");


            //TODO: feed imageBitmap into FirebaseVisionImage for text recognizing
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            InputImage image = InputImage.fromBitmap(imageBitmap, 0);
            Task<Text> result = recognizer.process(image)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(@NonNull Text text) {
                            // Task completed successfully
                            for (Text.TextBlock block : text.getTextBlocks()) {
                                String blockText = block.getText();
                                etTitle.setText(blockText);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            e.printStackTrace();
                        }
                    });
        }
    }
}