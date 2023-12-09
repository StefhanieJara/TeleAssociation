package com.example.teleassociation.adminActividad;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.teleassociation.R;
import com.example.teleassociation.adminActividad.CrearEventoFragment;
import com.example.teleassociation.adminGeneral.inicioAdmin;
import com.example.teleassociation.dto.actividad;
import com.example.teleassociation.dto.evento;
import com.example.teleassociation.dto.eventoCrear;
import com.example.teleassociation.dto.participante;
import com.example.teleassociation.dto.usuarioSesion;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CrearEventoFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference reference;
    FirebaseAuth mAuth;
    String nombreDelegado;
    String codigoDelegado;

    private AutoCompleteTextView lugar;
    private ArrayAdapter<String> adapterItems;
    private View rootView; // Declarar rootView aquí
    // URI de la imagen seleccionada
    private Uri uri;
    TextView nameUser;
    participante participante;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_crear_evento, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        reference=storage.getReference();

        obtenerDatosUsuario(usuarioSesion -> {
            //Log.d("msg-test", "El nombre del usuario fuera del collection es: " + usuario.getNombre());
            Log.d("msg-test", "El nombre del usuario fuera del collection es: " + usuarioSesion.getNombre());
            nombreDelegado = usuarioSesion.getNombre();
            codigoDelegado = usuarioSesion.getId();
            // Ahora puedes utilizar el nombre del usuario como lo necesites, por ejemplo:

            Log.d("msg-test", "El codigo del usuario fuera del collection es: " + codigoDelegado);

            nameUser = rootView.findViewById(R.id.nameUser);
            nameUser.setText(nombreDelegado);

            db.collection("actividad")
                    .whereEqualTo("delegado", nombreDelegado)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            String nombreActividad = documentSnapshot.getString("nombre");
                            int activo = Math.toIntExact(documentSnapshot.getLong("activo"));

                            TextInputEditText fechaEditText = rootView.findViewById(R.id.fecha);
                            TextInputEditText eventoNombre = rootView.findViewById(R.id.evento);
                            TextInputEditText descripcion = rootView.findViewById(R.id.descripcion);
                            Button button80 = rootView.findViewById(R.id.button80);
                            Button botonimage = rootView.findViewById(R.id.imagen);

                            /*fechaEditText.setEnabled(false);
                            eventoNombre.setEnabled(false);
                            descripcion.setEnabled(false);
                            button80.setEnabled(false);
                            botonimage.setEnabled(false);*/

                            if(activo==0){
                                Log.e("msg-test", "El estado de la actividad es "+ activo);
                                fechaEditText.setEnabled(false);
                                eventoNombre.setEnabled(false);
                                descripcion.setEnabled(false);
                                button80.setEnabled(false);
                                botonimage.setEnabled(false);
                                Toast.makeText(getContext(), "No se pueden crear mas eventos.", Toast.LENGTH_SHORT).show();
                            }else{
                                Log.e("msg-test", "El estado de la actividad es "+ activo);
                                String[] items = {"Minas", "Bati", "Digimundo", "Estacionamiento de Letras", "Polideportivo"};

                                lugar = rootView.findViewById(R.id.lugar);
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

                                // Configura el campo de texto para que sea de solo lectura
                                fechaEditText.setInputType(InputType.TYPE_NULL);

// Desactiva la interacción táctil directa con el campo de texto
                                fechaEditText.setOnTouchListener((v, event) -> {
                                    int inType = fechaEditText.getInputType(); // Conserva la configuración original del inputType
                                    fechaEditText.setInputType(InputType.TYPE_NULL); // Desactiva la edición
                                    fechaEditText.onTouchEvent(event); // Procesa el evento táctil

                                    // Restaura el inputType original después de procesar el evento táctil
                                    fechaEditText.setInputType(inType);
                                    return true; // Evita que se consuma el evento táctil
                                });

// Configura el clic para mostrar el selector de fecha
                                fechaEditText.setOnClickListener(v -> showDateTimePickerDialog());

                                button80.setOnClickListener(view -> {
                                    String nombreEvento = eventoNombre.getText().toString().trim();
                                    String descripcionEvento = descripcion.getText().toString().trim();
                                    String lugarName = lugar.getText().toString().trim();
                                    String fecha = fechaEditText.getText().toString().trim();

                                    if (nombreEvento.isEmpty() || descripcionEvento.isEmpty() || lugarName.isEmpty() || fecha.isEmpty()) {
                                        Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String cod_al = generateRandomCode();

                                        eventoCrear evento = new eventoCrear();
                                        evento.setApoyos("1");
                                        evento.setDescripcion(descripcionEvento);
                                        evento.setEstado("proceso");
                                        evento.setNombre(nombreEvento);
                                        evento.setNombre_lugar(lugarName);
                                        if(lugarName.equals("Bati")){
                                            evento.setUbicacion(-12.073198534215251,-77.08159029588224);
                                        } else if (lugarName.equals("Digimundo")) {
                                            evento.setUbicacion(-12.07316474474935,-77.08135327613498);
                                        } else if (lugarName.equals("Minas")) {
                                            evento.setUbicacion(-12.0721793337368,-77.08197205625702);
                                        } else if (lugarName.equals("Polideportivo")) {
                                            evento.setUbicacion(-12.0721793337368,-77.08197205625702);
                                        } else if (lugarName.equals("Local de ensayo")) {
                                            evento.setUbicacion(-12.075643035700846,-77.06511929051032);
                                        } else if (lugarName.equals("Estacionamiento de Letras")) {
                                            evento.setUbicacion(-12.0721793337368,-77.08197205625702);

                                        }
                                        evento.setNombre_actividad(nombreActividad);
                                        evento.setDelegado(nombreDelegado);

                                        // Subir la imagen a Firebase Storage
                                        StorageReference imageRef = reference.child("eventos/" + uri.getLastPathSegment());
                                        UploadTask uploadTask = imageRef.putFile(uri);

                                        uploadTask.addOnFailureListener(exception -> {
                                            exception.printStackTrace();
                                            Log.e("msg-test", "Error en la carga de la imagen", exception);
                                        }).addOnSuccessListener(taskSnapshot -> {
                                            // Obtén la URL de la imagen subida
                                            imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                                // Guarda la URL de la imagen en el evento
                                                evento.setUrl_imagen(downloadUri.toString());

                                                // Manejo del formato de fecha
                                                //String fechaHoraString = "2023-11-01 15:30:00";
                                                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                                try {
                                                    // Parsea la cadena en un objeto Date
                                                    //Date fechaHoraDate = formato.parse(fechaHoraString);
                                                    Date fechaHoraDate = formato.parse(fecha);

                                                    // Crea un Timestamp a partir del objeto Date
                                                    Timestamp timestamp = new Timestamp(fechaHoraDate);
                                                    evento.setFecha(timestamp);

                                                    Log.d("msg-test", evento.getNombre() + " " + evento.getNombre_lugar() + " .");

                                                    db.collection("eventos")
                                                            .document(cod_al)
                                                            .set(evento)
                                                            .addOnSuccessListener(unused -> {
                                                                // Luego, agrega un documento a la colección "participantes"
                                                                Map<String, Object> participanteData = new HashMap<>();
                                                                participanteData.put("asignacion", "Delegado");
                                                                participanteData.put("codigo", codigoDelegado);
                                                                participanteData.put("evento", evento.getNombre());
                                                                participanteData.put("nombre", nombreDelegado);

                                                                db.collection("participantes")
                                                                        .add(participanteData)
                                                                        .addOnSuccessListener(documentReference -> {
                                                                            // Después de agregar el participante, inicia la nueva actividad
                                                                            Intent intent = new Intent(getContext(), ListaActividadesDelactvActivity.class);
                                                                            intent.putExtra("Evento creado.", true);
                                                                            startActivity(intent);
                                                                        })
                                                                        .addOnFailureListener(e -> {
                                                                            Toast.makeText(getContext(), "Algo pasó al guardar el participante", Toast.LENGTH_SHORT).show();
                                                                        });
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(getContext(), "Algo pasó al guardar el evento", Toast.LENGTH_SHORT).show();
                                                            });

                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                    Log.e("msg-test", "Error en el bloque try-catch", e);
                                                }
                                            });
                                        }).addOnProgressListener(snapshot -> {
                                            long bytesTransferred = snapshot.getBytesTransferred();
                                            long totalByteCount = snapshot.getTotalByteCount();
                                            double porcentajeSubida = Math.round(bytesTransferred * 1.0f / totalByteCount * 100);
                                            TextView textoSubida = rootView.findViewById(R.id.subiendo);
                                            textoSubida.setText(porcentajeSubida + "%");
                                        });
                                    }
                                });

                                botonimage.setOnClickListener(view -> {
                                    pickMedia.launch(new PickVisualMediaRequest.Builder()
                                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                            .build());
                                });
                            }






                        } else {
                            // Manejar el error en caso de falla en la consulta
                            Log.e("msg-test", "Error al obtener documentos: " + task.getException());
                        }
                    });



        });



        return rootView;


    }


    private String generateRandomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomCode = new StringBuilder();
        int length = 6; // Puedes ajustar la longitud del código como desees

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            randomCode.append(characters.charAt(index));
        }

        return randomCode.toString();
    }


    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), result -> {
                if (result != null) {
                    uri = result;
                    // Cargar la imagen seleccionada en un ImageView (opcional)
                    ImageView imageView = rootView.findViewById(R.id.imageView);
                    Glide.with(requireContext()).load(uri).into(imageView);
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });


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
                                    TextInputEditText fechaEditText = requireView().findViewById(R.id.fecha);
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
}