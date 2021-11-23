package it.ltm.scp.module.android.model.devices.pos.tsn;

/**
 * Created by HW64 on 24/02/2017.
 *
 * Rappresentazione dei dati di una tessera sanitaria esposta ai JS
 */

public class TsnDTO {
    private String cf;
    private String nome;
    private String cognome;

    public String getCf() {
        return cf;
    }

    public void setCf(String cf) {
        this.cf = cf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
}
