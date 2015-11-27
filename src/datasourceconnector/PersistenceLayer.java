package datasourceconnector;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import org.jdesktop.swingx.JXTreeTable;
import treetable.table.Table;
import treetable.tree.node.RelationSchemaNode;

public class PersistenceLayer
{

    DatasourceConnector connector;

    public PersistenceLayer( Datasource datasource )
    {
        connector = ConnectorFactory.getConnector( datasource );
    }

    public boolean connect( HashMap<Connection, String> connectionParameters, Datasource datasource )
    {
        return connector.connect( connectionParameters );
    }
    
    
DefaultComboBoxModel relationSchema_names;


    public JXTreeTable importData()
    {
        relationSchema_names = new DefaultComboBoxModel(new String[]{});
        
        Table treeTable = new Table( new String[]
        {
            "Data Objects", "New Terms", "Property Range"
        } );

        treeTable.setDataSchemaNodeValue( connector.getCurrentDataSchemaName() );

        ArrayList<String> relationSchemaNames = connector.getRelationSchema();
        for ( String relationSchemeName : relationSchemaNames )
        {
            RelationSchemaNode relationSchemaNode = treeTable.addRelationSchema( relationSchemeName );
            // ---------------------------------------------------------------------
            //RelationSchema_comboBox.addItem( relationSchemaNode.getValueAt( 1 ) );
            relationSchema_names.addElement( relationSchemaNode.getValueAt( 1 ) );
            
            ArrayList<String> primaryKeyNames = connector.getPrimaryKeys( relationSchemeName );
            ArrayList<String> nonKeys = connector.getNonKeys( relationSchemeName );
            HashMap<String, HashMap<String, String>> foreignKeys = connector.getForeignKeys( relationSchemeName );

            HashMap<String, HashMap<String, String>> combinedKeys = new HashMap<>();

            for ( String primaryKeyName : primaryKeyNames )
            {
                if ( foreignKeys.keySet().contains( primaryKeyName ) )
                {
                    combinedKeys.put( primaryKeyName, foreignKeys.get( primaryKeyName ) );
                    foreignKeys.remove( primaryKeyName );
                }
                else
                {
                    treeTable.addPrimaryKey( primaryKeyName );
                }
            }
            for ( String foreignKeyName : foreignKeys.keySet() )
            {
                String referencedRelationSchemeName = foreignKeys.get( foreignKeyName ).keySet().iterator().next();
                String referencedAttributeName = foreignKeys.get( foreignKeyName ).get( referencedRelationSchemeName );
                treeTable.addForeignKey( foreignKeyName, referencedRelationSchemeName, referencedAttributeName );
            }
            for ( String combinedKeyName : combinedKeys.keySet() )
            {
                String referencedRelationSchemeName = combinedKeys.get( combinedKeyName ).keySet().iterator().next();
                String referencedAttributeName = combinedKeys.get( combinedKeyName ).get( referencedRelationSchemeName );
                treeTable.addCombinedKey( combinedKeyName, referencedRelationSchemeName, referencedAttributeName );
            }
            for ( String nonPrime : nonKeys )
            {
                treeTable.addNonKey( nonPrime ).setValueAt( "xsd:string", 2 );
            }
        }

        JComboBox datatype_comboBox = new JComboBox();
        datatype_comboBox.addItem( "xsd:string" );
        datatype_comboBox.addItem( "xsd:integer" );
        datatype_comboBox.addItem( "xsd:decimal" );
        datatype_comboBox.addItem( "xsd:anyURI" );

        return treeTable.getTreeTable(new JTextField(), new JComboBox(relationSchema_names), datatype_comboBox );
    }
}
