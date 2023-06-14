package com.example.chatfinalivan.modelos;

public class Usuario {
    private String nomUser;
    private int codUser;

    public Usuario(String nomUser) {
        this.nomUser = nomUser;
        this.codUser = (int)(Math.random()*(999 - 1 + 1) + 1);
    }

    public String getNomUser() {
        return nomUser;
    }

    public void setNomUser(String nomUser) {
        this.nomUser = nomUser;
    }

    public int getCodUser() {
        return codUser;
    }

    public void setCodUser(int codUser) {
        this.codUser = codUser;
    }

    public String getCompleteUser(){
        return nomUser + codUser;
    }
}
