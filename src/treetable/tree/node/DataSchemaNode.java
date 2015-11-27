package treetable.tree.node;

import model.MappingModel;
import model.OntologyModel;

public class DataSchemaNode extends treetable.tree.node.Node implements VisitorAcceptor

{
    public DataSchemaNode( Object[] objects )
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
}
