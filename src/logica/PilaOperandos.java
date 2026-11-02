package logica;

/*
 * Pila de numeros: guarda los operandos mientras se evalua la postfija.
 * Misma tecnica que la otra pila, con arreglo de double.
 */
public class PilaOperandos {

    private double[] datos;
    private int tope;

    public PilaOperandos(int capacidad) {
        if (capacidad < 1) {
            capacidad = 32;
        }
        datos = new double[capacidad];
        tope = -1;
    }

    public boolean push(double valor) {
        if (estaLlena()) {
            return false;
        }
        tope++;
        datos[tope] = valor;
        return true;
    }

    public double pop() {
        if (estaVacia()) {
            return 0;
        }
        double valor = datos[tope];
        tope--;
        return valor;
    }

    public double cima() {
        return estaVacia() ? 0 : datos[tope];
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

    public String contenido() {
        if (estaVacia()) {
            return "";
        }
        String texto = "";
        for (int i = 0; i <= tope; i++) {
            double v = datos[i];
            texto += (v == Math.floor(v) ? String.valueOf((long) v) : String.valueOf(v));
            if (i < tope) {
                texto += " ";
            }
        }
        return texto;
    }
}
