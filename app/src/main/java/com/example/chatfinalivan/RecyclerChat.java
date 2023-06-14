package com.example.chatfinalivan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatfinalivan.modelos.DataPackage;

import java.util.List;

public class RecyclerChat extends RecyclerView.Adapter<RecyclerChat.RecyclerHolder>{

    private List<DataPackage> mensajes;

    public RecyclerChat(List<DataPackage> listMsg){
        this.mensajes = listMsg;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_recycler,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);
        return recyclerHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        DataPackage msg = mensajes.get(position);

        String user = msg.getUsuario();
        String userMsg = msg.getMsg();

        holder.txtMsgRec.setText(userMsg);
        holder.txtUserRec.setText(user);
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }


    public class RecyclerHolder extends RecyclerView.ViewHolder {

        TextView txtUserRec;
        TextView txtMsgRec;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            //Se enlaza los elementos del layout
            txtUserRec = (TextView) itemView.findViewById(R.id.txtUserRec);
            txtMsgRec = (TextView) itemView.findViewById(R.id.txtMsgRec);
        }
    }
}

