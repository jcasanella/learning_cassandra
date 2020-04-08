package org.lean.cassandra;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Starting Cassandra Connection" );
        Cassandra cassandra = new Cassandra("127.0.0.1", 9042);

        boolean isPresent = cassandra.existKeySpace("killrvideo");
        System.out.println(isPresent);

        cassandra.close();
    }
}
