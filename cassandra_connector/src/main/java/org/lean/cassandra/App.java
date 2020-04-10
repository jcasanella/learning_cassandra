package org.lean.cassandra;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Starting Cassandra Connection" );
        Cassandra cassandra = new Cassandra("127.0.0.1", 9042);

        String keySpace = "killrvideo";

        if (cassandra.existKeySpace(keySpace)) {
            cassandra.showTable(keySpace, "videos");
        } else {
            System.out.println(String.format("%s keySpace not present", keySpace));
        }


        cassandra.close();
    }
}
