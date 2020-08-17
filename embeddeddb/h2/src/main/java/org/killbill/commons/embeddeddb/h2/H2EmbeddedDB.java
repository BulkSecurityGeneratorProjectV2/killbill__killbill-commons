/*
 * Copyright 2010-2014 Ning, Inc.
 * Copyright 2014-2020 Groupon, Inc
 * Copyright 2020-2020 Equinix, Inc
 * Copyright 2014-2020 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.commons.embeddeddb.h2;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.h2.api.ErrorCode;
import org.h2.engine.ConnectionInfo;
import org.h2.engine.Engine;
import org.h2.engine.Session;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.Server;
import org.killbill.commons.embeddeddb.EmbeddedDB;

public class H2EmbeddedDB extends EmbeddedDB {

    private final AtomicBoolean started = new AtomicBoolean(false);

    private Server server;

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public H2EmbeddedDB() {
        this(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

    public H2EmbeddedDB(final String databaseName, final String username, final String password) {
        this(databaseName, username, password, "jdbc:h2:mem:" + databaseName + ";MODE=MYSQL;DB_CLOSE_DELAY=-1");
    }

    public H2EmbeddedDB(final String databaseName, final String username, final String password, final String jdbcConnectionString) {
        super(databaseName, username, password, jdbcConnectionString);
    }

    @Override
    public DBEngine getDBEngine() {
        return DBEngine.H2;
    }

    @Override
    public void initialize() throws IOException {
    }

    @Override
    public void start() throws IOException {
        if (started.get()) {
            throw new IOException("H2 is already running: " + jdbcConnectionString);
        }
        createDataSource();

        try {
            // Start a web server for debugging (http://127.0.0.1:8082/)
            server = Server.createWebServer(new String[]{}).start();
            logger.info(String.format("H2 started on http://127.0.0.1:8082. JDBC=%s, Username=%s, Password=%s",
                                      getJdbcConnectionString(), getUsername(), getPassword()));
        } catch (final SQLException e) {
            // H2 most likely already started (e.g. by a different pool) -- ignore
            // Note: we still want the EmbeddedDB object to be started, for a clean shutdown of the dataSource
            if (!String.valueOf(ErrorCode.EXCEPTION_OPENING_PORT_2).equals(e.getSQLState())) {
                throw new IOException(e);
            }

        }

        started.set(true);

        refreshTableNames();
    }

    @Override
    public void refreshTableNames() throws IOException {
        final String query = String.format("select table_name from information_schema.tables where table_catalog = '%s' and table_type = 'TABLE';", databaseName.toUpperCase());
        try {
            executeQuery(query, new ResultSetJob() {
                @Override
                public void work(final ResultSet resultSet) throws SQLException {
                    allTables.clear();
                    while (resultSet.next()) {
                        allTables.add(resultSet.getString(1));
                    }
                }
            });
        } catch (final SQLException e) {
            throw new IOException(e);
        }
    }

    protected void createDataSource() throws IOException {
        if (useConnectionPooling()) {
            dataSource = createHikariDataSource();
        } else {
            final JdbcConnectionPool jdbcConnectionPool = JdbcConnectionPool.create(jdbcConnectionString, username, password);
            // Default is 10, set it to 30 to match the default for org.killbill.dao.maxActive
            jdbcConnectionPool.setMaxConnections(30);
            dataSource = jdbcConnectionPool;
        }
    }

    @Override
    public DataSource getDataSource() throws IOException {
        if (!started.get()) {
            throw new IOException("H2 is not running");
        }
        return super.getDataSource();
    }

    @Override
    public void stop() throws IOException {
        if (!started.get()) {
            throw new IOException("H2 is not running");
        }
        super.stop();

        if (dataSource instanceof JdbcConnectionPool) {
            ((JdbcConnectionPool) dataSource).dispose();
        }

        if (server != null) {
            server.stop();

            // Shutdown the MVStore
            final Properties info = new Properties();
            info.setProperty("user", username);
            info.put("password", password);
            final ConnectionInfo ci = new ConnectionInfo(jdbcConnectionString, info);
            final Session session = Engine.getInstance().createSession(ci);
            if (session.getDatabase() != null) {
                session.getDatabase().shutdownImmediately();
            }
        }
        started.set(false);
        logger.info(String.format("H2 stopped on http://127.0.0.1:8082. JDBC=%s, Username=%s, Password=%s",
                                  getJdbcConnectionString(), getUsername(), getPassword()));

    }

    @Override
    public String getCmdLineConnectionString() {
        return "open " + server.getURL();
    }
}
