package model;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.OutputStream;
import treetable.tree.node.CombinedKeyNode;
import treetable.tree.node.ForeignKeyNode;
import treetable.tree.node.NonKeyNode;
import treetable.tree.node.PrimaryKeyNode;
import treetable.tree.node.RelationSchemaNode;

public class MappingModel implements Visitor, Serialisable
{

    @Override
    public void visit( RelationSchemaNode relationSchemaNode )
    {
        TRIPLES_MAP_current = model.createResource( "#TriplesMap" + tripleMapNumber );
        TRIPLES_MAP_current.addProperty( LOGCIAL_TABLE,
                                         model.createResource()
                                         .addProperty( TABLE_NAME, relationSchemaNode.getValueAt( 0 ).toString() ) );
        TRIPLES_MAP_current.addProperty( SUBJECT_MAP,
                                         SUBJECT_MAP_current = model.createResource() );
        tripleMapNumber++;
    }

    @Override
    public void visit( NonKeyNode nonKeyNode )
    {
        TRIPLES_MAP_current.addProperty( PREDICATEOBJECT_MAP,
                                         PREDICATEOBJECT_MAP_current = model.createResource() );
        PREDICATEOBJECT_MAP_current.addProperty( PREDICATE, model.createResource( model.getNsPrefixURI( vocab_NS_Prefix ) + nonKeyNode.getValueAt( 1 ).toString() ) );
        PREDICATEOBJECT_MAP_current.addProperty( OBJECT_MAP, OBJECT_MAP_current = model.createResource() );
        OBJECT_MAP_current.addProperty( COLUMN, nonKeyNode.getValueAt( 0 ).toString() );
    }

    @Override
    public void visit( PrimaryKeyNode primaryKeyNode )
    {
        SUBJECT_MAP_current.addProperty( TEMPLATE, base_NS + "/" + TRIPLES_MAP_current.getProperty( LOGCIAL_TABLE ).getProperty( TABLE_NAME ).getString() + "/{" + primaryKeyNode.getValueAt( 0 ).toString() + "}" )
                .addProperty( CLASS, model.createResource( model.getNsPrefixURI( vocab_NS_Prefix ) + TRIPLES_MAP_current.getProperty( LOGCIAL_TABLE ).getProperty( TABLE_NAME ).getString() ) );
    }

    @Override
    public void visit( ForeignKeyNode foreignKeyNode )
    {
        TRIPLES_MAP_current.addProperty( PREDICATEOBJECT_MAP,
                                         PREDICATEOBJECT_MAP_current = model.createResource() );
        PREDICATEOBJECT_MAP_current.addProperty( PREDICATE, model.createResource( model.getNsPrefixURI( vocab_NS_Prefix ) + foreignKeyNode.getValueAt( 1 ) ) )
                .addProperty( OBJECT_MAP, OBJECT_MAP_current = model.createResource() );
        OBJECT_MAP_current.addProperty( TEMPLATE, base_NS + "/" + foreignKeyNode.getReferencedRelationSchemaName() + "/{" + foreignKeyNode.getValueAt( 0 ) + "}" );
    }

    @Override
    public void visit( CombinedKeyNode combinedKeyNodeNode )
    {
              if ( SUBJECT_MAP_current.getProperty( CLASS ) != null )
        {
            SUBJECT_MAP_current.getProperty( CLASS ).remove();
        }

        String propertyString;
        if ( SUBJECT_MAP_current.getProperty( TEMPLATE ) != null )
        {
            propertyString = SUBJECT_MAP_current.getProperty( TEMPLATE ).getString();
        }
        else
        {
            propertyString = base_NS;
        }
        if ( SUBJECT_MAP_current.getProperty( TEMPLATE ) != null )
        {
            SUBJECT_MAP_current.getProperty( TEMPLATE ).remove();
        }
        propertyString = propertyString.concat( "/" + combinedKeyNodeNode.getReferencedRelationSchemaName() + "={" + combinedKeyNodeNode.getReferencedRelationSchemaName() + "}" );
        SUBJECT_MAP_current.addProperty( TEMPLATE, propertyString );
    }

    @Override
    public void serialise( OutputStream outputStream )
    {
        model.write( outputStream, "TTL", "/" );
    }

// -----------------------------------------------------------------------------   
// -----------------------------------------------------------------------------
    private final Model model;

    private final String base_NS;
    private final String base_NS_Prefix;
    private final String vocab_NS;
    private final String vocab_NS_Prefix;

    private final Property LOGCIAL_TABLE;
    private final Property TABLE_NAME;
    private final Property SUBJECT_MAP;
    private final Property TEMPLATE;
    private final Property CLASS;
    private final Property PREDICATEOBJECT_MAP;
    private final Property PREDICATE;
    private final Property OBJECT_MAP;
    private final Property COLUMN;    

    private int tripleMapNumber;

    private Resource TRIPLES_MAP_current;
    private Resource SUBJECT_MAP_current;
    private Resource PREDICATEOBJECT_MAP_current;
    private Resource OBJECT_MAP_current;    
    
    public MappingModel()
    {
        model = ModelFactory.createDefaultModel();
        tripleMapNumber = 1;
        base_NS = "BASE_NS_NOT_SET";
        base_NS_Prefix = "BNNS";
        vocab_NS = "VOCAB_NS_NOT_SET";
        vocab_NS_Prefix = "VNNS";
        
        model.setNsPrefix( vocab_NS_Prefix, vocab_NS );

        LOGCIAL_TABLE = model.createProperty( model.getNsPrefixURI( this.vocab_NS_Prefix ), "logicalTable" );
        TABLE_NAME = model.createProperty( model.getNsPrefixURI( this.vocab_NS_Prefix ), "tableName" );
        SUBJECT_MAP = model.createProperty( model.getNsPrefixURI( this.vocab_NS_Prefix ), "subjectMap" );
        TEMPLATE = model.createProperty( model.getNsPrefixURI( this.vocab_NS_Prefix ), "template" );
        CLASS = model.createProperty( model.getNsPrefixURI( this.vocab_NS_Prefix ), "class" );
        PREDICATEOBJECT_MAP = model.createProperty( model.getNsPrefixURI( this.vocab_NS_Prefix ), "predicateObjectMap" );
        PREDICATE = model.createProperty( model.getNsPrefixURI( this.vocab_NS_Prefix ), "predicate" );
        OBJECT_MAP = model.createProperty( model.getNsPrefixURI( this.vocab_NS_Prefix ), "objectMap" );
        COLUMN = model.createProperty( model.getNsPrefixURI( this.vocab_NS_Prefix ), "column" );
    }
}
