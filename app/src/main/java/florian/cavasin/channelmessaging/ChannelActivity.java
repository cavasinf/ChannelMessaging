package florian.cavasin.channelmessaging;

        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Matrix;
        import android.media.ExifInterface;
        import android.net.Uri;
        import android.os.Environment;
        import android.os.Handler;
        import android.provider.MediaStore;
        import android.support.design.widget.FloatingActionButton;
        import android.support.v4.content.FileProvider;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.Toast;

        import com.google.gson.Gson;

        import org.apache.http.NameValuePair;
        import org.apache.http.message.BasicNameValuePair;

        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Timer;
        import java.util.TimerTask;

public class ChannelActivity extends AppCompatActivity {
    private ListView messages;
    private EditText myMessage;
    private FloatingActionButton btnPhoto;
    private FloatingActionButton btnSend;
    private List<Message> oldMessages;
    private Integer PICTURE_REQUEST_CODE = 1;
    static HashMap<String,Bitmap> images = new HashMap<String,Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        messages = (ListView) findViewById(R.id.listViewMessages);
        myMessage = (EditText) findViewById(R.id.editTextMessage);
        btnSend = (FloatingActionButton) findViewById(R.id.buttonEnvoyer);
        btnPhoto = (FloatingActionButton) findViewById(R.id.buttonPhoto);
        oldMessages = null;

        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                String accesstoken = settings.getString("accesstoken","");

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("accesstoken",accesstoken);
                params.put("channelid", getIntent().getStringExtra("channelid"));
                Connexion connexion = new Connexion(getApplicationContext(), params, "http://www.raphaelbischof.fr/messaging/?function=getmessages");
                connexion.execute();

                connexion.setOnDownloadCompleteListener(new  OnDownloadCompleteListener() {
                    @Override
                    public void onDownloadCompleted(String result) {
                        //déserialisation
                        Gson gson = new Gson();
                        MessageContainer obj = gson.fromJson(result, MessageContainer.class);
                        Collections.reverse(obj.getMessages());

                        //Teste s'il y a un nouveau message avant de refresh
                        if(!obj.getMessages().equals(oldMessages)){
                            oldMessages = obj.getMessages();
                            messages.setAdapter((new MessageListAdapter(getApplicationContext(), obj.getMessages())));
                        }
                    }
                });

                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(r, 1000);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                String accesstoken = settings.getString("accesstoken","");

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("accesstoken",accesstoken);
                params.put("channelid",getIntent().getStringExtra("channelid"));
                params.put("message",myMessage.getText().toString());

                myMessage.setText("");
                Connexion connexion = new Connexion(getApplicationContext(), params, " http://www.raphaelbischof.fr/messaging/?function=sendmessage");

                connexion.setOnDownloadCompleteListener(new  OnDownloadCompleteListener() {
                    @Override
                    public void onDownloadCompleted(String result) {
                        //déserialisation
                        Gson gson = new Gson();
                        Reponse obj = gson.fromJson(result, Reponse.class);

                        if(!obj.getResponse().equals("Message envoyé au channel")){
                            Toast.makeText(getApplicationContext(), "Erreur lors de l'envoi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                connexion.execute();

            }
        });

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                File picture =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/test/");
                picture.mkdir();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Création de l’appel à l’application appareil photo pour récupérer une image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picture)); //Emplacement de l’image stockée
                startActivityForResult(intent, PICTURE_REQUEST_CODE);*/
                File test = new File(Environment.getExternalStorageDirectory()+"/img.jpg");
                try {
                    test.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Android a depuis Android Nougat besoin d'un provider pour donner l'accès à un répertoire pour une autre app, cf : http://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
                Uri uri = FileProvider.getUriForFile(ChannelActivity.this, ChannelActivity.this.getApplicationContext().getPackageName() + ".provider", test);;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Création de l’appel à l’application appareil photo pour récupérer une image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri); //Emplacement de l’image stockée
                startActivityForResult(intent, PICTURE_REQUEST_CODE);
            }
        });

        messages.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Message message = (Message) messages.getItemAtPosition(position);

                SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                String username = settings.getString("username","");

                if(!message.getUsername().equals(username)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChannelActivity.this);
                    builder.setTitle("Ajouter un ami");
                    builder.setMessage("Voulez-vous vraiment ajouter cet utilisateur à votre liste d'amis ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if(isExternalStorageWritable())
                                        System.out.println("SD OK");
                                    UserDatasource data = new UserDatasource(getApplicationContext());
                                    data.open();
                                    data.createFriend(Integer.toString(message.getUserID()),message.getUsername(),message.getImageUrl());
                                    data.close();
                                }
                            })
                            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    builder.show();
                }
            }
        });
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            File test = new File(Environment.getExternalStorageDirectory()+"/img.jpg");

            try {
                this.resizeFile(test,ChannelActivity.this.getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }

            SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
            String accesstoken = settings.getString("accesstoken","");

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("accesstoken",accesstoken));
            params.add(new BasicNameValuePair("channelid",getIntent().getStringExtra("channelid")));


            UploadFileToServer upload = new UploadFileToServer(ChannelActivity.this, test.getAbsolutePath(), params, new UploadFileToServer.OnUploadFileListener() {
                @Override
                public void onResponse(String result) {
                    System.out.println("onResponse : " + result);
                }

                @Override
                public void onFailed(IOException error) {
                    System.out.println("onFailed : " + error);
                }
            });

            upload.execute();
        }
    }


    //decodes image and scales it to reduce memory consumption
    private void resizeFile(File f,Context context) throws IOException {
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

        //The new size we want to scale to
        final int REQUIRED_SIZE=400;

        //Find the correct scale value. It should be the power of 2.
        int scale=1;
        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
            scale*=2;

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=scale;
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        int i = getCameraPhotoOrientation(context, Uri.fromFile(f),f.getAbsolutePath());
        if (o.outWidth>o.outHeight)
        {
            Matrix matrix = new Matrix();
            matrix.postRotate(i); // anti-clockwise by 90 degrees
            bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap .getWidth(), bitmap .getHeight(), matrix, true);
        }
        try {
            f.delete();
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) throws IOException {
        int rotate = 0;
        context.getContentResolver().notifyChange(imageUri, null);
        File imageFile = new File(imagePath);
        ExifInterface exif = new ExifInterface(
                imageFile.getAbsolutePath());
        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        return rotate;
    }
}