package treetable.tree.node;

import model.MappingModel;
import model.OntologyModel;

public class PrimaryKeyNode extends treetable.tree.node.ChildNode implements VisitorAcceptor
{

    public PrimaryKeyNode( Object[] objects )
    {
        super( objects );
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
    
    @Override
    public boolean isEditable( int column )
    {
        return column != 0 && column != 2;
    }
    
}
