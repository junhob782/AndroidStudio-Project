package com.cookandroid.a13week;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cookandroid.a13week.R;

public class MainActivity extends AppCompatActivity {

    EditText nameEditText, phoneEditText, searchEditText;
    Button addButton, searchButton, allButton;
    TextView resultTextView;

    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // XML 연결
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        searchEditText = findViewById(R.id.searchEditText);
        addButton = findViewById(R.id.addButton);
        searchButton = findViewById(R.id.searchButton);
        allButton = findViewById(R.id.allButton);
        resultTextView = findViewById(R.id.resultTextView);

        // SQLite DB 생성 및 테이블 생성
        database = openOrCreateDatabase("ContactsDB", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT)");

        // 추가 버튼 동작
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                if (!name.isEmpty() && !phone.isEmpty()) {
                    database.execSQL("INSERT INTO contacts (name, phone) VALUES ('" + name + "', '" + phone + "')");
                    Toast.makeText(MainActivity.this, "연락처가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    nameEditText.setText("");
                    phoneEditText.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "이름과 전화번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 탐색 버튼 동작
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchEditText.getText().toString();
                if (!searchQuery.isEmpty()) {
                    Cursor cursor = database.rawQuery("SELECT * FROM contacts WHERE name LIKE ? OR phone LIKE ?",
                            new String[]{"%" + searchQuery + "%", "%" + searchQuery + "%"});
                    displayResults(cursor);
                } else {
                    Toast.makeText(MainActivity.this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 전체 조회 버튼 동작
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = database.rawQuery("SELECT * FROM contacts", null);
                displayResults(cursor);
            }
        });
    }

    // 결과 표시 메서드
    private void displayResults(Cursor cursor) {
        StringBuilder results = new StringBuilder();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String phone = cursor.getString(2);
            results.append(id).append(" | ").append(name).append(" | ").append(phone).append("\n");
        }
        cursor.close();
        if (results.length() > 0) {
            resultTextView.setText(results.toString());
        } else {
            resultTextView.setText("결과가 없습니다.");
        }
    }
}