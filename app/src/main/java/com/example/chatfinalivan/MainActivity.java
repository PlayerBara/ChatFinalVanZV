package com.example.chatfinalivan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatfinalivan.modelos.DataPackage;
import com.example.chatfinalivan.modelos.Usuario;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

//En esta clase se encendera el servidor para poder recibir mensajes, se abrira una ventana nueva para indicar una ip
// a la cual enviar mensajes a otro servidor
public class MainActivity extends AppCompatActivity {

    TextView txtIp, txtUser, txtMsg, txtMyIp;
    Button bSend; //192.168.14.120

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

        preparedServer();


        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ip = txtIp.getText().toString();
                user = txtUser.getText().toString();
                if(ip == null || user == null){
                    Toast.makeText(MainActivity.this, "Debe introducir una ip y un nombre de usuario antes de poder enviar un mensaje", Toast.LENGTH_SHORT).show();
                }else{
                    dataPackageOut = new DataPackage(user, txtMsg.getText().toString());

                    sendMsg();
                }
            }
        });

    }

    private void preparedServer(){
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        myIp = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        txtMyIp.setText(myIp);
        port = 3333;

        openServer();
    }

    private void openServer(){

        serverIsOpen = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    ServerSocket ss = new ServerSocket(port);

                    while(serverIsOpen){
                        Socket s = ss.accept();
                        Log.e("Server", "Server creado");
                        ObjectInputStream msgIn = new ObjectInputStream(s.getInputStream());

                        Log.e("Server", "Buzon activo");
                        dataPackageIn = (DataPackage) msgIn.readObject();

                        Log.e("Server", "Mensaje recibido");
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

    private void sendMsg(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Socket s = new Socket(ip, port);

                    ObjectOutputStream msgOut = new ObjectOutputStream(s.getOutputStream());

                    msgOut.writeObject(dataPackageOut);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addMsg(new DataPackage("Tu", dataPackageOut.getMsg()));
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

    private void addMsg(DataPackage dataPackage){
        listDataPackage.add(dataPackage);

        recAdapter = new RecyclerChat(listDataPackage);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        contenedor.setAdapter(recAdapter);
        contenedor.setLayoutManager(layoutManager);
    }
}