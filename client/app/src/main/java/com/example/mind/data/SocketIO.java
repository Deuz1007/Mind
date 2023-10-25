package com.example.mind.data;

import com.example.mind.interfaces.Include;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIO {
    public static Socket instance;

    public static void createInstance() throws URISyntaxException {
//        instance = IO.socket("https://mind-api.onrender.com");
        instance = IO.socket("http://192.168.1.4:3000");
        instance.connect();
    }

    public static void onChatGPT(Include include) {
        instance.on("chatgpt", include::execute);
    }

    public static void onDataError(Include include) {
        instance.on("data_error", include::execute);
    }
}
