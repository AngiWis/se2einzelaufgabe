package at.klu.se2einzelaufgabe;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onSendClick(View view) {
        EditText field = findViewById(R.id.editMatrikelnummer);
        String matr = field.getText().toString();
        TextView output = findViewById(R.id.txtOutput);

        executor.submit(() -> {
            try {

                Socket socket = new Socket("se2-submission.aau.at", 20080);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bw.write(matr); // send the matrikelnummer to the server
                bw.write("\n"); // important, otherwise the server will not recognize the end of the input!
                bw.flush();
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String result = br.readLine();
                //bw.close();
                //br.close()
                socket.close();
                runOnUiThread(() -> {
                    output.setText(result);
                });


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void onCalcClick(View view) {
        EditText field = findViewById(R.id.editMatrikelnummer);
        String matr = field.getText().toString();
        TextView output = findViewById(R.id.txtCalcOutput);
        String result = checkPairs(matr);
        output.setText(result);
    }
    private static String checkPairs(String matr) {
        char[] digits = matr.toCharArray();
        int[] nums = new int[digits.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nums.length; i++) {
            nums[i] = digits[i] - '0';
        }
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (ggt(nums[i], nums[j]) > 1) {
                    sb.append(i + ", " + j + "\n");
                }
            }
        }
        return sb.toString();
    }
    private static int ggt(int a, int b) {
        if (b == 0) {
            return a;
        }
        return ggt(b, a % b);

    }

}