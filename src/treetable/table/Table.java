package treetable.table;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import treetable.tree.node.CombinedKeyNode;
import treetable.tree.node.DataSchemaNode;
import treetable.tree.node.ForeignKeyNode;
import treetable.tree.node.Node;
import treetable.tree.node.NonKeyNode;
import treetable.tree.node.PrimaryKeyNode;
import treetable.tree.node.RelationSchemaNode;

public class Table
{

    private final String[] columnHeadings;
    private final ArrayList<Integer> relationSchema_rowNumbers = new ArrayList<>();
    private final ArrayList<Integer> nonKey_rowNumbers = new ArrayList<>();
    private final ArrayList<Integer> foreignKey_rowNumbers = new ArrayList<>();
    private DataSchemaNode dataSchemaNode;
    private RelationSchemaNode currentRelationSchemaNode;
    private DefaultTreeTableModel tableModel;
    private JXTreeTable treeTable;
    private int treeTableRowCount = 0;

    private Table()
    {
        this.columnHeadings = null;
    }

    public Table( String[] columnHeadings )
    {
        this.columnHeadings = columnHeadings;
        this.dataSchemaNode = new DataSchemaNode( new String[]
        {
            "DATASCHEMA_NAME_NOT_SET"
        } );
        // table row 0 = data schema node (root node)
        treeTableRowCount = 1;
    }

    public Table( String[] columnHeadings, String dataSchemaName )
    {
        this.columnHeadings = columnHeadings;
        this.dataSchemaNode = new DataSchemaNode( new String[]
        {
            dataSchemaName
        } );
        // table row 0 = data schema node (root node)
        treeTableRowCount = 1;
    }

    public void setDataSchemaNodeValue( String dataSchemaName )
    {
        this.dataSchemaNode.setUserObject( new String[]
        {
            dataSchemaName
        } );
    }

    public Node getDataSchemaNode()
    {
        return dataSchemaNode;
    }

    public RelationSchemaNode addRelationSchema( String relationSchemaName )
    {
        RelationSchemaNode newRelationSchemaNode = new RelationSchemaNode( new String[]
        {
            relationSchemaName, relationSchemaName.substring( 0, 1 ).toUpperCase() + relationSchemaName.substring( 1 )
        } );
        this.dataSchemaNode.add( newRelationSchemaNode );
        currentRelationSchemaNode = newRelationSchemaNode;
        relationSchema_rowNumbers.add( treeTableRowCount );
        treeTableRowCount++;
        return newRelationSchemaNode;
    }

    public Enumeration<? extends MutableTreeTableNode> getRelationSchemaNodes()
    {
        return this.dataSchemaNode.children();
    }

    public NonKeyNode addNonKey( String string )
    {
        NonKeyNode newNonKeyNode = new NonKeyNode( new String[]
        {
            string, string, ""
        } );
        currentRelationSchemaNode.add( newNonKeyNode );
        nonKey_rowNumbers.add( treeTableRowCount );
        treeTableRowCount++;
        return newNonKeyNode;
    }

    public void addPrimaryKey( String string )
    {
        PrimaryKeyNode newPrimaryKeyNode = new PrimaryKeyNode( new String[]
        {
            string, string, ""
        } );
        currentRelationSchemaNode.add( newPrimaryKeyNode );

        treeTableRowCount++;
    }

    public CombinedKeyNode addCombinedKey( String combinedKeyName, String referencedTableName, String referencedColumnName )
    {
        CombinedKeyNode newCombinedKeyNode = new CombinedKeyNode( new String[]
        {
            combinedKeyName, combinedKeyName, ""
        }, referencedTableName, referencedColumnName );
        currentRelationSchemaNode.add( newCombinedKeyNode );
        // record table row number of each foreign key
        foreignKey_rowNumbers.add( treeTableRowCount );
        treeTableRowCount++;
        return newCombinedKeyNode;
    }

    public ForeignKeyNode addForeignKey( String foreignKeyName, String referencedTableName, String referencedColumnName )
    {
        ForeignKeyNode newForiegnKeyNode = new ForeignKeyNode( new Object[]
        {
            foreignKeyName, foreignKeyName, ""
        }, referencedTableName, referencedColumnName );
        currentRelationSchemaNode.add( newForiegnKeyNode );
        // record table row number of each foreign key
        foreignKey_rowNumbers.add( treeTableRowCount );
        treeTableRowCount++;
        return newForiegnKeyNode;
    }

    public JXTreeTable getTreeTable( JTextField termsColumn_textField, JComboBox relationSchemaComboBox, JComboBox datatypesComboBox )
    {

        this.tableModel = new DefaultTreeTableModel( dataSchemaNode, Arrays.asList( columnHeadings ) );

        this.treeTable = new JXTreeTable( tableModel )
        {

            @Override
            public TableCellEditor getCellEditor( int tree_table_row, int tree_table_column )
            {
                DefaultCellEditor newCellEditor;
                // if column = 0 OR column = 1...
                if ( tree_table_column == 1 )
                {
                    String preEditText = getValueAt( tree_table_row, tree_table_column ).toString();
                    newCellEditor = new DefaultCellEditor( termsColumn_textField );

                    if ( relationSchema_rowNumbers.contains( tree_table_row ) )
                    {
                        newCellEditor.addCellEditorListener( new CellEditorListener()
                        {
                            @Override
                            public void editingStopped( ChangeEvent e )
                            {
                                String postEditText = newCellEditor.getCellEditorValue().toString();

                                if ( !preEditText.equals( postEditText ) )
                                {
                                    DefaultComboBoxModel relationSchema_names = ( DefaultComboBoxModel ) relationSchemaComboBox.getModel();
                                    int index = relationSchema_names.getIndexOf( preEditText );
                                    if ( index != -1 )
                                    {
                                        relationSchema_names.removeElementAt( index );
                                        relationSchema_names.insertElementAt( postEditText, index );
                                    }
                                }
                                treeTable.expandAll();
                                for ( int foreignKey_rowNumber : foreignKey_rowNumbers )
                                {
                                    if ( treeTable.getValueAt( foreignKey_rowNumber, 2 ).toString().equals( preEditText ) )
                                    {
                                        treeTable.setValueAt( postEditText, foreignKey_rowNumber, 2 );
                                    }
                                }
                            }

                            @Override
                            public void editingCanceled( ChangeEvent e )
                            {
                            }
                        } );
                    }
                    // foreign key row and column 2
                    else if ( foreignKey_rowNumbers.contains( tree_table_row ) )
                    {
                        newCellEditor.addCellEditorListener( new CellEditorListener()
                        {

                            @Override
                            public void editingStopped( ChangeEvent e )
                            {
                                String postEditText = newCellEditor.getCellEditorValue().toString();
                                if ( !postEditText.equals( preEditText ) )
                                {
                                    treeTable.expandAll();
                                    for ( Integer foreignKey_rowNumber : foreignKey_rowNumbers )
                                    {
                                        if ( treeTable.getValueAt( foreignKey_rowNumber, 1 ).toString().equals( preEditText ) )
                                        {
                                            treeTable.setValueAt( postEditText, foreignKey_rowNumber, 1 );
                                        }
                                    }
                                }
                            }

                            @Override
                            public void editingCanceled( ChangeEvent e )
                            {
                            }
                        } );
                    }
                }
                else if ( foreignKey_rowNumbers.contains( tree_table_row ) )
                {
                    newCellEditor = new DefaultCellEditor( relationSchemaComboBox );
                }
                else
                {
                    newCellEditor = new DefaultCellEditor( datatypesComboBox );
                }

                newCellEditor.setClickCountToStart( 1 );
                return newCellEditor;
            }
        };

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------
        HighlightPredicate literalHighlightPredicate = (Component renderer, ComponentAdapter adapter) -> ( nonKey_rowNumbers.contains( adapter.row ) );
        class LiteralHighlighter extends ColorHighlighter
        {

            private LiteralHighlighter( HighlightPredicate highlightPredicate )
            {
                super( highlightPredicate );
            }

            @Override
            protected Component doHighlight( Component component, ComponentAdapter componentAdapter )
            {
                return component;
            }
        }
        HighlightPredicate classHighlightPredicate = (Component renderer, ComponentAdapter adapter) -> ( relationSchema_rowNumbers.contains( adapter.row ) );
        class ClassHighlighter extends ColorHighlighter
        {

            private ClassHighlighter( HighlightPredicate highlightPredicate )
            {
                super( highlightPredicate );
            }

            @Override
            protected Component doHighlight( Component component, ComponentAdapter componentAdapter )
            {
                return component;
            }
        }
        HighlightPredicate foreignKeyHighlightPredicate = (Component renderer, ComponentAdapter adapter) -> ( foreignKey_rowNumbers.contains( adapter.row ) );
        class ObjectPropertyHighlighter extends ColorHighlighter
        {

            private ObjectPropertyHighlighter( HighlightPredicate highlightPredicate )
            {
                super( highlightPredicate );
            }

            @Override
            protected Component doHighlight( Component component, ComponentAdapter componentAdapter )
            {
                return component;
            }
        }
        class AlternatingRowHighlighter extends ColorHighlighter
        {

            private AlternatingRowHighlighter( HighlightPredicate highlightPredicate )
            {
                super( highlightPredicate );
            }

            @Override
            protected Component doHighlight( Component component, ComponentAdapter componentAdapter )
            {
                component.setBackground( new Color( 245, 245, 245 ) );
                return component;
            }
        }
        class SelectedRowHighlighter extends ColorHighlighter
        {

            private SelectedRowHighlighter( HighlightPredicate highlightPredicate )
            {
                super( highlightPredicate );
            }

            @Override
            protected Component doHighlight( Component component, ComponentAdapter componentAdapter )
            {
                component.setBackground( new Color( 255, 255, 225 ) );
                return component;
            }
        }
        AlternatingRowHighlighter alternatingRowHighlighter = new AlternatingRowHighlighter( HighlightPredicate.EVEN );
        SelectedRowHighlighter selectedRowHighlighter = new SelectedRowHighlighter( HighlightPredicate.IS_SELECTED );

        class TreeIconRenderer extends DefaultTreeCellRenderer
        {

            //@Override

            @Override
            public Component getTreeCellRendererComponent( JTree jtree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus )
            {
                if ( value instanceof ForeignKeyNode )
                {
                    setLeafIcon( new ImageIcon( "C:\\Users\\John\\Documents\\NetBeans\\Projects\\RDB2RDF\\icons\\green_key_16x16.png" ) );
                }
                else if ( value instanceof RelationSchemaNode )
                {
                    setOpenIcon( new ImageIcon( "C:\\Users\\John\\Documents\\NetBeans\\Projects\\RDB2RDF\\icons\\table6.png" ) );
                    setClosedIcon( new ImageIcon( "C:\\Users\\John\\Documents\\NetBeans\\Projects\\RDB2RDF\\icons\\table6.png" ) );
                }
                else if ( row == 0 )
                {
                    setOpenIcon( new ImageIcon( "C:\\Users\\John\\Documents\\NetBeans\\Projects\\RDB2RDF\\icons\\db.png" ) );
                    setClosedIcon( new ImageIcon( "C:\\Users\\John\\Documents\\NetBeans\\Projects\\RDB2RDF\\icons\\db.png" ) );
                }
                else if ( value instanceof CombinedKeyNode )
                {
                    setLeafIcon( new ImageIcon( "C:\\Users\\John\\Documents\\NetBeans\\Projects\\RDB2RDF\\icons\\blue_key_16x16.png" ) );
                }
                else if ( !( value instanceof NonKeyNode ) )
                {
                    setLeafIcon( new ImageIcon( "C:\\Users\\John\\Documents\\NetBeans\\Projects\\RDB2RDF\\icons\\orange_key_16x16.png" ) );
                }
                else
                {
                    setLeafIcon( new ImageIcon( "C:\\Users\\John\\Documents\\NetBeans\\Projects\\RDB2RDF\\icons\\square_16x16.png" ) );
                }
                return super.getTreeCellRendererComponent( jtree, ( ( Node ) value ).getValueAt( 0 ).toString(), sel, expanded, leaf, row, hasFocus );
            }
        }
// -------------------------------------------------------------------------
// -------------------------------------------------------------------------
        this.treeTable.setHighlighters( alternatingRowHighlighter, selectedRowHighlighter );
        this.treeTable.setShowGrid(
                true, true );

        this.treeTable.setColumnControlVisible(
                true );

        this.treeTable.setRootVisible(
                true );

        this.treeTable.expandAll();
        this.treeTable.setRowHeight( 25 );
        treeTable.setTreeCellRenderer( new TreeIconRenderer() );
        return this.treeTable;
    }
}
