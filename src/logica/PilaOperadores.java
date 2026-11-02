package logica;

/*
 * Pila de caracteres: guarda operadores y parentesis.
 * Implementada con arreglo y un indice tope. tope = -1 significa vacia.
 */
public class PilaOperadores {

    private char[] datos;
    private int tope;

    public PilaOperadores(int capacidad) {
        if (capacidad < 1) {
            capacidad = 32;
        }
        datos = new char[capacidad];
        tope = -1;
    }

    public boolean push(char simbolo) {
        if (estaLlena()) {
            return false;
        }
        tope++;
        datos[tope] = simbolo;
        return true;
    }

    public char pop() {
        if (estaVacia()) {
            return '\0';
        }
        char simbolo = datos[tope];
        tope--;
        return simbolo;
    }

    public char cima() {
        return estaVacia() ? '\0' : datos[tope];
    }

    public boolean estaVacia() {
        return tope == -1;
    }

    public boolean estaLlena() {
        return tope == datos.length - 1;
    }

    public int getTamano() {
        return tope + 1;
    }

    public void vaciar() {
        tope = -1;
    }

    /* Desde la base hacia la cima, para mostrarlo en la traza. */
    public String contenido() {
        if (estaVacia()) {
            return "";
        }
        String texto = "";
        for (int i = 0; i <= tope; i++) {
            texto += datos[i];
            if (i < tope) {
                texto += " ";
            }
        }
        return texto;
    }
}