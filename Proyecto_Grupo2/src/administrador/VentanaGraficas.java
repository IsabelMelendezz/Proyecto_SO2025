/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package administrador;

import javax.swing.*;
import java.awt.*;
import java.lang.management.*;
import com.sun.management.OperatingSystemMXBean; //Para CPU
import java.util.LinkedList;

/**
 *
 * @author david
 */

//CARLOS-9959-23-848
public class VentanaGraficas extends javax.swing.JFrame {

//CARLOS-9959-23-848
     public VentanaGraficas() {
        initComponents();
        setIconImage(getIconImage());
        
        //Tamaños panel
        PanelMemoria.setPreferredSize(new Dimension(400, 100));
        PanelCPU.setPreferredSize(new Dimension(400, 250));

        //Reemplazamos paneles por gráficas
        PanelMemoria.setLayout(new BorderLayout());
        PanelMemoria.add(new GraficaMemoria(), BorderLayout.CENTER);

        PanelCPU.setLayout(new BorderLayout());
        PanelCPU.add(new GraficaCPU(), BorderLayout.CENTER);

        setSize(620, 480);
        setLocationRelativeTo(null);
        setTitle("Gráficas del sistema");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
//VICTOOR--------------------------------------------------------------------------------------------------------------------------
   @Override
    public Image getIconImage() {
       return Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("img/logo.png"));
    }  
     
//CARLOS-9959-23-848
    class GraficaMemoria extends JPanel {
        private LinkedList<Integer> datos = new LinkedList<>();
        private final int MAX_DATOS = 60;
        private OperatingSystemMXBean osBean;

        public GraficaMemoria() {
            osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            Timer timer = new Timer(1000, e -> actualizarDatos());
            timer.start();
        }
//CRISTIAAAAAAAAAAAAAAAAAAAAAAAA--------------------------------------------------------------------------------------------------
        private void actualizarDatos() {
            long total = osBean.getTotalPhysicalMemorySize();
            long libre = osBean.getFreePhysicalMemorySize();
            long usada = total - libre;
            int porcentaje = (int) ((usada * 100) / total);

            if (datos.size() >= MAX_DATOS) datos.removeFirst();
            datos.add(porcentaje);
            repaint();
        }

        @Override
//CARLOS-9959-23-848
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();

            //Fondo
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, width, height);

            //Ejes
            g2.setColor(Color.RED);
            g2.drawLine(40, 0, 40, height - 20); //eje Y
            g2.drawLine(40, height - 20, width, height - 20); //eje X

            //Dibuja graficaMe
            if (datos.size() < 2) return;
            g2.setColor(Color.GREEN);
            int step = (width - 50) / MAX_DATOS;
            for (int i = 0; i < datos.size() - 1; i++) {
                int x1 = 40 + i * step;
                int y1 = height - 20 - (datos.get(i) * (height - 30) / 100);
                int x2 = 40 + (i + 1) * step;
                int y2 = height - 20 - (datos.get(i + 1) * (height - 30) / 100);
                g2.drawLine(x1, y1, x2, y2);
            }
            
            g2.setFont(new Font("Lucida Console", Font.BOLD, 15));   
            g2.drawString("Memoria usada (%)", 50, 15);
        }
    }

//ISAAAAA-------------------------------------------------------------------------------------------------------------------------
    class GraficaCPU extends JPanel {
        private LinkedList<Integer> datos = new LinkedList<>();
        private final int MAX_DATOS = 60;
        private OperatingSystemMXBean osBean;

        public GraficaCPU() {
            osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            Timer timer = new Timer(1000, e -> actualizarDatos());
            timer.start();
        }
//CRISTIAAAAAN-----------------------------------------------------------------------------------------------------------------
        private void actualizarDatos() {
            double load = osBean.getSystemCpuLoad();
            int porcentaje = load >= 0 ? (int) (load * 100) : 0;

            if (datos.size() >= MAX_DATOS) datos.removeFirst();
            datos.add(porcentaje);
            repaint();
        }
//ISAAA------------------------------------------------------------------------------------------------------------------------
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();

            //Fondo
            g2.setColor(Color.BLACK); 
            g2.fillRect(0, 0, width, height);

            //Ejes
            g2.setColor(Color.RED);
            g2.drawLine(40, 0, 40, height - 20); //eje Y
            g2.drawLine(40, height - 20, width, height - 20); //eje X

            // Dibuja GraficaCPU
            if (datos.size() < 2) return;
            g2.setColor(Color.CYAN);
            int step = (width - 50) / MAX_DATOS;
            for (int i = 0; i < datos.size() - 1; i++) {
                int x1 = 40 + i * step;
                int y1 = height - 20 - (datos.get(i) * (height - 30) / 100);
                int x2 = 40 + (i + 1) * step;
                int y2 = height - 20 - (datos.get(i + 1) * (height - 30) / 100);
                g2.drawLine(x1, y1, x2, y2);
            }
            g2.setFont(new Font("Lucida Console", Font.BOLD, 15)); 
            g2.drawString("Uso CPU (%)", 50, 15);
        }
    }


    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelMemoria = new javax.swing.JPanel();
        PanelCPU = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout PanelMemoriaLayout = new javax.swing.GroupLayout(PanelMemoria);
        PanelMemoria.setLayout(PanelMemoriaLayout);
        PanelMemoriaLayout.setHorizontalGroup(
            PanelMemoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        PanelMemoriaLayout.setVerticalGroup(
            PanelMemoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 145, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout PanelCPULayout = new javax.swing.GroupLayout(PanelCPU);
        PanelCPU.setLayout(PanelCPULayout);
        PanelCPULayout.setHorizontalGroup(
            PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 594, Short.MAX_VALUE)
        );
        PanelCPULayout.setVerticalGroup(
            PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 179, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelCPU, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(PanelMemoria, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(PanelMemoria, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelCPU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(VentanaGraficas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaGraficas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaGraficas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaGraficas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaGraficas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelCPU;
    private javax.swing.JPanel PanelMemoria;
    // End of variables declaration//GEN-END:variables

   
}
