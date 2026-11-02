package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import logica.Evaluador;
import modelo.Operacion;

public class VentanaCalculadora extends JFrame implements ActionListener {

    private Evaluador evaluador;

    private JLabel lblExpresion;
    private JLabel lblResultado;
    private JLabel lblPostfija;
    private JLabel lblEstado;

    private TablaProceso tabla;
    private DefaultListModel<String> modeloHistorial;

    private String expresion;

    public VentanaCalculadora() {
        evaluador = new Evaluador();
        expresion = "";

        setTitle("Calculadora de expresiones");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 780);
        setMinimumSize(new Dimension(1080, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Paleta.FONDO);
        setLayout(new BorderLayout());

        add(crearCabecera(), BorderLayout.NORTH);
        add(crearIzquierda(), BorderLayout.WEST);
        add(crearCentro(), BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);

        actualizarVisor();
    }

    private JPanel crearCabecera() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Paleta.PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 26, 18, 26));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("CALCULADORA DE EXPRESIONES");
        titulo.setFont(Paleta.TITULO);
        titulo.setForeground(Paleta.TEXTO);

        JLabel sub = new JLabel("Resuelve la expresion completa respetando la prioridad "
                + "de operadores. Por dentro, tres pilas hacen el trabajo.");
        sub.setFont(Paleta.SUBTITULO);
        sub.setForeground(Paleta.TEXTO_SUAVE);
        sub.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        textos.add(titulo);
        textos.add(sub);
        panel.add(textos, BorderLayout.WEST);
        return panel;
    }

    private JPanel crearIzquierda() {
        JPanel izq = new JPanel();
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
        izq.setBackground(Paleta.FONDO);
        izq.setPreferredSize(new Dimension(430, 0));
        izq.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 12));

        izq.add(crearVisor());
        izq.add(Box.createVerticalStrut(14));
        izq.add(crearTeclado());
        izq.add(Box.createVerticalStrut(16));
        izq.add(crearHistorial());

        return izq;
    }

    private JPanel crearVisor() {
        JPanel visor = new JPanel();
        visor.setLayout(new BoxLayout(visor, BoxLayout.Y_AXIS));
        visor.setBackground(Paleta.VISOR);
        visor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Paleta.BORDE),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        visor.setAlignmentX(LEFT_ALIGNMENT);
        visor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        lblExpresion = new JLabel(" ");
        lblExpresion.setFont(Paleta.EXPRESION);
        lblExpresion.setForeground(Paleta.TEXTO_SUAVE);
        lblExpresion.setAlignmentX(LEFT_ALIGNMENT);

        lblResultado = new JLabel("0");
        lblResultado.setFont(Paleta.RESULTADO);
        lblResultado.setForeground(Paleta.TEXTO);
        lblResultado.setAlignmentX(LEFT_ALIGNMENT);

        visor.add(lblExpresion);
        visor.add(Box.createVerticalStrut(8));
        visor.add(lblResultado);
        return visor;
    }

    private JPanel crearTeclado() {
        JPanel teclado = new JPanel(new GridLayout(5, 4, 8, 8));
        teclado.setOpaque(false);
        teclado.setAlignmentX(LEFT_ALIGNMENT);
        teclado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 290));

        String[][] filas = {
            { "C", "(", ")", "/" },
            { "7", "8", "9", "*" },
            { "4", "5", "6", "-" },
            { "1", "2", "3", "+" },
            { "0", ".", "<-", "=" },
        };

        for (int f = 0; f < filas.length; f++) {
            for (int c = 0; c < filas[f].length; c++) {
                teclado.add(tecla(filas[f][c]));
            }
        }
        return teclado;
    }

    private JButton tecla(String texto) {
        java.awt.Color color = Paleta.DIGITO;
        if (texto.equals("+") || texto.equals("-") || texto.equals("*") || texto.equals("/")) {
            color = Paleta.OPERADOR;
        } else if (texto.equals("(") || texto.equals(")")) {
            color = Paleta.PARENTESIS;
        } else if (texto.equals("=")) {
            color = Paleta.IGUAL;
        } else if (texto.equals("C") || texto.equals("<-")) {
            color = Paleta.BORRAR;
        }

        JButton b = new JButton(texto);
        b.setFont(Paleta.TECLA);
        b.setForeground(color == Paleta.DIGITO ? Paleta.TEXTO : new java.awt.Color(0x10, 0x16, 0x24));
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        b.setActionCommand(texto);
        b.addActionListener(this);
        return b;
    }

    private JPanel crearHistorial() {
        JPanel caja = new JPanel(new BorderLayout());
        caja.setOpaque(false);
        caja.setAlignmentX(LEFT_ALIGNMENT);

        JLabel rotulo = new JLabel("HISTORIAL");
        rotulo.setFont(Paleta.ETIQUETA);
        rotulo.setForeground(Paleta.TEXTO_SUAVE);
        rotulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        modeloHistorial = new DefaultListModel<String>();
        JList<String> lista = new JList<String>(modeloHistorial);
        lista.setFont(Paleta.MONO);
        lista.setBackground(Paleta.VISOR);
        lista.setForeground(Paleta.TEXTO_SUAVE);
        lista.setSelectionBackground(Paleta.CAMPO);
        lista.setSelectionForeground(Paleta.TEXTO);
        lista.setFixedCellHeight(22);

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createLineBorder(Paleta.BORDE));
        scroll.getViewport().setBackground(Paleta.VISOR);

        caja.add(rotulo, BorderLayout.NORTH);
        caja.add(scroll, BorderLayout.CENTER);
        return caja;
    }

    private JPanel crearCentro() {
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Paleta.FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(20, 12, 16, 24));

        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setOpaque(false);

        JLabel rotulo = new JLabel("QUE PASA POR DENTRO");
        rotulo.setFont(Paleta.ETIQUETA);
        rotulo.setForeground(Paleta.OPERADOR);
        rotulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel explica = new JLabel("De infija a postfija. La columna Pila es el contenido "
                + "real de la pila de operadores en cada paso.");
        explica.setFont(Paleta.SUBTITULO);
        explica.setForeground(Paleta.TEXTO_SUAVE);
        explica.setAlignmentX(LEFT_ALIGNMENT);
        explica.setBorder(BorderFactory.createEmptyBorder(4, 0, 12, 0));

        encabezado.add(rotulo);
        encabezado.add(explica);

        tabla = new TablaProceso();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(Paleta.BORDE));
        scroll.getViewport().setBackground(Paleta.VISOR);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(Paleta.PANEL);
        pie.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel rotPost = new JLabel("EXPRESION POSTFIJA RESULTANTE");
        rotPost.setFont(Paleta.ETIQUETA);
        rotPost.setForeground(Paleta.TEXTO_SUAVE);

        lblPostfija = new JLabel("—");
        lblPostfija.setFont(Paleta.MONO_FUERTE);
        lblPostfija.setForeground(Paleta.IGUAL);
        lblPostfija.setHorizontalAlignment(SwingConstants.RIGHT);

        pie.add(rotPost, BorderLayout.WEST);
        pie.add(lblPostfija, BorderLayout.EAST);

        centro.add(encabezado, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);
        centro.add(pie, BorderLayout.SOUTH);
        return centro;
    }

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barra.setBackground(Paleta.PANEL);
        barra.setBorder(BorderFactory.createEmptyBorder(14, 26, 14, 26));

        lblEstado = new JLabel("Escribe una expresion y presiona = para calcularla.");
        lblEstado.setFont(Paleta.NORMAL);
        lblEstado.setForeground(Paleta.TEXTO_SUAVE);

        barra.add(lblEstado);
        return barra;
    }

    public void actionPerformed(ActionEvent evento) {
        String comando = evento.getActionCommand();

        if (comando.equals("C")) {
            expresion = "";
            lblResultado.setText("0");
            lblResultado.setForeground(Paleta.TEXTO);
            lblPostfija.setText("—");
            tabla.limpiar();
            informar(true, "Expresion borrada.");
        } else if (comando.equals("<-")) {
            if (!expresion.isEmpty()) {
                expresion = expresion.substring(0, expresion.length() - 1);
            }
        } else if (comando.equals("=")) {
            calcular();
        } else {
            expresion += comando;
        }
        actualizarVisor();
    }

    private void calcular() {
        Operacion operacion = evaluador.evaluar(expresion);

        if (operacion.esExito()) {
            lblResultado.setText(operacion.getResultadoFormateado());
            lblResultado.setForeground(Paleta.IGUAL);
            lblPostfija.setText(operacion.getPostfija());
            tabla.mostrar(evaluador.getTraza());
            modeloHistorial.add(0, operacion.toString());
            informar(true, "Resultado: " + operacion.getResultadoFormateado()
                    + "   ·   la pila entrego los operadores en el orden correcto.");
        } else {
            lblResultado.setText("ERROR");
            lblResultado.setForeground(Paleta.ERROR);
            lblPostfija.setText("—");
            tabla.limpiar();
            informar(false, operacion.getMensaje());
        }
    }

    private void actualizarVisor() {
        lblExpresion.setText(expresion.isEmpty() ? " " : expresion);
    }

    private void informar(boolean exito, String mensaje) {
        lblEstado.setText(mensaje);
        lblEstado.setForeground(exito ? Paleta.TEXTO_SUAVE : Paleta.ERROR);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new VentanaCalculadora().setVisible(true);
            }
        });
    }
}