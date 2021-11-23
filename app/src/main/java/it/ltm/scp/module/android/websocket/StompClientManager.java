package it.ltm.scp.module.android.websocket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.ltm.scp.module.android.devices.terminal.TerminalManagerFactory;
import it.ltm.scp.module.android.utils.Constants;
import it.ltm.scp.module.android.utils.Properties;

/**
 * Created by panstudio on 16/09/16.
 */
public class StompClientManager extends WebSocketAdapter {
    private String URL;
    private String PROTOCOL;
    private String message;

    private final String TAG = StompClientManager.class.getSimpleName();



    public interface StompClientManagerListener {
        void onMessage(String message);
        void onErrorReceived(String error);
        void onConnected();
        void onStateChanged(String newState);
        void onDisconnected();
        void onConnectedTimeOut(String error);
        void onConnectException(String error);

    }

    private StompClient client;
    private StompClientManagerListener listener;

    public StompClientManager() {
        URL = TerminalManagerFactory.get().getWsUrl();
        PROTOCOL = Properties.get(Constants.PROP_WS_PROTOCOL);
        this.client = new StompClient(URL, PROTOCOL, this);
    }


    public void setStompClientManagerListener(StompClientManagerListener listener) {
        this.listener = listener;
    }

    public StompClient getClient() {
        return this.client;
    }

    //operation

    public void connect(){
        try {
            this.client.connectWebSocket();
        } catch (IOException e) {
//            Log.e(TAG, "connect: ", e);
            listener.onConnectException(e.getMessage());
        } catch (WebSocketException e) {
            listener.onConnectException("WebSocketException");
//            Log.e(TAG, "connect: ", e);
        } catch (URISyntaxException e) {
            listener.onConnectException(e.getMessage());
//            Log.e(TAG, "connect: ", e);
        }
    }

    public void close() {
        this.client.close();
    }


    public void list(){

        client.buildFrame(StompFrame.CMD_LIST);
        client.sendFrame();
    }

    public void subscribeAll(){
        subCashStatus(StompFrame.CMD_SUBSCRIBE);
        subPrinterStatus(StompFrame.CMD_SUBSCRIBE);
        subPaymentStatus(StompFrame.CMD_SUBSCRIBE);
        subScan(StompFrame.CMD_SUBSCRIBE);
        subScanStatus(StompFrame.CMD_SUBSCRIBE);
        subPower(StompFrame.CMD_SUBSCRIBE);
        subAsync(StompFrame.CMD_SUBSCRIBE);
        subAsyncAuth(StompFrame.CMD_SUBSCRIBE);
        subAsyncPayment(StompFrame.CMD_SUBSCRIBE);
        subAsyncTsn(StompFrame.CMD_SUBSCRIBE);
        subUpdate(StompFrame.CMD_SUBSCRIBE);
        subAsyncPrompt(StompFrame.CMD_SUBSCRIBE);
        subScannerSnapshot(StompFrame.CMD_SUBSCRIBE);
        subScanUpdate(StompFrame.CMD_SUBSCRIBE);
    }

    public void unSubAll(){
        subCashStatus(StompFrame.CMD_UNSUBSCRIBE);
        subPrinterStatus(StompFrame.CMD_UNSUBSCRIBE);
        subPaymentStatus(StompFrame.CMD_UNSUBSCRIBE);
        subScan(StompFrame.CMD_UNSUBSCRIBE);
        subScanStatus(StompFrame.CMD_UNSUBSCRIBE);
        subPower(StompFrame.CMD_UNSUBSCRIBE);
        subAsync(StompFrame.CMD_UNSUBSCRIBE);
        subAsyncAuth(StompFrame.CMD_UNSUBSCRIBE);
        subAsyncPayment(StompFrame.CMD_UNSUBSCRIBE);
        subAsyncTsn(StompFrame.CMD_UNSUBSCRIBE);
        subUpdate(StompFrame.CMD_UNSUBSCRIBE);
        subAsyncPrompt(StompFrame.CMD_UNSUBSCRIBE);
        subScannerSnapshot(StompFrame.CMD_UNSUBSCRIBE);
        subScanUpdate(StompFrame.CMD_UNSUBSCRIBE);
    }

    public void subPaymentStatus(String command){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("destination",Properties.get(Constants.PROP_SUB_PAYMENT));
        client.getFrame().buildFrame(headers,command);
        client.sendFrame();
    }

    public void subCashStatus(String command){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("destination",Properties.get(Constants.PROP_SUB_CASHDRAWER));
        client.getFrame().buildFrame(headers,command);
        client.sendFrame();
    }

    public void subPrinterStatus(String command){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("destination",Properties.get(Constants.PROP_SUB_PRINTER));
        client.getFrame().buildFrame(headers,command);
        client.sendFrame();

    }

    public void subAsync(String command){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("destination",Properties.get(Constants.PROP_SUB_ASYNC));
        client.getFrame().buildFrame(headers,command);
        client.sendFrame();
    }

    public void subAsyncPayment(String command){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("destination",Properties.get(Constants.PROP_SUB_ASYNC_PAYMENT));
        client.getFrame().buildFrame(headers,command);
        client.sendFrame();
    }

    public void subAsyncAuth(String command){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("destination",Properties.get(Constants.PROP_SUB_ASYNC_AUTH));
        client.getFrame().buildFrame(headers,command);
        client.sendFrame();
    }

    public void subScan(String command){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("destination",Properties.get(Constants.PROP_SUB_SCANNER));
        client.getFrame().buildFrame(headers,command);
        client.sendFrame();
    }

    public void subScanStatus(String command){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("destination",Properties.get(Constants.PROP_SUB_SCANNER_STATUS));
        client.getFrame().buildFrame(headers,command);
        client.sendFrame();
    }

    public void subScanUpdate(String command){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("destination",Properties.get(Constants.PROP_SUB_SCANNER_UPDATE));
        client.getFrame().buildFrame(headers,command);
        client.sendFrame();
    }

    public void subPower(String command){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("destination",Properties.get(Constants.PROP_SUB_POWER));
        client.getFrame().buildFrame(headers,command);
        client.sendFrame();
    }

    public void subAsyncTsn(String command){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("destination",Properties.get(Constants.PROP_SUB_ASYNC_TSN));
        client.getFrame().buildFrame(headers,command);
        client.sendFrame();
    }

    public void subAsyncPrompt(String command){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("destination",Properties.get(Constants.PROP_SUB_PROMPT));
        client.getFrame().buildFrame(headers,command);
        client.sendFrame();
    }

    public void subUpdate(String command){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("destination",Properties.get(Constants.PROP_SUB_UPDATE));
        client.getFrame().buildFrame(headers,command);
        client.sendFrame();
    }

    public void subScannerSnapshot(String command){
        String path = Properties.get(Constants.PROP_SUB_SCANNER_SNAPSHOT);
        if(path == null)
            return;
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("destination",path);
        client.getFrame().buildFrame(headers,command);
        client.sendFrame();
    }

    public void disconnect(){
        client.disconnect();
    }

    @Override
    public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
        listener.onStateChanged(newState.toString());
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        StompFrame fr = new StompFrame();
        fr.setConnectCommand();
        websocket.sendText(fr.getFrameToSend());
        listener.onConnected();
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
        listener.onErrorReceived("Errore di connessione "+cause.toString());
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
        listener.onDisconnected();
    }



    @Override
    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        if(frame.getFin()==true) {
            listener.onMessage(frame.getPayloadText());
            message = null;
        }else{
            message=frame.getPayloadText();
        }
    }

    @Override
    public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        String sFrame=new String(frame.getPayload());
        if(frame.getFin()==true) {
            listener.onMessage(message+sFrame);
            message = null;
        }else{
            message=message+sFrame;
        }
    }

    @Override
    public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }


    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
        listener.onErrorReceived("Errore "+cause.getMessage());
    }

    @Override
    public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {

        listener.onErrorReceived("Errore sul frame: "+cause.getMessage());
    }

    @Override
    public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {
        listener.onErrorReceived("Errore sul messaggio "+cause.getMessage());
    }


    @Override
    public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
        listener.onErrorReceived("Errore sul messaggio di testo:"+cause.getMessage());
    }


    @Override
    public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
        listener.onErrorReceived("Errore inaspettato"+cause.getMessage());
    }

}
