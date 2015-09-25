package io.tangent.chemesis.models;

/**
 * Created by Jesse on 9/19/2015.
 */
public enum EnergeticsField {

    GIBBS(    "Gibbs' Free Energy",             "delta-f G°",   "Δ-f G˚",  "kJ/mol"),
    ENTHALPY( "Standard Enthalpy of Formation", "delta-f H°",   "Δ-f H˚",   "kJ/mol"),
    ENTROPY(  "Standard Entropu of Formation",  "S°",           "S°",          "J/K/mol"),
    ;

    private String name;
    private String fieldname;
    private String shortname;
    private String units;

    EnergeticsField(String name, String fieldname, String shortname, String units){
        this.name = name;
        this.fieldname = fieldname;
        this.shortname = shortname;
        this.units = units;
    }

    public String getName() {
        return name;
    }

    public String getFieldname() {
        return fieldname;
    }

    public String getShortname() {
        return shortname;
    }

    public String getUnits() {
        return units;
    }
}
