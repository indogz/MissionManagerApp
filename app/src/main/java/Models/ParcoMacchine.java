package Models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by matteo on 17/05/17.
 * La classe ParcoMacchine contiene tutte le macchine disponibili nel database e controlla se
 * la stringa inserita corrisponde ad una di quelle effettivamente disponibli.
 */

public class ParcoMacchine {

    protected List<String> macchineDisponibili;
    private DatabaseReference databaseReference;

    public ParcoMacchine() {

        macchineDisponibili = new ArrayList<String>();

        /**
         * Controllo quali macchine sono disponibili sul database
         */
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Macchine");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    macchineDisponibili.add(postSnapshot.getKey().toString().trim());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.getMessage();

            }
        });
    }


    public List<String> getMacchineDisponibili() {
        return macchineDisponibili;
    }

    /**
     * Controlla se la stringa inserita fa parte delle macchine disponibili
     * @param s
     * @return exist
     */
    public boolean checkIfExist(String s) {
        boolean exist = false;
        for (String m : macchineDisponibili) {
            if (m.equals(s)) {
                exist = true;
            }
        }
        return exist;
    }

}
