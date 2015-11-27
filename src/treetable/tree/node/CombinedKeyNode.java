package treetable.tree.node;

import model.MappingModel;
import model.OntologyModel;

public class CombinedKeyNode extends ForeignKeyNode implements VisitorAcceptor
{

    public CombinedKeyNode( Object[] objects )
    {
        super( objects );
    }
    public CombinedKeyNode( Object[] objects, String referencedTableName, String referencedColumnName )
    {
        super( objects, referencedTableName, referencedColumnName );
    }

    @Override
    public void acceptVisitor( MappingModel mappingModel )
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void acceptVisitor( OntologyModel ontologyModel )
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    
}
