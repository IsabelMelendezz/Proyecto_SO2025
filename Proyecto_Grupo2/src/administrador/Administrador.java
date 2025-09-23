
package administrador;

//DISEÑO Y COMPONENTES 
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

//EVENTOS
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//CPU Y MEMORIA 
import java.io.BufferedReader;
import java.io.InputStreamReader;

//AYUDA
import java.io.File;
import java.io.IOException;

//ORDEN MAYO A MENOR 
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author david
 */

public class Administrador extends javax.swing.JFrame {
 
    private DefaultTableModel modelo;
    private TableRowSorter<TableModel> sorter; //Declaramos globalmente
    private Timer timer; //Timer global 
    
    //VICTOR 9959-23-10733
    private void filtrarTabla() {
    String texto = jTextField1.getText().trim();

    if (texto.isEmpty()) {
        sorter.setRowFilter(null); // Quita el filtro si no hay texto
    } else {
        sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + texto, 0)); 
        // "(?i)" -> ignora mayúsculas/minúsculas
        // 0 -> columna del nombre del proceso
    }
}

//CARLOS-9959-23-848    
    public Administrador() {
        
        initComponents();
        setIconImage(getIconImage());
  // --- FILTRO DE BUSQUEDA --- Victor 9959-23-1033
jTextField1.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
    @Override
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        filtrarTabla();
    }
    @Override
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        filtrarTabla();
    }
    @Override
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        filtrarTabla();
    }
});

        
//DISEÑO TABLA------------------------------------------------------------------------------------------
        JTableHeader tabla = jtabla_datos.getTableHeader();

        tabla.setForeground(Color.DARK_GRAY);

        tabla.setFont(new Font("Lucida Console", Font.PLAIN, 13));

        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) tabla.getDefaultRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

//--------------------------Compara valores para ordenar mayor a menor----------------------------------
        
        sorter = new TableRowSorter<>(jtabla_datos.getModel());
        jtabla_datos.setRowSorter(sorter);

        sorter.setComparator(4, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                try {
                    s1 = s1.replaceAll("[^0-9]", "");
                    s2 = s2.replaceAll("[^0-9]", "");
                    long n1 = Long.parseLong(s1.isEmpty() ? "0" : s1);
                    long n2 = Long.parseLong(s2.isEmpty() ? "0" : s2);
                    return Long.compare(n1, n2);
                } catch (Exception e) {
                    return s1.compareTo(s2);
                }
            }
        });
        sorter.toggleSortOrder(4);
        
        getContentPane().setBackground(new Color(160, 170, 180));
        this.setLocationRelativeTo(null);
        No_procesos.setFocusable(false);
        iniciarActualizacion();
    }

    
    //CRISTIAN 9959-23-1567 
    
    private void Alineacion_Columnas(){
        DefaultTableCellRenderer Alinear = new DefaultTableCellRenderer();
        Alinear.setHorizontalAlignment(SwingConstants.LEFT); //establece de que forma se va a alinear las columnas
        jtabla_datos.getColumnModel().getColumn(1).setCellRenderer(Alinear);//alinea columna 1
        jtabla_datos.getColumnModel().getColumn(2).setCellRenderer(Alinear);//alinea columna 2
        jtabla_datos.getColumnModel().getColumn(3).setCellRenderer(Alinear);//alinea columna 3
        jtabla_datos.getColumnModel().getColumn(4).setCellRenderer(Alinear);//alinea columna 4
        jtabla_datos.getColumnModel().getColumn(5).setCellRenderer(Alinear); 
    }
    
    
   //CARLOS-9959-23-848
         private void iniciarActualizacion() {
    // ajustar tiempo
     timer = new Timer(5000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            mostrar_procesos();
        }
    });
    timer.start();
}
    
     //VICTOR 9959-23-10733   
    @Override
    public Image getIconImage() {
       return Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("img/logo.png"));
    }
    
    
//VICTOR 9959-23-10733
  private void mostrar_procesos() {
   
    try {
        modelo = (DefaultTableModel) jtabla_datos.getModel();
        modelo.setRowCount(0);

        //Guardamos CPU por PID 
        Map<String, String> mapaCPU = new HashMap<>();

        //Funcion WMIC para CPU
        Process pCPU = Runtime.getRuntime().exec("wmic path Win32_PerfFormattedData_PerfProc_Process get IDProcess,PercentProcessorTime /FORMAT:CSV");
        BufferedReader inputCPU = new BufferedReader(new InputStreamReader(pCPU.getInputStream()));
        String lineCPU;
        while ((lineCPU = inputCPU.readLine()) != null) {
            lineCPU = lineCPU.trim();
            if (lineCPU.isEmpty() || lineCPU.startsWith("Node")) continue;
            String[] datos = lineCPU.split(",");
            if (datos.length < 3) continue;

            String pid = datos[1];
            String cpu = datos[2] + " %";
            mapaCPU.put(pid, cpu);
        }
        inputCPU.close();

        //Funcion Tasklist para Sesión y Memoria
        Process p = Runtime.getRuntime().exec("tasklist /FO CSV /NH");
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        int contador = 0;

        while ((line = input.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            //El formato CSV de tasklist trae datos entre comillas
            String[] datos = line.split("\",\"");
            for (int i = 0; i < datos.length; i++) {
                datos[i] = datos[i].replace("\"", ""); //quitar comillas
            }

            if (datos.length < 5) continue;

            String proceso = datos[0];
            String pid = datos[1];
            String sesion = datos[2];
            String noSesion = datos[3];
            String memoria = datos[4];
            String cpu = mapaCPU.getOrDefault(pid, "0 %");

            
            Object[] fila = new Object[6];
            fila[0] = proceso;
            fila[1] = pid;
            fila[2] = sesion;
            fila[3] = noSesion;
            fila[4] = memoria;
            fila[5] = cpu;

            modelo.addRow(fila);
            contador++;
        }

        input.close();

        //Se aplica el modelo y alineación
        jtabla_datos.setModel(modelo);
        jtabla_datos.setRowSorter(sorter);
        Alineacion_Columnas();
        No_procesos.setText(String.valueOf(contador));

    } catch (Exception err) {
        err.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al obtener procesos: " + err.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    //Isabel Melendez 9959-23-1379---------------------------------------------------------------------------------------------------------------------------
    void LimpiarTabla(){
       if (jtabla_datos.getModel() instanceof DefaultTableModel) {
        modelo = (DefaultTableModel) jtabla_datos.getModel();
        modelo.setRowCount(0); 
    }
    }
   //Isabel Melendez 9959-23-1379----------------------------------------------------------------------------------------------------------------------------- 
public void Matar_proceso() {
    modelo = (DefaultTableModel) jtabla_datos.getModel();

    // Verificar fila seleccionada
    int filaSeleccionada = jtabla_datos.getSelectedRow();
    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this,
                "ERROR, No se ha seleccionado ningún proceso",
                "Error", JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    int filaModelo = jtabla_datos.convertRowIndexToModel(filaSeleccionada);
    // Obtenemos el PID 
        String pid = String.valueOf(modelo.getValueAt(filaModelo, 1));

    try {
        Process hijo = Runtime.getRuntime().exec("taskkill /PID " + pid + " /F"); //Elimina Proceso
        hijo.waitFor();
        JOptionPane.showMessageDialog(this,
                "Proceso con PID " + pid + " finalizado.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException | InterruptedException ex) {
        Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this,
                "Error al finalizar proceso: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jtabla_datos = new javax.swing.JTable();
        btnFprocesos = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        No_procesos = new javax.swing.JTextField();
        btnEdit = new javax.swing.JButton();
        btnAyuda = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Administrador de tareas");

        jtabla_datos.setBackground(new java.awt.Color(0, 0, 0));
        jtabla_datos.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jtabla_datos.setFont(new java.awt.Font("Lucida Console", 0, 12)); // NOI18N
        jtabla_datos.setForeground(new java.awt.Color(255, 255, 255));
        jtabla_datos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Proceso", "PID", "Sesión ", "NO.sesión", "Memoria", "CPU"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jtabla_datos);

        btnFprocesos.setFont(new java.awt.Font("Lucida Console", 0, 12)); // NOI18N
        btnFprocesos.setText("FINALIZAR PROCESO");
        btnFprocesos.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnFprocesos.setBorderPainted(false);
        btnFprocesos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFprocesosActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Lucida Console", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(230, 230, 230));
        jLabel2.setText("TOTAL DE PROCESOS: ");

        No_procesos.setBackground(new java.awt.Color(160, 170, 180));
        No_procesos.setFont(new java.awt.Font("Lucida Console", 1, 12)); // NOI18N
        No_procesos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                No_procesosActionPerformed(evt);
            }
        });

        btnEdit.setFont(new java.awt.Font("Lucida Console", 0, 12)); // NOI18N
        btnEdit.setText("GRAFICOS");
        btnEdit.setBorderPainted(false);
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnAyuda.setFont(new java.awt.Font("Lucida Console", 0, 12)); // NOI18N
        btnAyuda.setText("AYUDA");
        btnAyuda.setBorderPainted(false);
        btnAyuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyudaActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Lucida Console", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(230, 230, 230));
        jLabel3.setText("BUSCAR");

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(No_procesos, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addComponent(btnAyuda)
                .addGap(34, 34, 34)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(btnEdit)
                .addGap(58, 58, 58)
                .addComponent(btnFprocesos, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEdit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFprocesos, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addComponent(No_procesos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAyuda, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Boton de cerrar o matar procesos
    private void btnFprocesosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFprocesosActionPerformed
       
        Matar_proceso();
        LimpiarTabla();
        mostrar_procesos();
    }//GEN-LAST:event_btnFprocesosActionPerformed

    private void No_procesosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_No_procesosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_No_procesosActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
      VentanaGraficas graficas = new VentanaGraficas();
      graficas.setVisible(true);
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyudaActionPerformed
        // TODO add your handling code here:
        //Isabel Melendez 9959-23-1379
        try {
            if ((new File("src\\ayuda\\Ayuda Administrador.chm")).exists()) {
                Process p = Runtime
                        .getRuntime()
                        .exec("rundll32 url.dll,FileProtocolHandler src\\ayuda\\Ayuda Administrador.chm");
                p.waitFor();
            } else {
                System.out.println("La ayuda no Fue encontrada");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnAyudaActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

       

  
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Administrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Administrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Administrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Administrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Administrador().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField No_procesos;
    private javax.swing.JButton btnAyuda;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnFprocesos;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable jtabla_datos;
    // End of variables declaration//GEN-END:variables
}
