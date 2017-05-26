package SocketIO;

import android.app.Application;






public class ChatApplication extends Application {
    public static final String CHAT_SERVER_URL = "https://missionmanager1.herokuapp.com/";


    public String getServerUrl() {
        return CHAT_SERVER_URL;
    }
}

