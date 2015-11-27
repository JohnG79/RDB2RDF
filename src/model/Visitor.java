package model;

public interface Visitor
{
    public abstract void visit(treetable.tree.node.RelationSchemaNode relationSchemaNode);
    public abstract void visit(treetable.tree.node.NonKeyNode nonKeyNode);
    public abstract void visit(treetable.tree.node.PrimaryKeyNode primaryKeyNode);
    public abstract void visit(treetable.tree.node.ForeignKeyNode foreignKeyNode);
    public abstract void visit(treetable.tree.node.CombinedKeyNode combinedKeyNodeNode);  

}
