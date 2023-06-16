package com.example.chatfinalivan.modelos;

import java.io.Serializable;

//Implementamos el serializable para que podamos enviar los datos como objetos
public class DataPackage implements Serializable {
    private String usuario;
    String msg;

    public DataPackage(String usuario, String msg) {
        this.usuario = usuario;
        this.msg = msg;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
