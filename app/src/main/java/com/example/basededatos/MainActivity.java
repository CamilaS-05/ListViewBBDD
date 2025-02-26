package com.example.basededatos;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private EditText mensaje, editTextNombre, editTextColor, editTextFiltro;
    private ListView listView;
    private ArrayList<String> listaMuebles;
    private ArrayAdapter<String> adapter;
    private Button buttonMostrarOcultar;
    private boolean listaVisible = true; // Para saber si la lista está visible o no

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mensaje = findViewById(R.id.editTextTextPersonName);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextColor = findViewById(R.id.editTextColor);
        editTextFiltro = findViewById(R.id.editTextFiltro);
        listView = findViewById(R.id.listView);
        buttonMostrarOcultar = findViewById(R.id.buttonMostrarOcultar);

        listaMuebles = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaMuebles);
        listView.setAdapter(adapter);


        editTextFiltro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                filtrarDatos(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        consultarDatos();
    }


    public void insertarDatos(View view) {
        String nombre = editTextNombre.getText().toString();
        String color = editTextColor.getText().toString();

        if (nombre.isEmpty() || color.isEmpty()) {
            mensaje.setText("Debe ingresar todos los datos.");
            return;
        }

        AdmBaseDatosSQLite admin = new AdmBaseDatosSQLite(MainActivity.this, "MueblesDB", null, 1);
        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();
        registro.put("nombre", nombre);
        registro.put("color", color);
        baseDeDatos.insert("muebles", null, registro);

        mensaje.setText("Datos ingresados correctamente");
        consultarDatos();
    }

    public void borrarDatos(View view) {
        AdmBaseDatosSQLite admin = new AdmBaseDatosSQLite(MainActivity.this, "MueblesDB", null, 1);
        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();

        baseDeDatos.execSQL("DELETE FROM Muebles");
        mensaje.setText("Muebles borrados");
        listaMuebles.clear();
        adapter.notifyDataSetChanged();
    }


    private void consultarDatos() {
        AdmBaseDatosSQLite admin = new AdmBaseDatosSQLite(MainActivity.this, "MueblesDB", null, 1);
        SQLiteDatabase baseDeDatos = admin.getReadableDatabase();

        String query = "SELECT * FROM Muebles";
        Cursor c = baseDeDatos.rawQuery(query, null);

        listaMuebles.clear();
        while (c.moveToNext()) {
            @SuppressLint("Range") String name = c.getString(c.getColumnIndex("nombre"));
            @SuppressLint("Range") String color = c.getString(c.getColumnIndex("color"));
            listaMuebles.add(name + " - " + color);
        }

        if (listaMuebles.isEmpty()) {
            mensaje.setText("No hay datos");
        } else {
            mensaje.setText("");
        }

        adapter.notifyDataSetChanged();
    }


    public void filtrarDatos(CharSequence charSequence) {
        String filtro = charSequence.toString();
        AdmBaseDatosSQLite admin = new AdmBaseDatosSQLite(MainActivity.this, "MueblesDB", null, 1);
        SQLiteDatabase baseDeDatos = admin.getReadableDatabase();

        String query = "SELECT * FROM Muebles WHERE nombre LIKE ?";
        Cursor c = baseDeDatos.rawQuery(query, new String[]{"%" + filtro + "%"});

        listaMuebles.clear();
        while (c.moveToNext()) {
            @SuppressLint("Range") String name = c.getString(c.getColumnIndex("nombre"));
            @SuppressLint("Range") String color = c.getString(c.getColumnIndex("color"));
            listaMuebles.add(name + " - " + color);
        }

        if (listaMuebles.isEmpty()) {
            mensaje.setText("No hay muebles que coincidan.");
        } else {
            mensaje.setText("");
        }

        adapter.notifyDataSetChanged();
    }


    public void mostrarOcultarLista(View view) {
        if (listaVisible) {
            listView.setVisibility(View.GONE);
            buttonMostrarOcultar.setText("Mostrar Muebles");
        } else {
            listView.setVisibility(View.VISIBLE);
            buttonMostrarOcultar.setText("Ocultar Muebles");
        }

        listaVisible = !listaVisible;
    }
}
