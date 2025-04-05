package Modeli;

import java.sql.Time;
import java.util.Date;

public class Seansa {
    private int id;
    private Date date;
    private Time vreme;
    private int trajanje; //U minutima
    private String beleske;
    private double cenaPoSatu;



    public int izracunajCenu(){
        return (int) (cenaPoSatu*(trajanje/60+1));

    }

}
