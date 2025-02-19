import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddComplaintActivity extends AppCompatActivity {
    private TextInputEditText etStudentEmail, etHealthIssue, etDescription, etTreatment;
    private MaterialButton btnSubmitComplaint;
    private FirebaseDatabase database;
    private DatabaseReference complaintsRef;
    private FirebaseStorage storage;
    private StorageReference studentDataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_complaint);

        etStudentEmail = findViewById(R.id.etStudentEmail);
        etHealthIssue = findViewById(R.id.etHealthIssue);
        etDescription = findViewById(R.id.etDescription);
        etTreatment = findViewById(R.id.etTreatment);
        btnSubmitComplaint = findViewById(R.id.btnSubmitComplaint);

        database = FirebaseDatabase.getInstance();
        complaintsRef = database.getReference("complaints");
        storage = FirebaseStorage.getInstance();
        studentDataRef = storage.getReference().child("uploads/dummy_student_data.csv");

        btnSubmitComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSubmitComplaint();
            }
        });
    }

    private void validateAndSubmitComplaint() {
        final String email = etStudentEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter Student Email", Toast.LENGTH_SHORT).show();
            return;
        }
        studentDataRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.ByteArrayInputStream(bytes)));
                String line;
                boolean studentExists = false;
                String studentName = "", contact = "";

                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length >= 3 && data[1].equals(email)) {
                        studentExists = true;
                        studentName = data[0];
                        contact = data[2];
                        break;
                    }
                }

                if (studentExists) {
                    submitComplaint(studentName, email, contact);
                } else {
                    Toast.makeText(AddComplaintActivity.this, "Student not found!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("CSV_ERROR", "Error reading student data", e);
            }
        }).addOnFailureListener(e -> Log.e("FIREBASE_STORAGE", "Failed to fetch CSV", e));
    }

    private void submitComplaint(String name, String email, String contact) {
        String healthIssue = etHealthIssue.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String treatment = etTreatment.getText().toString().trim();

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        HashMap<String, Object> complaintData = new HashMap<>();
        complaintData.put("name", name);
        complaintData.put("email", email);
        complaintData.put("contact", contact);
        complaintData.put("healthIssue", healthIssue);
        complaintData.put("description", description);
        complaintData.put("treatment", treatment);
        complaintData.put("date", currentDate);
        complaintData.put("time", currentTime);
        complaintData.put("location", "SGGSIE&T");

        complaintsRef.push().setValue(complaintData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddComplaintActivity.this, "Complaint Submitted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddComplaintActivity.this, "Failed to submit complaint", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}