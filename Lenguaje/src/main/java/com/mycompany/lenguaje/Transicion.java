
package com.mycompany.lenguaje;

public class Transicion {

    private final Estado origen;
    private final Estado destino;
    private final String etiqueta;

    public Transicion(Estado origen, Estado destino, String etiqueta) {
        this.origen = origen;
        this.destino = destino;
        this.etiqueta = etiqueta;
    }
    public Estado getOrigen() {
        return origen;
    }
    public Estado getDestino() {
        return destino;
    }
    public String getEtiqueta() {
        return etiqueta;
    }
    @Override
    public String toString() {
        return origen.getNombre() + " --[" + etiqueta + "]--> " + destino.getNombre();
    }
}

