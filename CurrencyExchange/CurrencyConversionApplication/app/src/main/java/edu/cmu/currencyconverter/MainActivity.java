// 1. Name - Yash Jahagirdar      Andrew id - ybj
// 2. Name - Leo Lin              Andrew id – hungfanl
// Project 4.
package edu.cmu.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    // Text for inputting the currency to be converted.
    EditText fromInput;
    // Text to be displayed after converting. Output
    TextView toOutput;
    // Submit button and Reset button
    Button submitButton, resetButton;
    // from Dropdown for converting currency
    // To Dropdown for converting currency.
    Spinner fromDropdown, toDropdown;
    // The selected from Currency- fromCurrency.
    // The selected to Currency - toCurrency
    String fromCurrency, toCurrency;

    /**
     * On Create method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fromDropdown = findViewById(R.id.fromCurrency);
        toDropdown = findViewById(R.id.toCurrency);
        fromInput = findViewById(R.id.from);
        toOutput = findViewById(R.id.convertedCurrency);
        submitButton = findViewById(R.id.submitButton);
        resetButton = findViewById(R.id.resetButton);
        // Reference and citation for Array Adapter-
        //1. https://stackoverflow.com/questions/2784081/android-create-spinner-programmatically-from-array
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.conversionOptions));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromDropdown.setAdapter(adapter);
        toDropdown.setAdapter(adapter);
        setupListeners();
    }

    /**
     * Set up listeners
     * When the submit button or the reset button is clicked,call heroku.
     */
    void setupListeners() {
        fromDropdown.setOnItemSelectedListener(new FromDropdown());
        toDropdown.setOnItemSelectedListener(new ToDropdown());
        // Submit button is clicked, call heroku
        submitButton.setOnClickListener(v -> {
            // when the input from user is null or there is no input, then
            // the user is told to enter a correct number.
            if (fromInput.getText().toString() == null || fromInput.getText().toString().isEmpty()) {
                fromInput.setHint(getString(R.string.fromText));
            } else {
                // the options for conversion are same.
                if (fromCurrency == toCurrency) {
                    toOutput.setText("The converted amount is " + toCurrency + " " + fromInput.getText().toString());
                } else {
                    // The user chooses different conversion options.
                    // and the user has correct input.
                    // then the heroku url is called and data from api is displayed as response
                    String[] params = fromInput.getText().toString().split(" ");
                    // Using executorService is based on the documentation provided by Android.
                    // Reference and Citation for the same-
                    // https://developer.android.com/reference/java/util/concurrent/ExecutorService
                    ExecutorService service = Executors.newSingleThreadExecutor();
                    service.execute(new Runnable() {
                        String response = "";

                        @Override
                        public void run() {
                            try {
                                // Calling the url from heroku.
                                URL url = new URL("https://sleepy-sierra-12630.herokuapp.com/CurrencyExchange?currencyFrom=" + fromCurrency + "&currencyTo=" + toCurrency + "&amountString=" + params[0]);
                                URLConnection conn = url.openConnection();
                                conn.connect();
                                // input stream from api.
                                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                                // For converting from BufferedInputStream to String, Reference and Citation-
                                // 1. https://stackoverflow.com/questions/5713857/bufferedinputstream-to-string-conversion
                                byte[] bytes = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = bis.read(bytes)) != -1) {
                                    response += new String(bytes, 0, bytesRead);
                                }
                            } catch (Exception e) {
                                v.invalidate();
                            }
                            // After the response is received.
                            runOnUiThread(() -> {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    String displayString = object.getString("Exchange Amount");

                                    // Set the output.
                                    toOutput.setText(
                                            "The converted amount is " + toCurrency + " " +
                                                    displayString);
                                } catch (Exception e) {
                                    v.invalidate();
                                }
                            });
                        }
                    });
                }
            }
        });

        // Reset button is clicked.
        resetButton.setOnClickListener(v -> {
            // Using executorService is based on the documentation provided by Android.
            // Reference and Citation for the same-
            // https://developer.android.com/reference/java/util/concurrent/ExecutorService
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(() -> runOnUiThread(() -> {
                try {
                    // Reset the dropdowns and the input and output is reset.
                    toOutput.setText(null);
                    fromInput.setText(null);
                    toOutput.setHint(getString(R.string.convertedString));
                    fromInput.setHint(getString(R.string.fromText));
                    fromDropdown.setSelection(0);
                    toDropdown.setSelection(0);
                } catch (Exception e) {
                    v.invalidate();
                }
            }));
        });
    }


    /**
     * Used to get the item from the From Dropdown
     */
    class FromDropdown implements AdapterView.OnItemSelectedListener {
        // If an item is selected.
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            fromCurrency = parent.getItemAtPosition(position).toString();
        }

        // If an item is not selected.
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }


    /**
     * Used to get the item selected from the To dropdown.
     */
    class ToDropdown implements AdapterView.OnItemSelectedListener {
        // If an item is selected.
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            toCurrency = parent.getItemAtPosition(position).toString();
        }

        // If an item is not selected.
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }


}



