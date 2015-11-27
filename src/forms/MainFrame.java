package forms;

import controller.Coordinator;
import datasourceconnector.Connection;
import datasourceconnector.Datasource;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class MainFrame extends javax.swing.JFrame
{

    private final Coordinator coordinator;

    private MainFrame()
    {
        coordinator = null;
    }

    public MainFrame( Coordinator coordinator )
    {
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation( screenDimension.width / 4 - this.getSize().width / 4, screenDimension.height / 4 - this.getSize().height / 4 );
        this.coordinator = coordinator;
        initComponents();
        rSyntaxTextArea1.setCurrentLineHighlightColor( new Color(255,255,225) );
    }

    public void connectionSuccess()
    {
        import_menuItem.setEnabled( true );
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        xsdFileOpener = new javax.swing.JFileChooser();
        splitPanel_main = new javax.swing.JSplitPane();
        panel_left = new javax.swing.JPanel();
        treeTable_scrollPane = new javax.swing.JScrollPane();
        treeTable = new org.jdesktop.swingx.JXTreeTable();
        jLabel1 = new javax.swing.JLabel();
        panel_right = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        rSyntaxTextArea1 = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
        jLabel2 = new javax.swing.JLabel();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        import_menuItem = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        splitPanel_main.setDividerLocation(375);

        panel_left.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        treeTable_scrollPane.setViewportView(treeTable);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Terms");

        javax.swing.GroupLayout panel_leftLayout = new javax.swing.GroupLayout(panel_left);
        panel_left.setLayout(panel_leftLayout);
        panel_leftLayout.setHorizontalGroup(
            panel_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_leftLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(treeTable_scrollPane)
                    .addGroup(panel_leftLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 312, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panel_leftLayout.setVerticalGroup(
            panel_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_leftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(treeTable_scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                .addContainerGap())
        );

        splitPanel_main.setLeftComponent(panel_left);

        panel_right.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        rSyntaxTextArea1.setColumns(20);
        rSyntaxTextArea1.setRows(5);
        rSyntaxTextArea1.setMarkAllHighlightColor(new java.awt.Color(211, 211, 211));
        jScrollPane2.setViewportView(rSyntaxTextArea1);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("RDF");

        javax.swing.GroupLayout panel_rightLayout = new javax.swing.GroupLayout(panel_right);
        panel_right.setLayout(panel_rightLayout);
        panel_rightLayout.setHorizontalGroup(
            panel_rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_rightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                    .addGroup(panel_rightLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panel_rightLayout.setVerticalGroup(
            panel_rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_rightLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                .addContainerGap())
        );

        splitPanel_main.setRightComponent(panel_right);

        jMenu3.setText("File");

        jMenu5.setText("Connect");

        jMenuItem1.setText("Database");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem1);

        jMenuItem3.setText("XML File");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem3);

        jMenu3.add(jMenu5);

        jMenuBar2.add(jMenu3);

        jMenu4.setText("Terms");

        import_menuItem.setText("Import Terms");
        import_menuItem.setEnabled(false);
        import_menuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                import_menuItemActionPerformed(evt);
            }
        });
        jMenu4.add(import_menuItem);

        jMenuBar2.add(jMenu4);

        jMenu6.setText("RDF");

        jMenu7.setText("Generate");

        jMenuItem5.setText("RML Mapping");
        jMenuItem5.setEnabled(false);
        jMenu7.add(jMenuItem5);

        jMenuItem6.setText("OWL Ontology");
        jMenuItem6.setEnabled(false);
        jMenu7.add(jMenuItem6);

        jMenu6.add(jMenu7);
        jMenu6.add(jSeparator1);

        jMenuItem4.setText("Save");
        jMenuItem4.setEnabled(false);
        jMenu6.add(jMenuItem4);

        jMenuBar2.add(jMenu6);

        setJMenuBar(jMenuBar2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPanel_main)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPanel_main)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem1ActionPerformed
    {//GEN-HEADEREND:event_jMenuItem1ActionPerformed
        coordinator.showDatabaseConnectorFrame( true );
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void import_menuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_import_menuItemActionPerformed
    {//GEN-HEADEREND:event_import_menuItemActionPerformed
        treeTable = coordinator.importData();
        treeTable_scrollPane.setViewportView( treeTable );
    }//GEN-LAST:event_import_menuItemActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem3ActionPerformed
    {//GEN-HEADEREND:event_jMenuItem3ActionPerformed
        if ( xsdFileOpener.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION )
        {
            File file = xsdFileOpener.getSelectedFile();
            HashMap<Connection, String> connectionParameters = new HashMap<>();
            connectionParameters.put( Connection.FILE_NAME, file.getPath() );
            connectionSuccess( coordinator.connect( connectionParameters, Datasource.XML ) );
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed
    private void connectionSuccess( boolean isConnected )
    {
        String message = "Connection Failed";
        if ( isConnected )
        {
            message = "Connection Successful";
        }
        JOptionPane.showMessageDialog( null, message );
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem import_menuItem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPanel panel_left;
    private javax.swing.JPanel panel_right;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea rSyntaxTextArea1;
    private javax.swing.JSplitPane splitPanel_main;
    private org.jdesktop.swingx.JXTreeTable treeTable;
    private javax.swing.JScrollPane treeTable_scrollPane;
    private javax.swing.JFileChooser xsdFileOpener;
    // End of variables declaration//GEN-END:variables
}
