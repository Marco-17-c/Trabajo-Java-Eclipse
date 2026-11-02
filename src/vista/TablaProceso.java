package vista;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import logica.Paso;

/*
 * La tabla que muestra, paso a paso, como la pila va entregando operadores.
 * Es la misma vista que tendrias en un depurador: no dibuja la pila bonita,
 * muestra su contenido en cada iteracion.
 */
public class TablaProceso extends JTable {

    private DefaultTableModel modelo;

    public TablaProceso() {
        modelo = new DefaultTableModel(
                new String[] { "#", "Token", "Que hace la pila", "Pila", "Salida (postfija)" }, 0) {
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        setModel(modelo);

        setBackground(Paleta.VISOR);
        setForeground(Paleta.TEXTO);
        setGridColor(Paleta.BORDE);
        setRowHeight(26);
        setFont(Paleta.MONO);
        setSelectionBackground(Paleta.CAMPO);
        setSelectionForeground(Paleta.TEXTO);
        setShowVerticalLines(true);

        JTableHeader cabecera = getTableHeader();
        cabecera.setBackground(Paleta.PANEL);
        cabecera.setForeground(Paleta.TEXTO_SUAVE);
        cabecera.setFont(Paleta.ETIQUETA);
        cabecera.setReorderingAllowed(false);

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        getColumnModel().getColumn(0).setPreferredWidth(36);
        getColumnModel().getColumn(0).setCellRenderer(centrado);
        getColumnModel().getColumn(1).setPreferredWidth(54);
        getColumnModel().getColumn(1).setCellRenderer(centrado);
        getColumnModel().getColumn(2).setPreferredWidth(248);
        getColumnModel().getColumn(3).setPreferredWidth(104);
        getColumnModel().getColumn(4).setPreferredWidth(246);
    }

    public void mostrar(Paso[] pasos) {
        modelo.setRowCount(0);
        for (int i = 0; i < pasos.length; i++) {
            Paso p = pasos[i];
            modelo.addRow(new Object[] {
                p.getNumero(),
                p.getToken(),
                p.getAccion(),
                "[ " + p.getPila() + " ]",
                p.getSalida()
            });
        }
    }

    public void limpiar() {
        modelo.setRowCount(0);
    }
}