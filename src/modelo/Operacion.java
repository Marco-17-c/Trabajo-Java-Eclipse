package modelo;

/*
 * El resultado de evaluar una expresion. Es lo que el motor devuelve
 * y lo que la calculadora muestra e guarda en el historial.
 */
public class Operacion {

    private String expresion;
    private String postfija;
    private double resultado;
    private boolean exito;
    private String mensaje;

    private Operacion(String expresion, String postfija, double resultado,
                      boolean exito, String mensaje) {
        this.expresion = expresion;
        this.postfija = postfija;
        this.resultado = resultado;
        this.exito = exito;
        this.mensaje = mensaje;
    }

    public static Operacion ok(String expresion, String postfija, double resultado) {
        return new Operacion(expresion, postfija, resultado, true, "Calculo correcto.");
    }

    public static Operacion error(String expresion, String mensaje) {
        return new Operacion(expresion, "", 0, false, mensaje);
    }

    public String getExpresion() {
        return expresion;
    }

    public String getPostfija() {
        return postfija;
    }

    public double getResultado() {
        return resultado;
    }

    public boolean esExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getResultadoFormateado() {
        if (!exito) {
            return "ERROR";
        }
        if (resultado == Math.floor(resultado) && !Double.isInfinite(resultado)) {
            return String.valueOf((long) resultado);
        }
        return String.valueOf(Math.round(resultado * 100000.0) / 100000.0);
    }

    public String toString() {
        return expresion + " = " + getResultadoFormateado();
    }
}