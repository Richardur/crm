package model;

public class Customer {

    public int id;
    public int adrid;
    public String username;
    public String at_v; // vardas
    public String at_p; // pavarde
    public String at_e; // el pastas
    public String at_t; // tel nr

    public Customer(int id, int adrid, String username, String at_v, String at_p, String at_e, String at_t) {
        this.id = id;
        this.adrid = adrid;
        this.username = username;
        this.at_v = at_v;
        this.at_p = at_p;
        this.at_e = at_e;
        this.at_t = at_t;
    }

}