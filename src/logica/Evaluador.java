package logica;

import modelo.Operacion;

/*
 * El motor de la calculadora. Hace lo mismo que hace un compilador cuando
 * encuentra una expresion aritmetica, y lo hace con dos pilas:
 *
 *   1. Valida que los parentesis esten balanceados      -> PilaOperadores
 *   2. Convierte la expresion infija a postfija         -> PilaOperadores
 *   3. Evalua la expresion postfija                     -> PilaOperandos
 *
 * La prioridad de operadores no se resuelve con ifs anidados: emerge sola
 * del orden en que la pila entrega los operadores.
 */
public class Evaluador {

    private static final int MAX_PASOS = 200;

    private Paso[] traza;
    private int cantidadPasos;

    public Evaluador() {
        traza = new Paso[MAX_PASOS];
        cantidadPasos = 0;
    }

    public Paso[] getTraza() {
        Paso[] copia = new Paso[cantidadPasos];
        for (int i = 0; i < cantidadPasos; i++) {
            copia[i] = traza[i];
        }
        return copia;
    }

    public Operacion evaluar(String expresion) {
        cantidadPasos = 0;

        if (expresion == null || expresion.trim().isEmpty()) {
            return Operacion.error(expresion, "Escribe una expresion.");
        }

        String[] tokens = separar(expresion);
        if (tokens == null) {
            return Operacion.error(expresion, "La expresion tiene un caracter no permitido.");
        }
        if (tokens.length == 0) {
            return Operacion.error(expresion, "Escribe una expresion.");
        }

        String errorDelimitadores = validarDelimitadores(tokens);
        if (errorDelimitadores != null) {
            return Operacion.error(expresion, errorDelimitadores);
        }

        String[] postfija = aPostfija(tokens);
        if (postfija == null) {
            return Operacion.error(expresion, "No se pudo convertir la expresion. "
                    + "Revisa que no falten operandos u operadores.");
        }

        String textoPostfija = unir(postfija);

        try {
            double resultado = evaluarPostfija(postfija);
            return Operacion.ok(expresion, textoPostfija, resultado);
        } catch (ArithmeticException e) {
            return Operacion.error(expresion, e.getMessage());
        }
    }

    /* Convierte "12+3*(8-5)" en los tokens 12 + 3 * ( 8 - 5 ) */
    private String[] separar(String expresion) {
        String[] tokens = new String[expresion.length()];
        int cantidad = 0;
        String numero = "";

        for (int i = 0; i < expresion.length(); i++) {
            char c = expresion.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                numero += c;
            } else {
                if (!numero.isEmpty()) {
                    tokens[cantidad] = numero;
                    cantidad++;
                    numero = "";
                }
                if (c == ' ') {
                    continue;
                }
                if (!esOperador(c) && c != '(' && c != ')') {
                    return null;        // caracter no permitido
                }
                tokens[cantidad] = String.valueOf(c);
                cantidad++;
            }
        }

        if (!numero.isEmpty()) {
            tokens[cantidad] = numero;
            cantidad++;
        }

        String[] exactos = new String[cantidad];
        for (int i = 0; i < cantidad; i++) {
            exactos[i] = tokens[i];
        }
        return exactos;
    }

    /*
     * PRIMER USO DE LA PILA: parentesis balanceados.
     * Cada "(" se apila. Cada ")" desapila. Si hay que desapilar y la pila
     * esta vacia, sobra un cierre. Si al final queda algo, falta un cierre.
     */
    public String validarDelimitadores(String[] tokens) {
        PilaOperadores pila = new PilaOperadores(64);

        for (int i = 0; i < tokens.length; i++) {
            String t = tokens[i];

            if (t.equals("(")) {
                pila.push('(');
            } else if (t.equals(")")) {
                if (pila.estaVacia()) {
                    return "Hay un parentesis de cierre sin su apertura.";
                }
                pila.pop();
            }
        }

        if (!pila.estaVacia()) {
            return "Faltan " + pila.getTamano() + " parentesis de cierre.";
        }
        return null;
    }

    /*
     * SEGUNDO USO DE LA PILA: infija a postfija (algoritmo shunting-yard).
     * La pila guarda operadores a la espera. La prioridad sale sola del
     * orden en que se desapilan.
     */
    public String[] aPostfija(String[] tokens) {
        PilaOperadores pila = new PilaOperadores(64);
        String[] salida = new String[tokens.length];
        int cantidad = 0;

        for (int i = 0; i < tokens.length; i++) {
            String t = tokens[i];

            if (esNumero(t)) {
                salida[cantidad] = t;
                cantidad++;
                registrar(t, "Operando: va directo a la salida", pila, salida, cantidad);

            } else if (t.equals("(")) {
                pila.push('(');
                registrar(t, "Apertura: push", pila, salida, cantidad);

            } else if (t.equals(")")) {
                while (!pila.estaVacia() && pila.cima() != '(') {
                    salida[cantidad] = String.valueOf(pila.pop());
                    cantidad++;
                }
                if (pila.estaVacia()) {
                    return null;
                }
                pila.pop();     // descarta el "(" sin enviarlo a la salida
                registrar(t, "Cierre: pop hasta la apertura", pila, salida, cantidad);

            } else {
                char op = t.charAt(0);
                while (!pila.estaVacia() && pila.cima() != '('
                        && prioridad(pila.cima()) >= prioridad(op)) {
                    salida[cantidad] = String.valueOf(pila.pop());
                    cantidad++;
                }
                pila.push(op);
                registrar(t, "Pop de prioridad >= y luego push", pila, salida, cantidad);
            }
        }

        while (!pila.estaVacia()) {
            char op = pila.pop();
            if (op == '(') {
                return null;
            }
            salida[cantidad] = String.valueOf(op);
            cantidad++;
        }
        registrar("fin", "Pop de todo lo que queda", pila, salida, cantidad);

        String[] exactos = new String[cantidad];
        for (int i = 0; i < cantidad; i++) {
            exactos[i] = salida[i];
        }
        return exactos;
    }

    /*
     * TERCER USO DE LA PILA: evaluar la postfija.
     * Los numeros se apilan. Al llegar un operador se sacan dos y se apila
     * el resultado. El orden importa: el primero que sale es el de la derecha.
     */
    public double evaluarPostfija(String[] postfija) {
        PilaOperandos pila = new PilaOperandos(64);

        for (int i = 0; i < postfija.length; i++) {
            String t = postfija[i];

            if (esNumero(t)) {
                pila.push(Double.parseDouble(t));
            } else {
                if (pila.getTamano() < 2) {
                    throw new ArithmeticException("Faltan operandos para el operador " + t + ".");
                }
                double derecha = pila.pop();    // ojo: sale primero el de la derecha
                double izquierda = pila.pop();
                pila.push(aplicar(izquierda, derecha, t.charAt(0)));
            }
        }

        if (pila.getTamano() != 1) {
            throw new ArithmeticException("La expresion esta incompleta.");
        }
        return pila.pop();
    }

    private double aplicar(double a, double b, char operador) {
        if (operador == '+') {
            return a + b;
        }
        if (operador == '-') {
            return a - b;
        }
        if (operador == '*') {
            return a * b;
        }
        if (b == 0) {
            throw new ArithmeticException("No se puede dividir entre cero.");
        }
        return a / b;
    }

    private int prioridad(char operador) {
        if (operador == '+' || operador == '-') {
            return 1;
        }
        if (operador == '*' || operador == '/') {
            return 2;
        }
        return 0;
    }

    private boolean esOperador(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean esNumero(String t) {
        char c = t.charAt(0);
        return Character.isDigit(c) || (t.length() > 1 && c == '.');
    }

    private String unir(String[] tokens) {
        String texto = "";
        for (int i = 0; i < tokens.length; i++) {
            texto += tokens[i];
            if (i < tokens.length - 1) {
                texto += " ";
            }
        }
        return texto;
    }

    private void registrar(String token, String accion, PilaOperadores pila,
                           String[] salida, int cantidad) {
        if (cantidadPasos >= MAX_PASOS) {
            return;
        }
        String texto = "";
        for (int i = 0; i < cantidad; i++) {
            texto += salida[i];
            if (i < cantidad - 1) {
                texto += " ";
            }
        }
        traza[cantidadPasos] = new Paso(cantidadPasos + 1, token, accion,
                                        pila.contenido(), texto);
        cantidadPasos++;
    }
}