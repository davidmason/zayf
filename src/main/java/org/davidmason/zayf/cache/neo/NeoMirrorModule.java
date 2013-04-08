package org.davidmason.zayf.cache.neo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.davidmason.zayf.cache.Mirror;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class NeoMirrorModule extends AbstractModule
{

   private Logger log = Logger.getLogger(NeoMirrorModule.class);

   private static final String DEFAULT_DB_PATH = "/tmp/testneo4jdb/test2";

   @Override
   protected void configure()
   {
      // Note: mirror does not have to be a singleton as long as the database
      //       service it uses is a singleton.
      bind(Mirror.class).to(NeoMirror.class);
   }

   @Provides
   @Singleton
   GraphDatabaseService provideGraphDatabaseService()
   {
      String dbPath = getDatabasePath();
      log.info("Starting database service at " + dbPath);
      final GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
      Runtime.getRuntime().addShutdownHook(new Thread()
      {

         @Override
         public void run()
         {
            log.info("Shutting down database service");
            graphDb.shutdown();
         }
      });
      return graphDb;
   }

   private String getDatabasePath()
   {
      String dbPath;
      Properties prop = new Properties();
      try
      {
         File inFile = new File("config.properties");
         log.info("expect datbase config at: " + inFile.getAbsolutePath());
         FileInputStream inStream = new FileInputStream(inFile);
         prop.load(inStream);
         dbPath = prop.getProperty("database");
      }
      catch (IOException e)
      {
         // FIXME user should be able to configure this through the UI.
         //       Dialog should be displayed at this point to allow user to
         //       choose a location or use default.
         log.warn("Failed to load database path from config, using default", e);
         dbPath = DEFAULT_DB_PATH;
      }
      return dbPath;
   }
}
