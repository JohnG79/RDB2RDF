package datasourceconnector;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class MySQLConnector implements DatasourceConnector
{

    private java.sql.Connection connection;
    private String database_name;

    public MySQLConnector()
    {
        try
        {
            Class.forName( "com.mysql.jdbc.Driver" ).newInstance();
        }
        catch ( ClassNotFoundException | InstantiationException | IllegalAccessException ex )
        {
            System.out.println( "\n>> EXCEPTION_THROWN_FROM_MySQLConnector()_CONSTRUCTOR:\n" + ex + "\n" );
        }
    }

    @Override
    public boolean connect( HashMap<Connection, String> connectionParameters )
    {
        String host = connectionParameters.get( Connection.HOST );
        String port = connectionParameters.get( Connection.PORT );
        database_name = connectionParameters.get( Connection.DATABASE_NAME );
        String user_name = connectionParameters.get( Connection.USER_NAME );
        String password = connectionParameters.get( Connection.PASSWORD );
        try
        {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database_name + "?user=" + user_name + "&password=" + password
            );
            return true;
        }
        catch ( SQLException ex )
        {
            System.out.println( "\n>> EXCEPTION_THROWN_FROM_connect()_METHOD:\n" + ex + "\n" );
            return false;
        }
    }

    @Override
    public String getCurrentDataSchemaName()
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement( "select database()" );
            return getFirstResult( statement.executeQuery() );
        }
        catch ( SQLException ex )
        {
            System.out.println( "\n>> EXCEPTION_THROWN_FROM_getCurrentDataSchemaName()_METHOD:\n" + ex + "\n" );
            return new String();
        }
    }

    @Override
    public ArrayList<String> getRelationSchema()
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement( "select distinct table_name from information_schema.key_column_usage where table_schema = ?" );
            statement.setString( 1, database_name );
            return parseResultSet( statement.executeQuery() );
        }
        catch ( SQLException ex )
        {
            System.out.println( "\n>> EXCEPTION_THROWN_FROM_getRelationSchema()_METHOD:\n" + ex + "\n" );
            return new ArrayList<>();
        }
    }

    @Override
    public ArrayList<String> getPrimaryKeys( String relation_schema_name )
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement( "select column_name from information_schema.columns where table_name = ? and column_key = 'PRI' and table_schema = ?" );
            statement.setString( 1, relation_schema_name );
            statement.setString( 2, database_name );
            return parseResultSet( statement.executeQuery() );
        }
        catch ( SQLException ex )
        {
            System.out.println( "\n>> EXCEPTION_THROWN_FROM__ArrayList<String>_getPrimaryKeys()__METHOD:\n" + ex + "\n" );
            return new ArrayList<>();
        }
    }

    private ArrayList<String> getForeignKeyNames( String relation_schema_name )
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement( "select column_name from information_schema.key_column_usage where table_name = ? and constraint_name != 'PRIMARY' and constraint_name != column_name and table_schema = ?" );
            statement.setString( 1, relation_schema_name );
            statement.setString( 2, database_name );
            return parseResultSet( statement.executeQuery() );
        }
        catch ( SQLException ex )
        {
            System.out.println( "\n>> EXCEPTION_THROWN_FROM__ArrayList<String>_getForeignKeyNames()__METHOD:\n" + ex + "\n" );
            return new ArrayList<>();
        }
    }

    @Override
    public HashMap<String, HashMap<String, String>> getForeignKeys( String relation_schema_name )
    {
        ArrayList<String> foreignKeyNames = getForeignKeyNames( relation_schema_name );
        HashMap<String, HashMap<String, String>> foreignKeys = new HashMap<>();
        HashMap<String, String> references;
        for ( String foreignKeyName : foreignKeyNames )
        {
            references = new HashMap<>();
            try
            {
                PreparedStatement statement1 = connection.prepareStatement( "select referenced_table_name from information_schema.key_column_usage where table_name = ? and constraint_name != 'PRIMARY' and constraint_name != column_name and column_name = ? and table_schema = ?" );
                statement1.setString( 1, relation_schema_name );
                statement1.setString( 2, foreignKeyName );
                statement1.setString( 3, database_name );
                
                String referencedRelationSchemaName = getFirstResult( statement1.executeQuery() );
                PreparedStatement statement2 = connection.prepareStatement( "select referenced_column_name from information_schema.key_column_usage where table_name = ? and constraint_name != 'PRIMARY' and constraint_name != column_name and column_name = ? and table_schema = ?" );
                statement2.setString( 1, relation_schema_name );
                statement2.setString( 2, foreignKeyName );
                statement2.setString( 3, database_name );
                String referencedAttributeName = getFirstResult( statement2.executeQuery() );

                references.put( referencedRelationSchemaName, referencedAttributeName );
                foreignKeys.put( foreignKeyName, references );
            }
            catch ( SQLException ex )
            {
                System.out.println( "\n>> EXCEPTION_THROWN_FROM__HashMap<String, HashMap<String, String>>_getForeignKeys()__METHOD:\n" + ex + "\n" );
                return new HashMap<>();
            }
        }
        return foreignKeys;
    }

    @Override
    public ArrayList<String> getNonKeys( String relationSchema )
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement( "select column_name from information_schema.columns where table_name = ? and column_key != 'PRI' and column_key != 'MUL' and table_schema = ?"
            );
            statement.setString( 1, relationSchema );
            statement.setString( 2, database_name );
            return parseResultSet( statement.executeQuery() );
        }
        catch ( SQLException ex )
        {
            System.out.println( "\n>> EXCEPTION_THROWN_FROM__ArrayList<String>_getNonKeys()__METHOD:\n" + ex + "\n" );
            return new ArrayList<>();
        }
    }

    private ArrayList<String> parseResultSet( ResultSet resultSet )
    {
        ArrayList<String> results = new ArrayList<>();
        try
        {
            while ( resultSet.next() )
            {
                results.add( resultSet.getString( 1 ) );
            }
        }
        catch ( SQLException ex )
        {
            System.out.println( "\n>> EXCEPTION_THROWN_FROM__ArrayList<String>_parseResultSet()__METHOD:\n" + ex + "\n" );
        }
        return results;
    }

    private String getFirstResult( ResultSet resultSet )
    {
        try
        {
            if ( resultSet.next() )
            {
                return resultSet.getString( 1 );
            }
        }
        catch ( SQLException ex )
        {
            System.out.println( "\n>> EXCEPTION_THROWN_FROM__String_getFirstResult()__METHOD:\n" + ex + "\n" );
        }
        return "";
    }

}
