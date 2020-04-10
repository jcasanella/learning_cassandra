package org.lean.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Cluster.Builder;

import javax.xml.transform.Result;
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
        return rs.all()
                .stream()
                .anyMatch(x -> x.getString(0).equalsIgnoreCase(keySpace));
    }

    public void showTable(String keySpace, String table) {
        ResultSet rs = session.execute(String.format("USE %s", keySpace));
        if (rs.isExhausted()) {
            ResultSet rs2 = session.execute(String.format("SELECT * FROM %s", table));
            for (Row r : rs2) {
                System.out.println(r.getUUID(0) + " " + r.getTimestamp(1) + " " + r.getString(2));
            }
        }
    }
}
