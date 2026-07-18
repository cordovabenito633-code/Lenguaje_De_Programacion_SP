
package com.mycompany.lenguaje;

public class Estado {
    private final String nombre;
    private final boolean esInicial;
    private final boolean esAceptacion;
    private final boolean esError;
    private int x;
    private int y;

    public Estado(String nombre, boolean esInicial, boolean esAceptacion, boolean esError, int x, int y) {
        this.nombre = nombre;
        this.esInicial = esInicial;
        this.esAceptacion = esAceptacion;
        this.esError = esError;
        this.x = x;
        this.y = y;
    }
    public String getNombre() {
        return nombre;
    }
    public boolean isEsInicial() {
        return esInicial;
    }
    public boolean isEsError() { 
        return esError; 
    }
    public boolean isEsAceptacion() {
        return esAceptacion;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    @Override
    public String toString() {
        return nombre + (esAceptacion ? " (Aceptación)" : "") + (esError ? " (Error)" : "");
    }
    
}
