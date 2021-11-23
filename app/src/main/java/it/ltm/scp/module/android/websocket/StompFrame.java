package it.ltm.scp.module.android.websocket;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by panstudio on 14/09/16.
 */
public class StompFrame {



    public final static String CMD_CONNECT = "CONNECT";
    public final static String CMD_DISCONNECT = "DISCONNECT";
    public final static String CMD_MESSAGE = "MESSAGE";
    public final static String CMD_ERROR = "ERROR";
    public final static String CMD_SUBSCRIBE = "SUBSCRIBE";
    public final static String CMD_UNSUBSCRIBE = "UNSUBSCRIBE";
    public final static String CMD_SEND = "SEND";
    public final static String CMD_LIST = "LIST";
    public final static String CMD_CREATE = "CREATE";
    public final static String CMD_DELETE = "DELETE";
    private final static String KEY_HEADERS = "headers";
    private final static String KEY_COMMAND = "command";
    private final static String KEY_CONTENT = "content";
    private final static String KEY_MESSAGE = "message";
    private HashMap<String, Object> frame;


    public StompFrame(){
        this.frame = new HashMap<>();
        this.frame.put(KEY_HEADERS, new HashMap<String, String>());
        this.frame.put(KEY_COMMAND, "");
        this.frame.put(KEY_CONTENT, "");

    }

    public void buildFrame( String command, String content) {
        this.frame = new HashMap<>();
        this.frame.put(KEY_HEADERS, new HashMap<String, String>());
        this.frame.put(KEY_COMMAND, command);
        this.frame.put(KEY_CONTENT, content);
    }

    public void buildFrame( String command) {
        this.frame = new HashMap<>();
        this.frame.put(KEY_HEADERS, new HashMap<String, String>());
        this.frame.put(KEY_COMMAND, command);
        this.frame.put(KEY_CONTENT, "");
    }

    public void buildFrame(HashMap<String, String> header, String command, String content) {
        this.frame = new HashMap<>();
        this.frame.put(KEY_HEADERS, header);
        this.frame.put(KEY_COMMAND, command);
        this.frame.put(KEY_CONTENT, content);
    }

    public void buildFrame(HashMap<String, String> header, String command) {
        this.frame = new HashMap<>();
        this.frame.put(KEY_HEADERS, header);
        this.frame.put(KEY_COMMAND, command);
        this.frame.put(KEY_CONTENT, "");
    }




    public void createNewHeader() {

    }

    public void addElementToFrameHeader(String key, String value) {
        HashMap<String, String> h = (HashMap<String, String>)this.frame.get(KEY_HEADERS);
        h.put(key, value);
        this.frame.put(KEY_HEADERS, h);
    }

    public void setFrameHeader(HashMap<String, String> header) {
        this.frame.put(KEY_HEADERS, header);


    }

    public void setFrameCommand(String command) {

        this.frame.put(KEY_COMMAND, command);


    }

    public void setFrameContent(String content) {
        this.frame.put(KEY_CONTENT, content);

    }



    public void setConnectCommand(){
        this.frame.put(KEY_COMMAND, CMD_CONNECT);
    }

    public void setDisconnectCommand(){
        this.frame.put(KEY_COMMAND, CMD_DISCONNECT);
    }

    public void setMessageCommand(){
        this.frame.put(KEY_COMMAND, CMD_MESSAGE);
    }

    public void setErrorCommand(){
        this.frame.put(KEY_COMMAND, CMD_ERROR);
    }

    public void setSubscribeCommand(){
        this.frame.put(KEY_COMMAND, CMD_SUBSCRIBE);
    }

    public void setUnsubscribeCommand(){
        this.frame.put(KEY_COMMAND, CMD_UNSUBSCRIBE);
    }

    public void setListCommand(){
        this.frame.put(KEY_COMMAND, CMD_LIST);
    }

    public void setSendCommand(){
        this.frame.put(KEY_COMMAND, CMD_SEND);
    }

    public void setCreateCommand(){
        this.frame.put(KEY_COMMAND, CMD_CREATE);
    }

    public void setDeleteCommand(){
        this.frame.put(KEY_COMMAND, CMD_DELETE);
    }






    public HashMap<String, String> getFrameHeader( ) {
        return (HashMap<String, String>)this.frame.get(KEY_HEADERS);
    }

    public String getFrameContent( ) {
        return (String)this.frame.get(KEY_CONTENT);
    }

    public String getFrameCommand( ) {
        return (String)this.frame.get(KEY_COMMAND);
    }

    public String getFrameMessage( ) {
        if(this.frame.get(KEY_MESSAGE) != null)
            return (String)this.frame.get(KEY_MESSAGE);
        else
            return "NO MESSAGE";
    }

    public void setFrameMessage(String message) {
        this.frame.put(KEY_MESSAGE, message);
    }


    public String getFrameToSend() {
        String data = frame.get(KEY_COMMAND)+"\n";
        String header_content = "";
        if((HashMap<String, String>)frame.get(KEY_HEADERS) != null) {
            HashMap<String, String> header = (HashMap<String, String>) frame.get(KEY_HEADERS);
            for (String key : header.keySet()) {

                header_content += key
                        + ": "
                        + (String) header.get(key)
                        + "\n";

            }
        }
        data += header_content;
        data += "\n\n";
        data += frame.get(KEY_CONTENT);
        data += "\n\0";
        return data;
    }


    public String parseframeToJson(String data) {
        StompFrame f = processFrame(data);
        String result = "{";
        HashMap<String, String> headers = f.getFrameHeader();
        for(String key : headers.keySet())
            result = result+" \""+key+"\" : \""+headers.get(key)+"\"";

        return result + "}";
    }

    public StompFrame processFrame(String data) {
        String [] lines = data.split("\n");
        //var frame = {};
        //frame['headers'] = {};
        if (lines.length > 1) {

            this.setFrameCommand(lines[0]);
            int x = 0;
            while(lines[x].length() > 0) {
                try {
                    String[] header_split = lines[x].split(":");
                    String key = header_split[0].trim();
                    String val = header_split[1].trim();
                    this.getFrameHeader().put(key, val);
                    x++;
                } catch (Exception e){
                    x++;
                }

            }
            lines = Arrays.copyOfRange(lines, x+1, lines.length-x);
            String f_content = lines+"\n";
            //String content = this.getFrameContent();

            this.setFrameContent(f_content);
        }


        return this;

    }


    public void sendError(String message, String detail) {
        this.setFrameHeader(null);
            if(message != null)
              this.setFrameMessage(message);
            else
               this.setFrameMessage("No error message given");
               this.setFrameCommand("ERROR");
               this.setFrameContent(detail);

    }

}
