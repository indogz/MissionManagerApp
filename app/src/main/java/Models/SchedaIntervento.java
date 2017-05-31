package Models;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
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

    private String nome;
    private String cognome;
    private String descrizione;
    private String codice;
    private String indirizzo;
    private String tipologia;
    private String id_destinatario;
    private String key_firebase;
    private String aes_key;
    private String primo;


    public SchedaIntervento(String nome, String cognome, String descrizione, String codice, String indirizzo) {
        this.nome = nome;
        this.cognome = cognome;
        this.descrizione = descrizione;
        this.codice = codice;
        this.indirizzo = indirizzo;
        AESHelper aesHelper = new AESHelper();
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

    public void setKey_firebase(String key_firebase) {
        this.key_firebase = key_firebase;
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

    public String getKey_firebase() {
        return key_firebase;
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



    public void decryptAll(AESHelper aesHelper) {


    }


}
