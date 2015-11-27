package model;

import java.io.File;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.vocab.XSDVocabulary;
import treetable.tree.node.CombinedKeyNode;
import treetable.tree.node.ForeignKeyNode;
import treetable.tree.node.NonKeyNode;
import treetable.tree.node.PrimaryKeyNode;
import treetable.tree.node.RelationSchemaNode;

public class OntologyModel implements Visitor, Serialisable
{
// -----------------------------------------------------------------------------
// Visitor Requirements
// -----------------------------------------------------------------------------

    @Override
    public void visit( RelationSchemaNode relationSchemaNode )
    {
        String className = relationSchemaNode.getValueAt( 1 ).toString();
        addClassDeclaration( className );
    }

    @Override
    public void visit( NonKeyNode nonKeyNode )
    {
        String domain_className = nonKeyNode.getParent().getValueAt( 1 ).toString();
        String propertyName = nonKeyNode.getValueAt( 1 ).toString();
        String datatypeName = nonKeyNode.getValueAt( 2 ).toString();
        addDataProperty( domain_className, propertyName, getVocabulary( datatypeName ) );
    }

    @Override
    public void visit( PrimaryKeyNode primaryKeyNode )
    {
    }

    @Override
    public void visit( ForeignKeyNode foreignKeyNode )
    {
        String domain_className = foreignKeyNode.getParent().getValueAt( 1 ).toString();
        String propertyName = foreignKeyNode.getValueAt( 1 ).toString();
        String range_className = foreignKeyNode.getValueAt( 2 ).toString();
        addObjectProperty( domain_className, propertyName, range_className );
    }

    @Override
    public void visit( CombinedKeyNode combinedKeyNodeNode )
    {
    }
// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------
    private final static String baseIRI_string = "BASE_IRI_NOT_SET";
    private static IRI baseIRI;
    private OWLDataFactory dataFactory;
    private OWLOntologyManager owlManager;
    private OWLOntology ontology;

    public OntologyModel()
    {
        baseIRI = IRI.create( baseIRI_string );

        dataFactory = OWLManager.getOWLDataFactory();

        owlManager = OWLManager.createOWLOntologyManager();
        owlManager.addIRIMapper( new AutoIRIMapper( new File( "materializedOntologies" ), true ) );

        try
        {
            ontology = owlManager.createOntology( OntologyModel.baseIRI );
        }
        catch ( OWLOntologyCreationException ex )
        {
            System.out.println( "\n>> EXCEPTION_THROWN_FROM_OntologyModel()_CONSTRUCTOR:\n" + ex + "\n" );
        }
    }

    private void addClassDeclaration( String className )
    {
        OWLClass newClass = dataFactory.getOWLClass( IRI.create( baseIRI + "#" + className ) );
        OWLDeclarationAxiom newClassDeclarationAxiom = dataFactory.getOWLDeclarationAxiom( newClass );
        owlManager.addAxiom( ontology, newClassDeclarationAxiom );
    }

    private void addObjectProperty( String domain_string, String property_name, String range_string )
    {
        OWLClass newRangeClass = dataFactory.getOWLClass( IRI.create( baseIRI + "#" + range_string ) );
        OWLObjectProperty newObjectProperty = dataFactory.getOWLObjectProperty( IRI.create( baseIRI + "#" + property_name ) );
        Set<OWLObjectPropertyDomainAxiom> allDomainAxioms = ontology.getObjectPropertyDomainAxioms( newObjectProperty );
        OWLClass newDomainClass = dataFactory.getOWLClass( IRI.create( baseIRI + "#" + domain_string ) );

        Iterator<OWLObjectPropertyDomainAxiom> PropertyDomainAxiom_itr;
        if ( ( PropertyDomainAxiom_itr = allDomainAxioms.iterator() ).hasNext() )
        {
            OWLObjectPropertyDomainAxiom propertyDomainAxiom;
            Set<OWLClass> existingDomainClasses = ( propertyDomainAxiom = PropertyDomainAxiom_itr.next() ).getClassesInSignature();

            owlManager.removeAxiom( ontology, propertyDomainAxiom );
            existingDomainClasses.add( newDomainClass );
            appendToObjectPropertyDomainAxiom( existingDomainClasses, propertyDomainAxiom, newObjectProperty );

            return;
        }

        addObjectPropertyDomainAxiom( dataFactory.getOWLClass( IRI.create( baseIRI + "#" + domain_string ) ), newObjectProperty );
        addObjectPropertyRangeAxiom( newObjectProperty, newRangeClass );
    }

    private void addDataProperty( String domain_string, String property_name, XSDVocabulary xsdVocabulary )
    {
        OWLDataProperty newDataProperty = dataFactory.getOWLDataProperty( IRI.create( baseIRI + "#" + property_name ) );
        OWLDatatype newDatatype = dataFactory.getOWLDatatype( xsdVocabulary.getIRI() );

        Set<OWLDataPropertyDomainAxiom> allDomainAxioms = ontology.getDataPropertyDomainAxioms( newDataProperty );
        OWLClass newClass = dataFactory.getOWLClass( IRI.create( baseIRI + "#" + domain_string ) );

        Iterator<OWLDataPropertyDomainAxiom> PropertyDomainAxiom_itr;
        if ( ( PropertyDomainAxiom_itr = allDomainAxioms.iterator() ).hasNext() )
        {
            OWLDataPropertyDomainAxiom propertyDomainAxiom;
            Set<OWLClass> existingDomainClasses = ( propertyDomainAxiom = PropertyDomainAxiom_itr.next() ).getClassesInSignature();

            owlManager.removeAxiom( ontology, propertyDomainAxiom );
            existingDomainClasses.add( newClass );
            appendToDataPropertyDomainAxiom( existingDomainClasses, propertyDomainAxiom, newDataProperty );

            Set<OWLDataPropertyRangeAxiom> allRangeAxioms = ontology.getDataPropertyRangeAxioms( newDataProperty );
            Iterator<OWLDataPropertyRangeAxiom> PropertyRangeAxiom_itr;
            if ( ( PropertyRangeAxiom_itr = allRangeAxioms.iterator() ).hasNext() )
            {
                OWLDataPropertyRangeAxiom propertyRangeAxiom;
                Set<OWLDatatype> existingDatatypes = ( propertyRangeAxiom = PropertyRangeAxiom_itr.next() ).getDatatypesInSignature();

                existingDatatypes.add( newDatatype );
                appendToDataPropertyRangeAxiom( propertyRangeAxiom, newDataProperty, existingDatatypes );
            }
            return;
        }

        addDataPropertyDomainAxiom( dataFactory.getOWLClass( IRI.create( baseIRI + "#" + domain_string ) ), newDataProperty );
        addDataPropertyRangeAxiom( newDataProperty, newDatatype );
    }

    private void addDataPropertyDomainAxiom( OWLClass domain_class, OWLDataProperty property )
    {
        OWLAxiom newAxiom = dataFactory.getOWLDataPropertyDomainAxiom( property, domain_class );
        owlManager.addAxiom( ontology, newAxiom );
    }

    private void addObjectPropertyDomainAxiom( OWLClass domain_class, OWLObjectProperty property )
    {
        OWLAxiom newAxiom = dataFactory.getOWLObjectPropertyDomainAxiom( property, domain_class );
        owlManager.addAxiom( ontology, newAxiom );
    }

    private void addDataPropertyRangeAxiom( OWLDataProperty property, OWLDatatype datatype )
    {
        OWLAxiom PropertyRangeAxiom = dataFactory.getOWLDataPropertyRangeAxiom( property, datatype );
        owlManager.addAxiom( ontology, PropertyRangeAxiom );
    }

    private void addObjectPropertyRangeAxiom( OWLObjectProperty property, OWLClass range_class )
    {
        OWLAxiom PropertyRangeAxiom = dataFactory.getOWLObjectPropertyRangeAxiom( property, range_class );
        owlManager.addAxiom( ontology, PropertyRangeAxiom );
    }

    private void appendToDataPropertyDomainAxiom( Set<OWLClass> classes, OWLDataPropertyDomainAxiom axiom, OWLDataProperty property )
    {
        OWLClassExpression oe = dataFactory.getOWLObjectUnionOf( classes );
        owlManager.removeAxiom( ontology, axiom );
        axiom = dataFactory.getOWLDataPropertyDomainAxiom( property, oe );
        owlManager.addAxiom( ontology, axiom );
    }

    private void appendToObjectPropertyDomainAxiom( Set<OWLClass> classes, OWLObjectPropertyDomainAxiom axiom, OWLObjectProperty property )
    {
        OWLClassExpression oe = dataFactory.getOWLObjectUnionOf( classes );
        owlManager.removeAxiom( ontology, axiom );
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom( property, oe );
        owlManager.addAxiom( ontology, axiom );
    }

    private void appendToDataPropertyRangeAxiom( OWLDataPropertyRangeAxiom axiom, OWLDataProperty property, Set<OWLDatatype> datatypes )
    {
        OWLDataRange owlDataRange = dataFactory.getOWLDataUnionOf( datatypes );
        owlManager.removeAxiom( ontology, axiom );
        axiom = dataFactory.getOWLDataPropertyRangeAxiom( property, owlDataRange );
        owlManager.addAxiom( ontology, axiom );
    }

    private XSDVocabulary getVocabulary( String vocabularyName )
    {
        switch ( vocabularyName )
        {
            case "xsd:string":
            {
                return XSDVocabulary.STRING;
            }
            case "xsd:integer":
            {
                return XSDVocabulary.INTEGER;
            }
            case "xsd:decimal":
            {
                return XSDVocabulary.DECIMAL;
            }
            case "xsd:anyURI":
            {
                return XSDVocabulary.ANY_URI;
            }
            default:
            {
                return XSDVocabulary.ANY_TYPE;
            }
        }
    }

    @Override
    public void serialise( OutputStream outputStream )
    {
        try
        {
            owlManager.saveOntology( ontology, new TurtleOntologyFormat(), outputStream );
        }
        catch ( OWLOntologyStorageException ex )
        {
            System.out.println( "\n>> EXCEPTION_THROWN_FROM_serialise()_METHOD:\n" + ex + "\n" );
        }
    }
}
