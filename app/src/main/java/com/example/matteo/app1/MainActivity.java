package com.example.matteo.app1;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import Controller.InternetConnessionChecker;
import Controller.NfcConnectionChecker;
import Models.ParcoMacchine;
import Tools.AESHelper;

public class MainActivity extends AppCompatActivity {

    private Button mFirebaseBtn;
    private DatabaseReference mDatabase;
    private TextView mtextView;
    private TextView identificativoVeicolo;

    private TextView nome;
    private TextView strada;
    private TextView codice;
    private TextView descrizione;

    private EditText mNameFiled;
    private EditText mEmailField;

    private DatabaseReference ref;

    public String id;


    protected NfcAdapter nfcAdapter;
    protected PendingIntent pendingIntent;
    protected IntentFilter[] intentFilters;
    protected ToggleButton tglReadWrite;
    protected EditText txtTagContent;
    protected String tagContent;
    protected byte[] language;
    private ParcoMacchine parcoMacchine;
    private InternetConnessionChecker internetConnessionChecker;
    private NfcConnectionChecker nfcConnectionChecker;
    private Switch nfcLogin;

    public static String seedValue = "I AM UNBREAKABLE";
    public static String MESSAGE = "No one can read this message without decrypting me.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        nfcAdapter = nfcAdapter.getDefaultAdapter(this);
        txtTagContent = (EditText) findViewById(R.id.txtTagContent);
        nfcLogin = (Switch) findViewById(R.id.forceLogin);
        Boolean forceLogin = false;

        try {
            viewAntennaStatus(nfcAdapter);
        } catch (java.lang.NullPointerException ex) {
            System.out.println("eccezione");
        }


        id = "";
        txtTagContent.setText("");

        mFirebaseBtn = (Button) findViewById(R.id.firebase_btn);
        identificativoVeicolo = (TextView) findViewById(R.id.textView);
        nome = (TextView) findViewById(R.id.nome);
        strada = (TextView) findViewById(R.id.strada);
        codice = (TextView) findViewById(R.id.codice);
        descrizione = (TextView) findViewById(R.id.descrizione);
        //punta alla root
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Macchine");

        String a, b;

        AESHelper aesHelper = new AESHelper();
        try {
            a = aesHelper.encrypt("5", "chiave");
            System.out.println("Criptata: " + a);
            b = aesHelper.decrypt(a, "chiave");
            System.out.println("Dectittato: " + b);


        } catch (Exception e) {
            e.printStackTrace();
        }


        parcoMacchine = new ParcoMacchine();
        internetConnessionChecker = new InternetConnessionChecker(MainActivity.this);
        nfcConnectionChecker = new NfcConnectionChecker(MainActivity.this);

        System.out.println("Macchine disponibili: " + parcoMacchine.getMacchineDisponibili().toString().trim());

        System.out.println(internetConnessionChecker.isConnectionAvailable());
        if (!internetConnessionChecker.isConnectionAvailable()) {
            Toast.makeText(MainActivity.this, "INTERNET ERROR", Toast.LENGTH_SHORT).show();
        }
        mFirebaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nfcConnectionChecker.isConnectionAvailable() == false || !nfcLogin.isChecked()) {
                    getId();
                }

                System.out.println();
                if (parcoMacchine.checkIfExist(id) && internetConnessionChecker.isConnectionAvailable()) {

                    Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                    intent.putExtra("id", id);
                    finish();
                    startActivity(intent);
                } else {
                    //oast.makeText(MainActivity.this, "Macchina non autorizzata", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Check if nfc is set on and return a toast with the antenna status
     *
     * @param nfcAdapter
     */
    private void viewAntennaStatus(NfcAdapter nfcAdapter) throws java.lang.NullPointerException {
        try {
            if (nfcAdapter.isEnabled() != true) {
                Toast.makeText(this, "NFC not enabled", Toast.LENGTH_LONG).show();
            }
        } catch (java.lang.NullPointerException ex) {
            System.out.println("Eccezione");
        }
    }


    /**
     * The foreground dispatch system allows an activity to intercept an intent and claim priority
     * over other activities that handle the same intent
     * See more at: https://developer.android.com/guide/topics/connectivity/nfc/advanced-nfc.html
     */

    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        /**
         * Create a PendingIntent object so the Android system can populate it with the details of
         * the tag when it is scanned.
         */
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        /**
         * Declare intent filters to handle the intents that you want to intercept.
         */
        intentFilters = new IntentFilter[]{};

        if (nfcConnectionChecker.isConnectionAvailable()) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        } else {
            Toast.makeText(this, "NFC ERROR", Toast.LENGTH_SHORT).show();

        }
    }


    /**
     * Override the following activity lifecycle callbacks and add logic to enable and disable the
     * foreground dispatch when the activity loses (onPause()) and regains (onResume())
     */
    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();
    }


    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatchSystem();
    }

    private void disableForegroundDispatchSystem() {
        if (nfcConnectionChecker.isConnectionAvailable()) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(this, "NfcIntent detected", Toast.LENGTH_SHORT).show();

            //Check which mode is selected (read or write)
            //READ MODE
            /**
             *Extra containing an array of NdefMessage present on the discovered tag.
             *This extra is mandatory for ACTION_NDEF_DISCOVERED intents, and optional
             * for ACTION_TECH_DISCOVERED, and ACTION_TAG_DISCOVERED intents.
             * When this extra is present there will always be at least one NdefMessage element.
             * Most NDEF tags have only one NDEF message, but we use an array for future compatibility.
             * See more at: https://developer.android.com/guide/topics/connectivity/nfc/nfc.html
             * and: https://developer.android.com/reference/android/nfc/NfcAdapter.html#EXTRA_NDEF_MESSAGES
             * and: https://developer.android.com/guide/components/activities/parcelables-and-bundles.html
             */


            //https://developer.android.com/guide/components/activities/parcelables-and-bundles.html
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (parcelables != null && parcelables.length > 0) {
                readTextFromTag((NdefMessage) parcelables[0]);
            } else {
                Toast.makeText(this, "No NDEF messages found", Toast.LENGTH_SHORT).show();
            }
            /*else {

                //WRITE MODE

                /**
                 * IT'S REQUIRED!!!!!!!!!!!!!!!!!!!
                 *Mandatory extra containing the Tag that was discovered for the
                 * ACTION_NDEF_DISCOVERED, ACTION_TECH_DISCOVERED, and ACTION_TAG_DISCOVERED intents.
                 * Constant Value: "android.nfc.extra.TAG"
                 * See more at: https://developer.android.com/reference/android/nfc/NfcAdapter.html#EXTRA_TAG
                 */
              /*  Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                //Create a new NdefMessage
                NdefMessage ndefMessage = createNdefMessage(txtTagContent.getText() + "");
                //Write the tag previusly got with our NdefMessage
                writeNdefMessage(tag, ndefMessage);
            }*/
        }
    }


    /**
     * This method permits to create an  NFC writable NDEF Message for Android application
     *
     * @param s
     * @return ndefMessage
     */
    private NdefMessage createNdefMessage(String s) {
        //Create a new Ndef Record
        NdefRecord ndefRecord = createTextRecord(s);
        //Create the ndef message based on the last ndefRecord
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});
        return ndefMessage;
    }


    /**
     * Create a text record to write to the NFC tag
     * Inspired by: http://www.programcreek.com/java-api-examples/index.php?class=android.nfc.NdefRecord&method=createApplicationRecord
     *
     * @param s
     * @return
     */
    @Nullable
    private NdefRecord createTextRecord(String s) {
        try {
            //Take the  character enconding type
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = s.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            /**
             * Represents an immutable NDEF Record.
             * A logical NDEF Record always contains a 3-bit TNF (Type Name Field) that provides high
             * level typing for the rest of the record. The remaining fields are variable length and not always present:
             *  ->type: detailed typing for the payload
             *  ->id: identifier meta-data, not commonly used
             *  ->payload: the actual payload
             *
             *  See more at: https://developer.android.com/reference/android/nfc/NdefRecord.html
             *
             *  See other ways to create a NdefRecord here:
             *  https://developer.android.com/reference/android/nfc/NdefRecord.html#NdefRecord(short, byte[], byte[], byte[])
             */

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    //NOT USED IN THIS VERSION!!!!!!!!!!!!!!
    /**
     * NOT USED IN THIS VERSION!!!!!!
     * This method is the one which physically write the message into the tag.
     * @param tag
     * @param ndefMessage
     */
    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {
        try {
            if (tag == null) {
                Toast.makeText(this, "ERROR: tag obj cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }
            /**
             * Get an instance of Ndef for the given tag.
             * If returns null indicates the tag is not NDEF formatted, or
             * that this tag is NDEF formatted but under a vendor specification that this Android
             * device does not implement.
             * More about this notation here:
             * https://developer.android.com/reference/android/nfc/tech/Ndef.html#get(android.nfc.Tag)
             */
            Ndef ndef = Ndef.get(tag);

            if (ndef == null) {

                /**
                 * Format tag with the NDEF format standard
                 */
                formatTag(tag, ndefMessage);
            } else {

                /**
                 * Enable I/O operations to the tag from this TagTechnology object.
                 */
                ndef.connect();

                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag isn't writable", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }

                //THIS IS NOT RECURSIVE!!!!
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(this, "Tag written", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses NdefFormatable obj which provide  access to NDEF format operations on a Tag.
     * Unfortunately the procedures to convert unformated tags to NDEF formatted tags are not
     * specified by NFC Forum, and are not generally well-known.
     * So there is no mandatory set of tags for which all Android devices with NFC must support NdefFormatable.
     * See more at: https://developer.android.com/reference/android/nfc/tech/NdefFormatable.html
     *
     * @param tag
     * @param ndefMessage
     */
    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {
            //Acquire a NdefFormatable object using get(Tag).
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag is not NDEF compatible", Toast.LENGTH_LONG).show();
            }
            ndefFormatable.connect();

            /**
             * Format a tag as NDEF, and write a NdefMessage.
             * This is a multi-step process, an IOException is thrown if any one step fails.
             * The card is left in a read-write state after this operation.
             * See more at: https://developer.android.com/reference/android/nfc/tech/NdefFormatable.html#format(android.nfc.NdefMessage)
             */
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();
            Toast.makeText(this, "Tag formatted", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Clean the TextView (changed to EditText)     *
     * @param view
     */
    public void tglReadWriteOnClick(View view) {
        txtTagContent.setText("");
    }

    /**
     * This method permits to convert an NdefRecord to a text     *
     * @param ndefRecord
     * @return
     */
    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            /**
             * The first sector is not writable. Contains the format of the data
             * inspired by: http://stackoverflow.com/questions/30914423/android-encoding-issue
             *              http://stackoverflow.com/questions/29231463/how-to-send-bytes-by-nfc
             */

            //NOTICE: using & operator which make a bitwise operation
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return tagContent;
    }

    /**
     * This method is the one which physically write the message into the tag.
     *
     * @param ndefMessage
     */

    //THIS METHOD READ FROM THE TAG; HERE
    private void readTextFromTag(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            tagContent = getTextFromNdefRecord(ndefRecord);

            //txtTagContent.setText(tagContent);
            AESHelper aesHelper = new AESHelper();
            try {

                id = aesHelper.decrypt(tagContent, "chiave");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //5 txtTagContent.setText(id);
            for (int i = 0; i < tagContent.length(); i++) {
                txtTagContent.setText(txtTagContent.getText() + "*");
            }
            // id = tagContent;
            nome.setText("Mike India " + id);
        } else {
            Toast.makeText(this, "No NDEF reoords found", Toast.LENGTH_SHORT).show();
        }

    }

    public void getId() {
        id = txtTagContent.getText().toString().trim();
    }
}
