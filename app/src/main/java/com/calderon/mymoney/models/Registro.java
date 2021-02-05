package com.calderon.mymoney.models;

public class Registro {

    private float total;
    private String fecha;
    private float capital;
    private float ahorrado;
    private float invertido;
    private boolean expanded;

    public Registro(){}

    public Registro(float total, String fecha, float capital, float ahorrado, float invertido) {
        this.total = total;
        this.fecha = fecha;
        this.capital = capital;
        this.ahorrado = ahorrado;
        this.invertido = invertido;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public float getCapital() {
        return capital;
    }

    public void setCapital(float capital) {
        this.capital = capital;
    }

    public float getAhorrado() {
        return ahorrado;
    }

    public void setAhorrado(float ahorrado) {
        this.ahorrado = ahorrado;
    }

    public float getInvertido() {
        return invertido;
    }

    public void setInvertido(float invertido) {
        this.invertido = invertido;
    }

    @Override
    public String toString() {
        return "Registro" +
                "\ntotal=" + total +
                " | fecha='" + fecha +
                " | capital=" + capital +
                " | ahorrado=" + ahorrado +
                " | invertido=" + invertido ;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
