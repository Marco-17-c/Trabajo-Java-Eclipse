package logica;

/* Una fila de la traza: que token se leyo y como quedaron las pilas. */
public class Paso {

    private int numero;
    private String token;
    private String accion;
    private String pila;
    private String salida;

    public Paso(int numero, String token, String accion, String pila, String salida) {
        this.numero = numero;
        this.token = token;
        this.accion = accion;
        this.pila = pila;
        this.salida = salida;
    }

    public int getNumero() {
        return numero;
    }

    public String getToken() {
        return token;
    }

    public String getAccion() {
        return accion;
    }

    public String getPila() {
        return pila;
    }

    public String getSalida() {
        return salida;
    }
}