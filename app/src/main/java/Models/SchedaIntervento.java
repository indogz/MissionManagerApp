package Models;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Tools.AESHelper;

/**
 * Created by matteo on 28/05/17.
 */

public class SchedaIntervento {

    private String nome = " ";
    private String cognome = " ";
    private String descrizione = " ";
    private String codice = " ";
    private String indirizzo = " ";
    private String tipologia = " ";
    private String id_destinatario = " ";
    private String dataOra = " ";
    private String aes_key = " ";
    private String primo = " ";
    private String comune = " ";
    private String civico = " ";
    private String via = " ";


    public SchedaIntervento(String nome, String cognome, String descrizione, String codice, String indirizzo) {
        this.nome = nome;
        this.cognome = cognome;
        this.descrizione = descrizione;
        this.codice = codice;
        this.indirizzo = indirizzo;
        AESHelper aesHelper = new AESHelper();
    }


    public String getComune() {
        return comune;
    }

    public String getCivico() {
        return civico;
    }

    public String getVia() {
        return via;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public void setCivico(String civico) {
        this.civico = civico;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public SchedaIntervento() {

        AESHelper aesHelper = new AESHelper();
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    public void setId_destinatario(String id_destinatario) {
        this.id_destinatario = id_destinatario;
    }

    public void setDataOra(String dataOra) {
        this.dataOra = dataOra;
    }

    public void setAes_key(String aes_key) {
        this.aes_key = aes_key;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getCodice() {
        return codice;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public String getTipologia() {
        return tipologia;
    }

    public String getId_destinatario() {
        return id_destinatario;
    }

    public String getDataOra() {
        return dataOra;
    }

    public String getAes_key() {
        return aes_key;
    }

    public String getPrimo() {
        return primo;
    }

    public void setPrimo(String primo) {
        this.primo = primo;
    }

    public void encryptAll(AESHelper aesHelper) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        String key = this.getAes_key();
        this.setNome(aesHelper.encrypt(this.getNome(), key));
        this.setIndirizzo(aesHelper.encrypt(this.getIndirizzo(), key));

    }


    public void decryptAll(AESHelper aesHelper) throws GeneralSecurityException, IOException {
        String key = this.getAes_key();
        this.setNome(aesHelper.decrypt(this.getNome(), key));
    }

    public void decryptAll(String key) throws GeneralSecurityException, IOException {
        AESHelper aesHelper = new AESHelper();
        this.setNome(aesHelper.decrypt(this.getNome(), key));
    }


}
