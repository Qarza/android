/*
 * Android-perusohjelmointikurssin pohja ristinollapelin tekemiseen.
 * Voit käyttää tätä koodia esimerkkinä ja refaktoroida sitä mielinmäärin omanlaiseksesi.
 * Kari Sainio, 2017-2021
 *
 * This is very basic example of Tic-Tac-Toe game with one activity for Android as a coding example.
 * You may further develop this for your own purposes.
 */
package com.myapp.ristinolla;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.style.DrawableMarginSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Time;
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends Activity {

    // Määritellään UI komponentit
    private TextView status;
    private Button[] btn = {null, null, null, null, null, null, null, null, null};
    private Button uusiPeliBtn;

    private View.OnClickListener kliksuttelija;
    private static int napinId;

    // Määritellään muita tarvittavia muuttujia
    final private int PELAAJA = 11;   //pelaajalle kovakoodattu risti
    final private int ANDROID = 22;   // androidille kovakoodattu nolla
    final private int TYHJA = -1;

    //
    private int[] matriisi = {TYHJA, TYHJA, TYHJA, TYHJA, TYHJA, TYHJA, TYHJA, TYHJA, TYHJA};
    private boolean peliStatus = false;
    private Random rnd;
    private int peliVuoro = 0;
    private int peliSiirto = 0;
    /*
     * Pakollinen Activityssä oltava järjestelmän callback-metodi, jolla Activity alustetaan
     * ja näytetään. Tämän on oltava aina onCreate(Bundle) niminen!!!!!
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Muista aina kutsua super-luokan vastaavaa metodia
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /*
         * Tämä on yleinen handleri, jolla tutkitaan kaikki napinpainallukset
         * Tämän toteutukseen on toinenkin vaihtoehto, jolloin activity toteuttaa sen omana metodina
         * ja metodi esitellään "alemmalla tasolla"
         *
         * Tämä on samalla peliluuppi, eli tämä ajetaan aina kun jotain nappia ruudulla painetaan!
         */
        kliksuttelija = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case (R.id.button1): napinId = 0;
                    break;
                    case (R.id.button2): napinId = 1;
                    break;
                    case (R.id.button3): napinId = 2;
                    break;
                    case (R.id.button4): napinId = 3;
                    break;
                    case (R.id.button5): napinId = 4;
                    break;
                    case (R.id.button6): napinId = 5;
                    break;
                    case (R.id.button7): napinId = 6;
                    break;
                    case (R.id.button8): napinId = 7;
                    break;
                    case (R.id.button9): napinId = 8;
                    break;
                    case (R.id.buttonStart): napinId = 10;
                       uusiPeli();

                }


                /* TODO tässä kohtaa voit esim. kutsua pelilogiikkaa, joka tutkii mitä nappia
                 * painettiin ja esim. muuttaa napin väriä ja kirjoittaa O tai X numeron tilalle
                 */
                boolean voittaja = false;
                if (peliVuoro == PELAAJA) {
                    pelilogiikkaPelaaja(napinId);
                    voittaja = tarkistaVoittiko(PELAAJA);
                    if (voittaja) peliLoppui(PELAAJA);
                }
                if (peliVuoro == ANDROID) {
                    pelilogiikkaAndroid();
                    voittaja = tarkistaVoittiko(ANDROID);
                    if (voittaja) peliLoppui(ANDROID);
                }
                if (peliSiirto >= 9 ) peliLoppui(0);

            }
        }; // Huomaa tähän päättyi napinkäsittelijä ja pelin luuppi!

        /* UI alustus */
        status = (TextView) findViewById(R.id.status);
        btn[0] = (Button) findViewById(R.id.button1);
        btn[1] = (Button) findViewById(R.id.button2);
        btn[2]=  (Button) findViewById(R.id.button3);
        btn[3] = (Button) findViewById(R.id.button4);
        btn[4] = (Button) findViewById(R.id.button5);
        btn[5] = (Button) findViewById(R.id.button6);
        btn[6] = (Button) findViewById(R.id.button7);
        btn[7] = (Button) findViewById(R.id.button8);
        btn[8] = (Button) findViewById(R.id.button9);
        uusiPeliBtn = (Button) findViewById(R.id.buttonStart);

        // Päivitä status uudelle pelillä
        status.setText("Aloita uusi peli");

        // Buttonien käsittelijä
        // https://developer.android.com/reference/android/widget/Button.html
        for (int i = 0; i < matriisi.length; i++) {
            btn[i].setOnClickListener(kliksuttelija);
        }
        uusiPeliBtn.setOnClickListener(kliksuttelija);

        // Alustukset ovat valmiit aloita pelit
        uusiPeli();
    }

/*
 * Alusta uusi peli ja pelilauta tyhjillä "nappuloilla"
 */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void uusiPeli() {
        for (int i = 0; i < matriisi.length; i++) {
            //btn[i].setBackgroundColor(Color.GRAY);
            //btn[i].setText(""+i);
            btn[i].setBackground(getDrawable(R.drawable.tyhja));
            matriisi[i] = TYHJA;
            rnd = new Random(Calendar.MILLISECOND);
            rnd.setSeed(Calendar.SECOND);
            peliVuoro = PELAAJA;
            peliSiirto = 0;
            peliStatus = true;
            status.setText("Uusi peli");
        }
    }

    /*
     *  Pelaajan siirrot
     */
    public void pelilogiikkaPelaaja(int id) {

        /*
         * Tarkistetaan pelaajan paina
         */
        if (id >=0 && id <=8 && matriisi[id] == TYHJA) {
            //btn[id].setBackgroundColor(Color.GREEN);
            //btn[id].setText("X");
            btn[id].setBackground(getDrawable(R.drawable.risti));
            matriisi[id] = PELAAJA;
            peliSiirto++;
            peliVuoro = ANDROID;
        }
        /* halutaanko uusi peli? alusta, jos halutaan */
        if (id == 10) uusiPeli();
        Log.d("Ristinolla ", "id " + id);


    }

    /*
     * Android-siirrot
     * Taktiikka: pyritään ensin saamaan keskiruutu jos sitä ei ole ja tämän jälkeen
     * katsotaan onko pelaajalla jo 2x samassa rivissä ja sitten vasta random.
     */

    public void pelilogiikkaAndroid() {

        // Katsotaan puolustus, eli onko vastustajalla jo kaksi rivissä
        // tutkitaan ylin vaakarivi
        if (matriisi[0] == PELAAJA && matriisi[1] == PELAAJA && matriisi[2] == TYHJA) {
           asetaAndroidMerkki(2);
           return;
        }
        if (matriisi[0] == PELAAJA && matriisi[1] == TYHJA && matriisi[2] == PELAAJA) {
            asetaAndroidMerkki(1);
            return;
        }
        if (matriisi[0] == TYHJA && matriisi[1] == PELAAJA && matriisi[2] == PELAAJA) {
            asetaAndroidMerkki(0);
            return;
        }

        // tutkitaan keskimmäinen vaakarivi
        if (matriisi[3] == PELAAJA && matriisi[4] == PELAAJA && matriisi[5] == TYHJA) {
            asetaAndroidMerkki(5);
            return;
        }
        if (matriisi[3] == PELAAJA && matriisi[4] == TYHJA && matriisi[5] == PELAAJA) {
            asetaAndroidMerkki(4);
            return;
        }
        if (matriisi[3] == TYHJA && matriisi[4] == PELAAJA && matriisi[5] == PELAAJA) {
            asetaAndroidMerkki(3);
            return;
        }

        // tutkitaan alin vaakarivi
        if (matriisi[6] == PELAAJA && matriisi[7] == PELAAJA && matriisi[8] == TYHJA) {
            asetaAndroidMerkki(8);
            return;
        }
        if (matriisi[6] == PELAAJA && matriisi[7] == TYHJA && matriisi[8] == PELAAJA) {
            asetaAndroidMerkki(7);
            return;
        }
        if (matriisi[6] == TYHJA && matriisi[7] == PELAAJA && matriisi[8] == PELAAJA) {
            asetaAndroidMerkki(6);
            return;
        }

        // tutkitaan vasen pystyrivi
        if (matriisi[0] == PELAAJA && matriisi[3] == PELAAJA && matriisi[6] == TYHJA) {
            asetaAndroidMerkki(6);
            return;
        }
        if (matriisi[0] == PELAAJA && matriisi[3] == TYHJA && matriisi[6] == PELAAJA) {
            asetaAndroidMerkki(3);
            return;
        }
        if (matriisi[0] == TYHJA && matriisi[3] == PELAAJA && matriisi[6] == PELAAJA) {
            asetaAndroidMerkki(0);
            return;
        }

        // tutkitaan keskimmäinen pystyrivi
        if (matriisi[1] == PELAAJA && matriisi[4] == PELAAJA && matriisi[7] == TYHJA) {
            asetaAndroidMerkki(7);
            return;
        }
        if (matriisi[1] == PELAAJA && matriisi[4] == TYHJA && matriisi[7] == PELAAJA) {
            asetaAndroidMerkki(4);
            return;
        }
        if (matriisi[1] == TYHJA && matriisi[4] == PELAAJA && matriisi[7] == PELAAJA) {
            asetaAndroidMerkki(1);
            return;
        }

        // tutkitaan oikea pystyrivi
        if (matriisi[2] == PELAAJA && matriisi[5] == PELAAJA && matriisi[8] == TYHJA) {
            asetaAndroidMerkki(8);
            return;
        }
        if (matriisi[2] == PELAAJA && matriisi[5] == TYHJA && matriisi[8] == PELAAJA) {
            asetaAndroidMerkki(5);
            return;
        }
        if (matriisi[2] == TYHJA && matriisi[5] == PELAAJA && matriisi[8] == PELAAJA) {
            asetaAndroidMerkki(2);
            return;
        }

        // tutkitaan viistorivit
        if (matriisi[0] == PELAAJA && matriisi[4] == PELAAJA && matriisi[8] == TYHJA) {
            asetaAndroidMerkki(8);
            return;
        }
        if (matriisi[0] == PELAAJA && matriisi[4] == TYHJA && matriisi[8] == PELAAJA) {
            asetaAndroidMerkki(4);
            return;
        }
        if (matriisi[0] == PELAAJA && matriisi[4] == TYHJA && matriisi[8] == PELAAJA) {
            asetaAndroidMerkki(0);
            return;
        }

        // tutkitaan viistorivit
        if (matriisi[2] == PELAAJA && matriisi[4] == PELAAJA && matriisi[6] == TYHJA) {
            asetaAndroidMerkki(6);
            return;
        }
        if (matriisi[2] == PELAAJA && matriisi[4] == TYHJA && matriisi[6] == PELAAJA) {
            asetaAndroidMerkki(4);
            return;
        }
        if (matriisi[2] == PELAAJA && matriisi[4] == TYHJA && matriisi[6] == PELAAJA) {
            asetaAndroidMerkki(2);
            return;
        }

        // Jos ei tarvitse puolustaa, niin merkitään random ruutu
        boolean tyhja = false;
        // Yritetään aina saada keskiruutu, jos se vain on vapaana
        int i = 4;

        while (!tyhja && peliSiirto < 9) {

            //Log.d("Ristinolla ", "rnd " + i);
            if (matriisi[i] == TYHJA) {
                asetaAndroidMerkki(i);
                tyhja = true;
            }
            i = rnd.nextInt(9);
        }
    }

    /*
     * Aseta Androidin "pelimerkki"
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void asetaAndroidMerkki(int i) {
        //btn[i].setBackgroundColor(Color.RED);
        //btn[i].setText("O");
        btn[i].setBackground(getDrawable(R.drawable.nolla));
        matriisi[i] = ANDROID;
        peliSiirto++;
        peliVuoro = PELAAJA;
    }

    /*
     * Pelin lopetus
     */
    public void peliLoppui(int voittaja) {
        status.setText("Peli päättyi!");
        if (voittaja == PELAAJA) {
            status.setText("Pelaaja voitti!");
        }
        if (voittaja == ANDROID) {
            status.setText("Android voitti!");
        }
        peliStatus = false;
    }


    // TODO mieti tarvitsetko muita activityn metodeja kuten onStart, onResume, onPause, jne


    // On hyvä tallentaa pelin tiedot väliaikaisesti esim. jos puhelu yllättää
    // onRestoreInstanceState ja onSaveInstanceState kutsutaan automaattisesti Android-järjestelmän
    // toimesta, joten sinun pitää vain huolehtia, että toteutat metodit

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        Log.d("Ristinolle", "Luetaan muistista");
        matriisi   = bundle.getIntArray("MATRIISI");
        peliSiirto = bundle.getInt("SIIRTO");
        peliVuoro  = bundle.getInt("VUORO");
        peliStatus = bundle.getBoolean("STATUS");
        paivitaUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Log.d("Ristinolla", "Talletetaan muistiin");
        bundle.putIntArray("MATRIISI", matriisi);
        bundle.putInt("SIIRTO", peliSiirto);
        bundle.putInt("VUORO", peliVuoro);
        bundle.putBoolean("STATUS", peliStatus);
    }

    /*
     * UI päivitysmetodi, jolla tila saadaan takaisin esim. kun activity palautuu
     */
    private void paivitaUI() {
        for (int i = 0; i < matriisi.length; i++) {
            if (matriisi[i] == PELAAJA) {
                //btn[i].setBackgroundColor(Color.GREEN);
                //btn[i].setText("X");
                btn[i].setBackground(getDrawable(R.drawable.risti));
            }
            if (matriisi[i] == ANDROID) {
                //btn[i].setBackgroundColor(Color.RED);
                //btn[i].setText("O");
                btn[i].setBackground(getDrawable(R.drawable.nolla));
            }
            if (matriisi[i] == TYHJA) {
                //btn[i].setBackgroundColor(Color.GRAY);
                //btn[i].setText(""+i);
                btn[i].setBackground(getDrawable(R.drawable.tyhja));
            }
        }
    }


    /*
      Tarkistetaan kumpi voitti
     */
    private boolean tarkistaVoittiko(int merkki) {

        // tarkistetaan ensin löytyykö voittoriviä
        boolean loytyi = false;
        // vaakarivien tarkistus
        if (matriisi[0] == merkki && matriisi[1] == merkki && matriisi[2] == merkki)
            loytyi = true;
        if (matriisi[3] == merkki && matriisi[4] == merkki && matriisi[5] == merkki)
            loytyi = true;
        if (matriisi[6] == merkki && matriisi[7] == merkki && matriisi[8] == merkki)
            loytyi = true;

        // pystyrivien tarkistus
        if (matriisi[0] == merkki && matriisi[3] == merkki && matriisi[6] == merkki)
            loytyi = true;
        if (matriisi[1] == merkki && matriisi[4] == merkki && matriisi[7] == merkki)
            loytyi = true;
        if (matriisi[2] == merkki && matriisi[5] == merkki && matriisi[8] == merkki)
            loytyi = true;

        // vinorivien tarkistus
        if (matriisi[0] == merkki && matriisi[4] == merkki && matriisi[8] == merkki)
            loytyi = true;
        if (matriisi[2] == merkki && matriisi[4] == merkki && matriisi[6] == merkki)
            loytyi = true;

        // jos ei löytynyt palautetaan false
        return loytyi;


    }


}
