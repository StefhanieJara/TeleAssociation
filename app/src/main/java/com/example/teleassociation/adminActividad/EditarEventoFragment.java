package com.example.teleassociation.adminActividad;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teleassociation.R;
import com.example.teleassociation.adminGeneral.EditarActividadAdmin;
import com.example.teleassociation.adminGeneral.inicioAdmin;
import com.example.teleassociation.dto.eventoCrear;
import com.example.teleassociation.dto.usuarioSesion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditarEventoFragment extends Fragment {

    private FirebaseFirestore db;
    FirebaseAuth mAuth;
    String nombreDelegado;
    String codigoDelegado;
    TextView nameUser;
    private AutoCompleteTextView delegadoEdit;
    private ArrayAdapter<String> adapterItems;
    private TextInputLayout tituloActividadEdit;
    private TextInputLayout titulodescripcionEdit;
    private AutoCompleteTextView lugar;
    private AutoCompleteTextView estado;
    String idEvento;
    eventoCrear eventoCrear;

    public static EditarEventoFragment newInstance(String nombreEvento, String descripcionEvento, Date fechaEvento, String lugarEvento, String estadoEvento, String idEvento) {
        EditarEventoFragment fragment = new EditarEventoFragment();
        Bundle args = new Bundle();
        args.putString("nombreEvento", nombreEvento);
        args.putString("descripcionEvento", descripcionEvento);
        args.putString("fechaEvento", String.valueOf(fechaEvento));
        args.putString("lugarEvento", lugarEvento);
        args.putString("estadoEvento", estadoEvento);
        args.putString("idEvento", idEvento);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_evento, container, false);

        db = FirebaseFirestore.getInstance();

        obtenerDatosUsuario(usuarioSesion -> {
            //Log.d("msg-test", "El nombre del usuario fuera del collection es: " + usuario.getNombre());
            Log.d("msg-test", "El nombre del usuario fuera del collection es: " + usuarioSesion.getNombre());
            nombreDelegado = usuarioSesion.getNombre();
            codigoDelegado = usuarioSesion.getId();
            // Ahora puedes utilizar el nombre del usuario como lo necesites, por ejemplo:

            Log.d("msg-test", "El codigo del usuario fuera del collection es: " + codigoDelegado);

            nameUser = view.findViewById(R.id.nameUser);
            nameUser.setText(nombreDelegado);

            Button buttonEditarActividad = view.findViewById(R.id.button12);
            TextInputEditText titulo = view.findViewById(R.id.titulo);
            TextInputEditText descripcion = view.findViewById(R.id.descrEvent);
            TextInputEditText fecha = view.findViewById(R.id.fechaDelEvento);
            lugar = view.findViewById(R.id.lugarDelEvento);
            estado = view.findViewById(R.id.EstadodelEvento);
            String eventoParticipante = getArguments().getString("nombreEvento"); // Asegúrate de que sea "nombreEvento" y no "nombre_evento"
            String descripcionEvento = getArguments().getString("descripcionEvento");
            String fechaEvento = getArguments().getString("fechaEvento");
            String lugarEvento = getArguments().getString("lugarEvento");
            String estadoEvento = getArguments().getString("estadoEvento");
            idEvento = getArguments().getString("idEvento");
            Log.d("msg-test", "el nombre del evento es: "+eventoParticipante+ " "+descripcionEvento+" "+fechaEvento+" "+lugarEvento+" "+estadoEvento+" "+idEvento);
            String formattedDateString = formatDateString(fechaEvento);

            titulo.setText(eventoParticipante);
            descripcion.setText(descripcionEvento);
            fecha.setText(formattedDateString);
            lugar.setText(lugarEvento);
            estado.setText(estadoEvento);

            // Configura el campo de texto para que sea de solo lectura
            fecha.setInputType(InputType.TYPE_NULL);
            fecha.setOnTouchListener((v, event) -> {
                int inType = fecha.getInputType(); // Conserva la configuración original del inputType
                fecha.setInputType(InputType.TYPE_NULL); // Desactiva la edición
                fecha.onTouchEvent(event); // Procesa el evento táctil

                // Restaura el inputType original después de procesar el evento táctil
                fecha.setInputType(inType);
                return true; // Evita que se consuma el evento táctil
            });
            fecha.setOnClickListener(v -> showDateTimePickerDialog());

            String[] items = {"Minas", "Bati", "Digimundo", "Estacionamiento de Letras", "Polideportivo"};
            adapterItems = new ArrayAdapter<>(requireContext(), R.layout.list_item, items);
            lugar.setAdapter(adapterItems);
            lugar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedName = adapterView.getItemAtPosition(i).toString();
                    // Puedes realizar acciones con el nombre seleccionado aquí
                    Toast.makeText(requireContext(), "Seleccionado: " + selectedName, Toast.LENGTH_SHORT).show();
                }
            });

            String[] items2 = {"proceso", "finalizado"};
            adapterItems = new ArrayAdapter<>(requireContext(), R.layout.list_item, items2);
            estado.setAdapter(adapterItems);
            estado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedName = adapterView.getItemAtPosition(i).toString();
                    // Puedes realizar acciones con el nombre seleccionado aquí
                    Toast.makeText(requireContext(), "Seleccionado: " + selectedName, Toast.LENGTH_SHORT).show();
                }
            });


            buttonEditarActividad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tituloEvento = titulo.getText().toString().trim();
                    String descripcionEvento = descripcion.getText().toString().trim();
                    String fechaEvento = fecha.getText().toString().trim();
                    String lugarEvento = lugar.getText().toString().trim();
                    String estadoEvento = estado.getText().toString().trim();

                    if (tituloEvento.isEmpty() || descripcionEvento.isEmpty() || fechaEvento.isEmpty() || lugarEvento.isEmpty() ||estadoEvento.isEmpty()) {
                        Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                    }else{
                        eventoCrear = new eventoCrear();
                        if(lugarEvento.equals("Bati")){
                            eventoCrear.setUbicacion(-12.073198534215251,-77.08159029588224);
                        } else if (lugarEvento.equals("Digimundo")) {
                            eventoCrear.setUbicacion(-12.07316474474935,-77.08135327613498);
                        } else if (lugarEvento.equals("Minas")) {
                            eventoCrear.setUbicacion(-12.0721793337368,-77.08197205625702);
                        } else if (lugarEvento.equals("Polideportivo")) {
                            eventoCrear.setUbicacion(-12.0721793337368,-77.08197205625702);
                        } else if (lugarEvento.equals("Local de ensayo")) {
                            eventoCrear.setUbicacion(-12.075643035700846,-77.06511929051032);
                        } else if (lugarEvento.equals("Estacionamiento de Letras")) {
                            eventoCrear.setUbicacion(-12.0721793337368,-77.08197205625702);
                        }
                        actualizarInformacionFirebase(tituloEvento, descripcionEvento, fechaEvento, eventoCrear.getUbicacion(), estadoEvento,idEvento,lugarEvento);
                    }
                }
            });




        });



        return view;
    }

    private void actualizarInformacionFirebase(String tituloDelEvento, String descripcionDelEvento, String fechaDelEvento, GeoPoint lugarDelEvento, String estadoDelEvento, String id,String nombreDellugar) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("eventos")
                .document(id)  // Utiliza el ID directamente en lugar de whereEqualTo
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Aquí obtienes el documento con el ID específico
                                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    Date fechaHoraDate = formato.parse(fechaDelEvento);
                                    Timestamp timestamp = new Timestamp(fechaHoraDate);

                                    document.getReference()
                                            .update("nombre", tituloDelEvento,
                                                    "descripcion", descripcionDelEvento,
                                                    "fecha", timestamp,
                                                    "ubicacion", lugarDelEvento,
                                                    "nombre_lugar",nombreDellugar,
                                                    "estado", estadoDelEvento)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //Toast.makeText(getContext(), "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getContext(), ListaActividadesDelactvActivity.class);
                                                    intent.putExtra("Evento actualizado.", true);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Error al guardar los cambios", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });

    }


    private void showDateTimePickerDialog() {
        // Obtén la fecha y la hora actuales
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Configura la fecha máxima permitida (31 de diciembre de 2023)
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(2023, Calendar.DECEMBER, 31);

        // Configura la fecha mínima permitida (fecha actual)
        Calendar minDate = Calendar.getInstance();

        // Crea una instancia de DatePickerDialog para seleccionar la fecha
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // El usuario ha seleccionado una fecha
                    Calendar selectedDateTime = Calendar.getInstance();
                    selectedDateTime.set(selectedYear, selectedMonth, selectedDay);

                    // Comprueba si la fecha seleccionada es futura y mayor o igual a la fecha actual
                    if (selectedDateTime.before(maxDate) || selectedDateTime.equals(maxDate)) {
                        // Crea una instancia de TimePickerDialog para seleccionar la hora
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                requireContext(),
                                (view1, selectedHour, selectedMinute) -> {
                                    // El usuario ha seleccionado la hora
                                    selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                                    selectedDateTime.set(Calendar.MINUTE, selectedMinute);

                                    // Formatea la fecha y la hora según el patrón deseado
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                    String formattedDateTime = dateFormat.format(selectedDateTime.getTime());

                                    // Actualiza el texto en el TextInputEditText con la fecha y la hora formateadas
                                    TextInputEditText fechaEditText = requireView().findViewById(R.id.fechaDelEvento);
                                    fechaEditText.setText(formattedDateTime);
                                },
                                hour, minute, true);

                        // Muestra el TimePickerDialog
                        timePickerDialog.show();
                    } else {
                        // La fecha seleccionada es después del límite máximo permitido o antes de la fecha actual
                        Toast.makeText(requireContext(), "Selecciona una fecha futura y después de la fecha actual", Toast.LENGTH_SHORT).show();
                    }
                },
                year, month, day);

        // Establece la fecha máxima permitida en el DatePickerDialog
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        // Establece la fecha mínima permitida en el DatePickerDialog
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        // Muestra el DatePickerDialog
        datePickerDialog.show();
    }

    private void obtenerDatosUsuario(CrearEventoFragment.FirestoreCallback callback) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        usuarioSesion usuarioSesion = new usuarioSesion();

        if (user != null) {
            String email = user.getEmail();

            Log.d("msg-test", "el email es: " + email);

            db.collection("usuarios")
                    .get()
                    .addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            QuerySnapshot usuariosCollection = task2.getResult();
                            for (QueryDocumentSnapshot document : usuariosCollection) {
                                String codigo = document.getId();
                                String correo = (String) document.get("correo");
                                String nombre = (String) document.get("nombre");

                                if (correo.equals(email)) {
                                    Log.d("msg-test", "datos del usuario " + codigo + "  correo " + " nombre");
                                    usuarioSesion.setId(codigo);
                                    usuarioSesion.setNombre(nombre);
                                    usuarioSesion.setCorreo(correo);
                                    // Llamada al método de la interfaz con el nombre del usuario
                                    callback.onCallback(usuarioSesion);
                                    return;
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Maneja la excepción que ocurra al intentar obtener los documentos
                        Log.e("msg-test", "Excepción al obtener documentos de la colección usuarios: ", e);
                    });
        }
    }

    public interface FirestoreCallback {
        void onCallback(usuarioSesion usuario);
    }

    public static String formatDateString(String inputDateString) {
        try {
            // Crea un objeto SimpleDateFormat con el patrón de fecha original
            SimpleDateFormat originalFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

            // Convierte la cadena de fecha original a un objeto Date
            Date date = originalFormat.parse(inputDateString);

            // Crea un nuevo objeto SimpleDateFormat con el patrón deseado
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

            // Formatea la fecha al nuevo patrón y devuelve la cadena resultante
            return newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Manejar la excepción según sea necesario
        }
    }
}