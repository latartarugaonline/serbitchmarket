package it.ltm.scp.module.android.model;

import com.google.gson.Gson;

/**
 * Created by HW64 on 06/09/2016.
 *
 * Classe globale per la gestione della communicazione con i javascript/internamente all'app.
 * Viene usata come ritorno da un metodo invocato, sotto forma di stringa Json.
 */
public class Result {

    private int code;
    private String description;
    private String exceptionMessage;
    private Object data;

    public Result(int code){
        this.code = code;
    }

    public Result(int code, Object data){
        this(code);
        this.data = data;
    }

    public Result(int code, String description, String exceptionMessage){
        this(code);
        this.description = description;
        this.exceptionMessage = exceptionMessage;
    }

    /**
     * @param code codice di errore
     * @param description messaggio descrittivo dell'errore
     * @param exceptionMessage
     * @param data eventuale oggetto di ritorno
     */
    public Result(int code, String description, String exceptionMessage, Object data) {
        this(code, description, exceptionMessage);
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Object getData() {
        return data;
    }

    public String toJsonString(){
        return new Gson().toJson(this);
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void clearExceptionLog(){
        this.exceptionMessage = null;
    }
}
