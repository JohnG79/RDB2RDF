package controller;

import datasourceconnector.Connection;
import datasourceconnector.Datasource;
import datasourceconnector.PersistenceLayer;
import forms.*;
import java.util.HashMap;
import org.jdesktop.swingx.JXTreeTable;

public class Coordinator
{

    private final MainFrame mainFrame;
    private final ConnectFrame connectFrame;
    private PersistenceLayer persistenceLayer;
    public Coordinator()
    {
        mainFrame = new MainFrame( this );
        connectFrame = new ConnectFrame( this );

        mainFrame.setVisible( true );
    }

    public void showDatabaseConnectorFrame( boolean visibility )
    {
        connectFrame.setVisible( visibility );
    }
    
    public boolean connect( HashMap<Connection, String> connectionParameters, Datasource datasource )
    {
        persistenceLayer = new PersistenceLayer(datasource);
        if( persistenceLayer.connect( connectionParameters, datasource ))
        {
            mainFrame.connectionSuccess();
            return true;
        }
        return false;
    }

    public JXTreeTable importData()
    {
        return persistenceLayer.importData();
    }
}
