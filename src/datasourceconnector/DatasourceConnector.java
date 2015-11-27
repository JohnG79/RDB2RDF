package datasourceconnector;

import java.util.ArrayList;
import java.util.HashMap;

public interface DatasourceConnector
{
    public boolean connect( HashMap<Connection, String> connectionParameters );
    
    public String getCurrentDataSchemaName();

    public ArrayList<String> getRelationSchema();

    public ArrayList<String> getPrimaryKeys( String relationSchema );

    public HashMap<String, HashMap<String, String>> getForeignKeys( String relationSchema );

    public ArrayList<String> getNonKeys( String relationSchema );
}
