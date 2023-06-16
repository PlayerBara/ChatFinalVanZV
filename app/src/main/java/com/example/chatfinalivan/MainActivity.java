package com.example.chatfinalivan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatfinalivan.modelos.DataPackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

//En esta clase se encendera el servidor para poder recibir mensajes, se abrira una ventana nueva para indicar una ip
// a la cual enviar mensajes a otro servidor
public class MainActivity extends AppCompatActivity {

    TextView txtIp, txtUser, txtMsg, txtMyIp;
    Button bSend;

    private DataPackage dataPackageIn;
    private DataPackage dataPackageOut;

    private ArrayList<DataPackage> listDataPackage = new ArrayList<>();
    RecyclerView contenedor;
    RecyclerChat recAdapter;

    private String myIp, ip, user;
    private int port;
    private boolean serverIsOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contenedor = (RecyclerView) findViewById(R.id.RecChat);
        bSend = (Button) findViewById(R.id.bSend);
        txtMsg = (TextView) findViewById(R.id.txtMsg);
        txtUser = (TextView) findViewById(R.id.txtUser);
        txtIp = (TextView) findViewById(R.id.txtIp);
        txtMyIp = (TextView) findViewById(R.id.txtMyIp);

        //Prepara el servidor para recibir mensajes
        preparedServer();

        //Al pulsar el boton enviar...
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Guarda en un auxiliar los daots del usuario y la ip
                ip = txtIp.getText().toString().trim();
                user = txtUser.getText().toString().trim();
                //Si estan vacios entonces...
                if(ip.equals("") || user.equals("")){
                    //... No envia nada y sale un mensaje de error
                    Toast.makeText(MainActivity.this, "Debe introducir una ip y un nombre de usuario antes de poder enviar un mensaje", Toast.LENGTH_SHORT).show();
                }else {
                    //Comprueba de que no este vacio el mensaje
                    if (!txtMsg.getText().toString().trim().equals("")){
                        //Crea el objeto que se va a enviar al otro servidor
                        dataPackageOut = new DataPackage(user, txtMsg.getText().toString());
                        //Lo envia
                        sendMsg();
                        //Vacia el cuadro de mensaje
                        txtMsg.setText("");

                    }else{
                        //Envia un mensaje de error al usuario
                        Toast.makeText(MainActivity.this, "Debe introducir un mensaje", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void preparedServer(){
        //Obtiene la ip del usuario para poder verla con mayor facilidad
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        myIp = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        //Añade la ip del usuario al testView para que sea visible
        txtMyIp.setText(myIp);
        port = 3333;

        //Abre el servidor
        openServer();
    }

    private void openServer(){
        //Auxiliar para saber si debe estar abierto el servidor
        serverIsOpen = true;

        //Abre un hilo nuevo
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //Crea el servidor
                    ServerSocket ss = new ServerSocket(port);

                    while(serverIsOpen){
                        //Crea el Socket del servidor
                        Socket s = ss.accept();
                        //Utilizaremos el ObjectInputStream para recibir los mensajes, que seran objetos
                        ObjectInputStream msgIn = new ObjectInputStream(s.getInputStream());

                        //Los obtenemos
                        dataPackageIn = (DataPackage) msgIn.readObject();

                        //Utilizamos el runOnUiThread para tocas cosas del diseño de la pantalla en el hilo
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Cosas que hara en el diseño como añadir el mensaje
                                addMsg(dataPackageIn);
                                Log.e("Server", "Mensaje colocado");
                            }
                        });

                        msgIn.close();

                        s.close();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //Este hilo hace que envie los datos al servidor
    private void sendMsg(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //Creamos el Socket y ponemos lo necesario para enviar los datos como objetos
                    Socket s = new Socket(ip, port);

                    ObjectOutputStream msgOut = new ObjectOutputStream(s.getOutputStream());

                    msgOut.writeObject(dataPackageOut);

                    //Añadimos los datos que hemos enviado a nuestro recycler view para que podamos ver los mensajes que hemos enviado
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addMsg(new DataPackage("Tú", dataPackageOut.getMsg()));
                        }
                    });

                    msgOut.close();

                    s.close();

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //Este metodo guarda todos los cambios del recycler view
    private void addMsg(DataPackage dataPackage){
        listDataPackage.add(dataPackage);

        recAdapter = new RecyclerChat(listDataPackage);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        contenedor.setAdapter(recAdapter);
        contenedor.setLayoutManager(layoutManager);
    }
}