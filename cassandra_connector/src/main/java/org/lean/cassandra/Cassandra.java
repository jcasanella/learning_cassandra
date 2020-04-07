package org.lean.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Cluster.Builder;

import java.util.Optional;

public class Cassandra {

    private Cluster cluster = null;
    private Session session = null;

    public Cassandra(String node, Integer port) {
        Builder b = Cluster.builder().addContactPoint(node);
        if (port != null) {
            b.withPort(port);
        }

        cluster = b.build();
        session = cluster.connect();
    }

    public void close() {
        if (session != null)
            session.close();

        if (cluster != null)
            cluster.close();
    }

    public boolean existKeySpace(String keySpace) {
        ResultSet rs = session.execute("SELECT * FROM system_schema.keyspaces;");
        Optional<Row> spaces = rs.all()
                .stream()
                .filter(x -> x.getString(0).equalsIgnoreCase(keySpace))
                .findFirst();

        return spaces.isPresent();
    }
}
