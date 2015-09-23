package io.tangent.chemesis.models;

/**
 * Created by Jesse on 9/19/2015.
 */
public enum EnergeticsField {

    GIBBS(    "Gibbs' Free Energy",             "delta-f G°",   "kJ/mol"),
    ENTHALPY( "Standard Enthalpy of Formation", "delta-f H°",   "kJ/mol"),
    ENTROPY(  "Standard Entropu of Formation",  "S°",           "J/K/mol"),
    ;

    private String name;
    private String shortname;
    private String units;

    EnergeticsField(String name, String shortname, String units){
        this.name = name;
        this.shortname = shortname;
        this.units = units;
    }

    public String getName() {
        return name;
    }

    public String getShortname() {
        return shortname;
    }

    public String getUnits() {
        return units;
    }
}
