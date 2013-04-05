package org.davidmason.zayf.cache.neo;

import org.davidmason.zayf.cache.Mirror;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class NeoMirrorModule extends AbstractModule
{

   // FIXME get this from configuration
   private static final String DB_PATH = "/tmp/testneo4jdb/test2";

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
      System.out.println("Starting database service");
      final GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
      Runtime.getRuntime().addShutdownHook(new Thread()
      {

         @Override
         public void run()
         {
            System.out.println("Shutting down database service");
            graphDb.shutdown();
         }
      });
      return graphDb;
   }
}
