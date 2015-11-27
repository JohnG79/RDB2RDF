package datasourceconnector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPathExpressionException;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLConnector implements DatasourceConnector
{

    private String dataSchema;
    private final ArrayList<String> relationSchema;
    private final HashMap<String, ArrayList<String>> relationSchema_attributes;
    private final HashMap<String, ArrayList<String>> relationSchema_primaryKeys;
    private final HashMap<String, HashMap<String, HashMap<String, String>>> relationSchema_foreignKeys;
    private Document document;
    private final DOMParser parser;

    public XMLConnector()
    {
        dataSchema = "";
        relationSchema = new ArrayList<>();
        relationSchema_attributes = new HashMap<>();
        relationSchema_foreignKeys = new HashMap<>();
        relationSchema_primaryKeys = new HashMap<>();

        parser = new DOMParser();
    }

    private void traverse( Node node ) throws XPathExpressionException
    {
        int type = node.getNodeType();

        if ( type == Node.ELEMENT_NODE )
        {
            String node_name = node.getNodeName();
            switch ( node_name )
            {
                case "xsd:element":
                {
                    process_element_subTree( node );
                    break;
                }
                case "xsd:keyref":
                {
                    process_keyref_subTree( node );
                    break;
                }
                case "xsd:key":
                {
                    process_key_subTree( node );
                    break;
                }
            }
        }
        NodeList node_children = node.getChildNodes();
        if ( node_children != null )
        {
            for ( int i = 0; i < node_children.getLength(); i++ )
            {
                traverse( node_children.item( i ) );
            }
        }
    }

    private String getValue( NamedNodeMap node_map, String node_name )
    {
        return node_map.getNamedItem( node_name ).getNodeValue();
    }

    private void process_key_subTree( Node node )
    {
        String primary_key_name;
        String relation_schema_name;

        NamedNodeMap node_map = node.getAttributes();
        String node_value = getValue( node_map, "name" );
        relation_schema_name = node_value.substring( 2, node_value.length() );

        Node child_node = node.getChildNodes().item( 3 );

        node_map = child_node.getAttributes();
        node_value = getValue( node_map, "xpath" );
        primary_key_name = node_value.substring( node_value.indexOf( "@" ) + 1, node_value.length() );

        if ( this.relationSchema_primaryKeys.get( relation_schema_name ) == null )
        {
            ArrayList<String> primary_key_names = new ArrayList<>();
            primary_key_names.add( primary_key_name );
            this.relationSchema_primaryKeys.put( relation_schema_name, primary_key_names );
        }
        else
        {
            this.relationSchema_primaryKeys.get( relation_schema_name ).add( primary_key_name );
        }

        child_node = node.getChildNodes().item( 5 );
        if ( child_node != null )
        {
            child_node = node.getChildNodes().item( 5 );

            node_map = child_node.getAttributes();
            node_value = getValue( node_map, "xpath" );
            primary_key_name = node_value.substring( node_value.indexOf( "@" ) + 1, node_value.length() );

            this.relationSchema_primaryKeys.get( relation_schema_name ).add( primary_key_name );
        }
    }

    private void process_keyref_subTree( Node node )
    {
        String foreign_key_name;
        String referenced_table_name;
        String referenced_column_name;

        NamedNodeMap node_map = node.getAttributes();
        String node_value = getValue( node_map, "refer" );
        referenced_table_name = node_value.substring( 5, node_value.length() );

        node_value = getValue( node_map, "name" );
        referenced_column_name = node_value.substring( node_value.indexOf( "_" ) + 1, node_value.length() );

        HashMap<String, String> fk_references = new HashMap<>();
        fk_references.put( referenced_table_name, referenced_column_name );

        Node node_child = node.getChildNodes().item( 3 );
        node_map = node_child.getAttributes();
        node_value = getValue( node_map, "xpath" );
        foreign_key_name = node_value.substring( node_value.indexOf( "@" ) + 1, node_value.length() );

        node_child = node.getChildNodes().item( 1 );
        node_map = node_child.getAttributes();
        node_value = getValue( node_map, "xpath" );
        String relation_name = node_value.substring( node_value.lastIndexOf( "/" ) + 1, node_value.length() );

        if(this.relationSchema_foreignKeys.get( relation_name ) == null)
        {
            HashMap<String, HashMap<String, String>> foreignKey = new HashMap<>();
            foreignKey.put( foreign_key_name, fk_references );
            this.relationSchema_foreignKeys.put( relation_name, foreignKey );
        }
        else
        {
            this.relationSchema_foreignKeys.get( relation_name ).put( foreign_key_name, fk_references );
        }
    }

    private void process_element_subTree( Node node )
    {
        NamedNodeMap node_map = node.getAttributes();
        String node_name;
        String node_value;
        if ( node_map.getLength() == 1 )
        {
            node_name = node_map.item( 0 ).getNodeName();
            if ( node_name.equals( "name" ) )
            {
                dataSchema = node_map.item( 0 ).getNodeValue();
            }
        }
        else if ( node_map.getLength() == 3 )
        {
            for ( int index = 0; index < node_map.getLength(); index++ )
            {
                node_name = node_map.item( index ).getNodeName();
                if ( node_name.equals( "name" ) )
                {
                    node_value = node_map.item( index ).getNodeValue();
                    relationSchema.add( node_value );
                    NodeList node_children = node.getChildNodes();
                    NodeList node_grandchildren;
                    if ( node_children != null )
                    {
                        for ( int i = 0; i < node_children.getLength(); i++ )
                        {
                            node_grandchildren = node_children.item( i ).getChildNodes();
                            if ( node_grandchildren != null && node_grandchildren.getLength() != 0 )
                            {
                                process_attribute_element( node_value, node_grandchildren );
                            }
                        }
                    }
                }
            }
        }
    }

    private void process_attribute_element( String relation_schema_name, NodeList nodes )
    {
        Node node;
        int node_type;
        ArrayList<String> attribute_list = new ArrayList<>();
        for ( int i = 0; i < nodes.getLength(); i++ )
        {
            node = nodes.item( i );
            node_type = node.getNodeType();
            if ( node_type == Node.ELEMENT_NODE )
            {
                NamedNodeMap node_map = nodes.item( i ).getAttributes();
                String node_name;
                for ( int j = 0; j < node_map.getLength(); j++ )
                {
                    node_name = node_map.item( j ).getNodeName();
                    if ( node_name.equals( "name" ) )
                    {
                        String attribute_name = node_map.item( j ).getNodeValue();
                        attribute_list.add( attribute_name );
                    }
                }
            }
        }
        this.relationSchema_attributes.put( relation_schema_name, attribute_list );
    }

    public String getDataSchema()
    {
        return dataSchema;
    }

    @Override
    public ArrayList<String> getRelationSchema()
    {
        return relationSchema;
    }

    public ArrayList<String> getAttributes( String table )
    {
        if ( relationSchema_attributes.get( table ) != null )
        {
            return relationSchema_attributes.get( table );
        }
        else
        {
            return new ArrayList<>();
        }
    }

    @Override
    public HashMap<String, HashMap<String, String>> getForeignKeys( String table )
    {

        if ( relationSchema_foreignKeys.get( table ) != null )
        {
            return relationSchema_foreignKeys.get( table );
        }
        else
        {
            return new HashMap<>();
        }
    }

    @Override
    public ArrayList<String> getPrimaryKeys( String table )
    {

        if ( relationSchema_primaryKeys.get( table ) != null )
        {
            return relationSchema_primaryKeys.get( table );
        }
        else
        {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean connect( HashMap<Connection, String> connectionParameters )
    {
        try
        {
            String file_name = connectionParameters.get( Connection.FILE_NAME );
            parser.parse( file_name );
            document = parser.getDocument();
            traverse( document );
            return true;
        }
        catch ( SAXException | IOException | XPathExpressionException ex )
        {
            Logger.getLogger( XMLConnector.class.getName() ).log( Level.SEVERE, null, ex );
        }
        return false;
    }

    @Override
    public String getCurrentDataSchemaName()
    {
        return dataSchema;
    }

    @Override
    public ArrayList<String> getNonKeys( String relationSchema )
    {
        ArrayList<String> nonKey_attributes = getAttributes( relationSchema );

        ArrayList<String> primaryKey_attributes = getPrimaryKeys( relationSchema );
        Set<String> foreignKey_attributes = getForeignKeys( relationSchema ).keySet();

        for ( String pk : primaryKey_attributes )
        {
            nonKey_attributes.remove( pk );
        }
        for ( String fk : foreignKey_attributes )
        {
            nonKey_attributes.remove( fk );
        }
        return nonKey_attributes;
    }
}
