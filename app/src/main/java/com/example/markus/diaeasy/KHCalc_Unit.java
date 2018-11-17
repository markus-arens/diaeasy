package com.example.markus.diaeasy;

public class KHCalc_Unit{
    private double g100, g_gegessen;
    private String Zutat_str;

    public KHCalc_Unit(String Zutat, double g100, double g_gegessen) {
        if (Zutat.equals(""))
            Zutat = "Zutat";
        this.Zutat_str = Zutat;
        this.g100 = g100;
        this.g_gegessen = g_gegessen;

    }

    public double getKohlenhydrate() {
        return Math.round((g100 / 100 * g_gegessen)*100 )/100;
    }

    public double getKEs() {
        return getKohlenhydrate() / 10;
    }

    public String toString()  {
        return String.valueOf("(" + Zutat_str + ") " + g100 + " / 100 * " + g_gegessen + " = " + getKEs() + " (KEs)");
    }

}
