package it.ltm.scp.module.android.model;

public class CIE
{
    private String cf;
    private String nome;
    private String cognome;
    private String sesso;
    private String indirizzoResidenza;
    private String residenza;
    private String provincia;
    private String luogoDiNascita;
    private String provinciaDiNascita;
    private String giornoNascita="";
    private String meseNascita="";
    private String annoNascita="";
    private String cityCode="";
    private String countryCode="";
    private String residenceCityCode="";
    private String residenceCountryCode="";
    private String enteRilascio;
    private String dataEmissione;
    private String numDoc;
    private String cittadinanza;
    private String dataScadenza;
    private String comuneEmettitore="";


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

    public String getSesso() {
        return sesso;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    public String getIndirizzoResidenza() {
        return indirizzoResidenza;
    }

    public void setIndirizzoResidenza(String indirizzoResidenza) {
        this.indirizzoResidenza = indirizzoResidenza;
    }

    public String getResidenza() {
        return residenza;
    }

    public void setResidenza(String residenza) {
        this.residenza = residenza;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getLuogoDiNascita() {
        return luogoDiNascita;
    }

    public void setLuogoDiNascita(String luogoDiNascita) {
        this.luogoDiNascita = luogoDiNascita;
    }

    public String getProvinciaDiNascita() {
        return provinciaDiNascita;
    }

    public void setProvinciaDiNascita(String provinciaDiNascita) {
        this.provinciaDiNascita = provinciaDiNascita;
    }

    public String getGiornoNascita() {
        return giornoNascita;
    }

    public void setGiornoNascita(String giornoNascita) {
        this.giornoNascita = giornoNascita;
    }

    public String getMeseNascita() {
        return meseNascita;
    }

    public void setMeseNascita(String meseNascita) {
        this.meseNascita = meseNascita;
    }

    public String getAnnoNascita() {
        return annoNascita;
    }

    public void setAnnoNascita(String annoNascita) {
        this.annoNascita = annoNascita;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getResidenceCityCode() {
        return residenceCityCode;
    }

    public void setResidenceCityCode(String residenceCityCode) {
        this.residenceCityCode = residenceCityCode;
    }

    public String getResidenceCountryCode() {
        return residenceCountryCode;
    }

    public void setResidenceCountryCode(String residenceCountryCode) {
        this.residenceCountryCode = residenceCountryCode;
    }

    public String getEnteRilascio() {
        return enteRilascio;
    }

    public void setEnteRilascio(String enteRilascio) {
        this.enteRilascio = enteRilascio;
    }

    public String getDataEmissione() {
        return dataEmissione;
    }

    public void setDataEmissione(String dataEmissione) {
        this.dataEmissione = dataEmissione;
    }

    public String getNumDoc() {
        return numDoc;
    }

    public void setNumDoc(String numDoc) {
        this.numDoc = numDoc;
    }

    public String getCittadinanza() {
        return cittadinanza;
    }

    public void setCittadinanza(String cittadinanza) {
        this.cittadinanza = cittadinanza;
    }

    public String getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(String dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public String getComuneEmettitore() {
        return comuneEmettitore;
    }

    public void setComuneEmettitore(String comuneEmettitore) {
        this.comuneEmettitore = comuneEmettitore;
    }

    @Override
    public String toString() {
        return "CIE{" +
                "cf='" + cf + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", sesso='" + sesso + '\'' +
                ", indirizzoResidenza='" + indirizzoResidenza + '\'' +
                ", residenza='" + residenza + '\'' +
                ", provincia='" + provincia + '\'' +
                ", luogoDiNascita='" + luogoDiNascita + '\'' +
                ", provinciaDiNascita='" + provinciaDiNascita + '\'' +
                ", giornoNascita='" + giornoNascita + '\'' +
                ", meseNascita='" + meseNascita + '\'' +
                ", annoNascita='" + annoNascita + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", residenceCityCode='" + residenceCityCode + '\'' +
                ", residenceCountryCode='" + residenceCountryCode + '\'' +
                ", enteRilascio='" + enteRilascio + '\'' +
                ", dataEmissione='" + dataEmissione + '\'' +
                ", numDoc='" + numDoc + '\'' +
                ", cittadinanza='" + cittadinanza + '\'' +
                ", dataScadenza='" + dataScadenza + '\'' +
                '}';
    }
}
