package agrocolor.listaverificacion.presentacion;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class EvidenciasFragment extends Fragment implements View.OnClickListener {

    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, newBtn, saveBtn, eraseBt;
    private View rootView;

    //variables guardar
    String archivo;
    File file;

    //medidas del pincel
    private float smallBrush, mediumBrush, largeBrush;

    //variables de tomar foto
    private Button btn;
    private ImageView img;
    private Intent I;
    private final static int cons = 0;
    private Bitmap bmp;

    //contexto de la actividad
    private MainActivity contexto;


    public EvidenciasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //referencia al contendedor
        rootView = (LinearLayout) inflater.inflate(R.layout.fragment_evidencias, container, false);


        drawView = (DrawingView) rootView.findViewById(R.id.drawing);

        LinearLayout paintLayout = (LinearLayout) rootView.findViewById(R.id.paint_colors2);

        //pinceles
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        //referencia al boton dibujar
        drawBtn = (ImageButton) rootView.findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener((View.OnClickListener) this);

        //referencia al boton nuevo
        newBtn = (ImageButton) rootView.findViewById(R.id.new_btn);
        newBtn.setOnClickListener((View.OnClickListener) this);

        //referencia al boton guardar
        saveBtn = (ImageButton) rootView.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener((View.OnClickListener) this);

        //referencia al bonto limpiar
        eraseBt = (ImageButton) rootView.findViewById(R.id.erase_btn);
        eraseBt.setOnClickListener((View.OnClickListener) this);

        //escogemos el valor
        currPaint = (ImageButton) paintLayout.getChildAt(5);

        //y cambiamos el boton de creacion con
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        //cargamos camara
        cargarcamara();

        // Inflate the layout for this fragment
        return rootView;
    }

    //metodo paint clicke
    public void paintClicked(View view) {

        if (view != currPaint) {

            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();

            drawView.setColor(color);
            //e implementamos para que el boton vuelva a la normalidad
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton) view;
        }

    }


    //on click  para dibujar
    public void onClick(View v) {
        //modificado a v
        if (v.getId() == R.id.draw_btn) {
            //para elegir size de pincel
            final Dialog brushDialog = new Dialog(contexto);
            brushDialog.setTitle("Tamaño Brocha:");
            //creacion del menu de pincel
            brushDialog.setContentView(R.layout.brush_chooser);
            //completamos el dialogo con
            brushDialog.show();

            //on click del menu de pincel
            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });


        } else if (v.getId() == R.id.new_btn) {

            /*
            //dialogo de nuevo botn para dibujar
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("Nuevo Dibujo");
            newDialog.setMessage("¿Empezamos de Nuevo?");
            newDialog.setPositiveButton("Si", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("No", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
            */
        } else if (v.getId() == R.id.save_btn) {
            //guardar el dibujo
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(contexto);
            saveDialog.setTitle("Guardar Dibujo");
            saveDialog.setMessage("Guardar dibujo");
            saveDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    //nombre foto
                    NombreFoto();

                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            saveDialog.show();

        } else if (v.getId() == R.id.erase_btn) {
            Toast.makeText(contexto, "Vamos a Encalar", Toast.LENGTH_SHORT).show();
            //limpiamos la vista
            drawView.startNew();

        }
    }

    //onactivytyresult para la captura de la imagen
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            Bundle ext = data.getExtras();
            bmp = (Bitmap) ext.get("data");
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
            drawView.setBackgroundDrawable(bitmapDrawable);

        }

    }

    private void cargarcamara() {
        //implementamos el boto tomar foto
        I = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(I, cons);

    }

    public void NombreFoto() {

        String nombre;
        //alerta de dialogo para guardar el nombre de archivo
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.layout_nuevo, null);
        //declarada final para evitar cambios
        final EditText et = (EditText) v.findViewById(R.id.et_nombre_archivo);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        ad
                .setTitle("Guardar Foto")
                .setView(v)
                // Add action buttons
                .setPositiveButton("Aceptar", null)
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // LoginDialogFragment.this.getDialog().cancel();
                            }
                        });
        //creacion del dialogo a partir del constructor y lo mostramos
        final AlertDialog dialog = ad.create();
        dialog.show();
        //si pulsamos en acepta
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String nombre = et.getText().toString().trim();
                        //guardarfoto(nombre);

                    }
                });

    }

    public void guardarfoto(String nombre) {


        //Guardamos el dibujo
        drawView.setDrawingCacheEnabled(true);

        //creamos una imagen temporal para guardar el archivo

       //String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), drawView.getDrawingCache(), nombre+"png", "dibujo");

        //file = new File (imgSaved);

        //definimos si se guarda o no la imagen
        if (file != null) {
            Toast.makeText(contexto.getApplicationContext(),
                    "Guardamos la imagen!", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(contexto.getApplicationContext(),
                    "La imagen no se ha guardado", Toast.LENGTH_SHORT).show();

        }
        //y limpiamos el cache para evitar reescribir
        drawView.destroyDrawingCache();

    }

    /**
     * Creamos el fichero temporal para almacenar la imagen
     * @return fichero temporal generado por si el usuario decide guardar la imagen
     * @throws IOException por si no tiene permisos de escritura y lectura en el dispositivo
     */
    private File createImageFile(String nombre) throws IOException {
        // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "AU_"+nombre;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM + "/Camera");
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }


    /**
     * Intent que lanza la camara, le pasamos el fichero tempooral que
     * hemos generado por si el usuario decide guardar la imagen
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
//          Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAMERA_IMAGE_REQUEST);
            }
        }
    }



    /**
     * Método obligatorio para que la imagen se registre en la base de datos
     * de mediaStore. Necesario para que otras galerías puedan saber de que
     * la imagen existe y poder visualizarla
     * @param path la ruta completa donde se almacena la imagen
     */
    public void scanFile(String path) {
        MediaScannerConnection.scanFile(this,
                new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i(TAG, "Scanned " + path + ":");
                        Log.i(TAG, "-> uri=" + uri);
                        new AsyncTask<Void,Void,Void>(){
                            @Override
                            protected void onPreExecute() {
                                sqlite = new sqlite(getApplicationContext(), "bdvision", 1);
                            }

                            @Override
                            protected Void doInBackground(Void... params) {
                                sqlite.sincrinizarBaseDeDatos();

                                sqlite.obtenerCarpetas(FragmentCarpetas.categorias);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                FragmentCarpetas.menuCategoriasRecyclerViewAdapter.notifyDataSetChanged();
                                sqlite.close();

                            }
                        }.execute();
                    }
                });
    }





}
