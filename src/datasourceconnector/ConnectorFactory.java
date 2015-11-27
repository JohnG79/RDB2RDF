package datasourceconnector;

public class ConnectorFactory
{
    public static DatasourceConnector getConnector(Datasource datasource)
    {
        switch(datasource)
        {
            case MYSQL:
            {
                return new MySQLConnector();
            }
            case ORACLE:
            {
                throw new UnsupportedOperationException(">> Oracle Connector not implemented yet");
            }
            case XML:
            {
                return new XMLConnector();
            }
            default:
            {
                throw new UnsupportedOperationException(">> Invalid connector_id passed to ConnectorFactory.getConnector(int connector_id) method");
            }
        }
    }
}
