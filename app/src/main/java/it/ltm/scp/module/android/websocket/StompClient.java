package it.ltm.scp.module.android.websocket;


import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Created by panstudio on 14/09/16.
 */
public class StompClient {

    private String url;
    private String protocol;
    private WebSocket webSocket;
    private StompFrame frame;
    private URI uri;
    private StompClientManager manager;


    public StompClient(String url, String protocol, StompClientManager manager) {
        this.url = url;
        this.protocol = protocol;
        this.manager = manager;
    }


    public boolean isConnected() { return webSocket.getSocket().isConnected();}

    public void buildFrame(HashMap<String, String> headers, String command, String content) {
        this.frame.buildFrame( headers,  command,  content);
    }

    public void buildFrame( String command, String content) {
        this.frame.buildFrame(command,  content);
    }

    public void buildFrame(String command) {
        this.frame.buildFrame(command);
    }

    public void sendFrame(){
        webSocket.sendText(frame.getFrameToSend());
    }



    public StompFrame getFrame() {
        return this.frame;
    }


    public void close() {
        StompFrame fr = new StompFrame();
        fr.setDisconnectCommand();
        webSocket.sendText(fr.getFrameToSend());
        webSocket.sendClose();

    }

    public void connectWebSocket() throws IOException, WebSocketException, URISyntaxException {
        WebSocketFactory factory = new WebSocketFactory();
        frame = new StompFrame();
        uri = new URI(url);
        webSocket = factory.createSocket(uri);
        webSocket.addProtocol(protocol);
        webSocket.setUserInfo(protocol);
        webSocket.addListener(manager);
        webSocket.connect();

    }

    public void disconnect(){
        webSocket.disconnect();
    }

}
