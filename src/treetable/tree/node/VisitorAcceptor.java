package treetable.tree.node;

public interface VisitorAcceptor
{
    public void acceptVisitor(model.MappingModel mappingModel);
    public void acceptVisitor(model.OntologyModel ontologyModel);
}
