package it.ltm.scp.module.android.model.sm.gson;

public class VirtualAuthRequest {

    private String username;
    private String password;
    private String usercodeCartaFisica;
    private String tokenCartaFisica;
    private String modulo;
    private String esponente;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsercodeCartaFisica() {
        return usercodeCartaFisica;
    }

    public void setUsercodeCartaFisica(String usercodeCartaFisica) {
        this.usercodeCartaFisica = usercodeCartaFisica;
    }

    public String getTokenCartaFisica() {
        return tokenCartaFisica;
    }

    public void setTokenCartaFisica(String tokenCartaFisica) {
        this.tokenCartaFisica = tokenCartaFisica;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getEsponente() {
        return esponente;
    }

    public void setEsponente(String esponente) {
        this.esponente = esponente;
    }
}
