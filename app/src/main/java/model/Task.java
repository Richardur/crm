package model;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Task {

    public int ID;
    public int ReakcijosHeader;
    public int VeiksmoID;
    public Timestamp PradData;
    public Timestamp PradDataReal;
    public short Pradeta;
    public Timestamp AtlikData;
    public Timestamp AtlikDataReal;
    public short Atlikta;
    public String Komentaras;
    public int DarbID;
    public int KlientasID;
    public String Trukme;
    public int day;
    public Date hour;
    public Date minute;


    public Task(int ID, int ReakcijosHeader, int VeiksmoID,
                Timestamp PradData, Timestamp PradDataReal,
                short Pradeta, Timestamp AtlikData, Timestamp AtlikDataReal,
                short Atlikta, String Komentaras, int DarbID, int KlientasID, String Trukme) {

        this.ID = ID;
        this.ReakcijosHeader=ReakcijosHeader;
        this.VeiksmoID=VeiksmoID;
        this.PradData=PradData;
        this.PradDataReal=PradDataReal;
        this.Pradeta=Pradeta;
        this.AtlikData=AtlikData;
        this.AtlikDataReal=AtlikDataReal;
        this.Atlikta=Atlikta;
        this.Komentaras=Komentaras;
        this.DarbID=DarbID;
        this.KlientasID=KlientasID;
        this.Trukme=Trukme;

    }
    public String getTime(Timestamp t){
        long l = t.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String dateString = sdf.format(calendar.getTime());
        return dateString;
    }


}
