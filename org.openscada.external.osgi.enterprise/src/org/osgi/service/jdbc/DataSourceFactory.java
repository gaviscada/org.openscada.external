package org.osgi.service.jdbc;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

public abstract interface DataSourceFactory
{
    public static final String OSGI_JDBC_DRIVER_CLASS = "osgi.jdbc.driver.class";

    public static final String OSGI_JDBC_DRIVER_NAME = "osgi.jdbc.driver.name";

    public static final String OSGI_JDBC_DRIVER_VERSION = "osgi.jdbc.driver.version";

    public static final String JDBC_DATABASE_NAME = "databaseName";

    public static final String JDBC_DATASOURCE_NAME = "dataSourceName";

    public static final String JDBC_DESCRIPTION = "description";

    public static final String JDBC_NETWORK_PROTOCOL = "networkProtocol";

    public static final String JDBC_PASSWORD = "password";

    public static final String JDBC_PORT_NUMBER = "portNumber";

    public static final String JDBC_ROLE_NAME = "roleName";

    public static final String JDBC_SERVER_NAME = "serverName";

    public static final String JDBC_USER = "user";

    public static final String JDBC_URL = "url";

    public static final String JDBC_INITIAL_POOL_SIZE = "initialPoolSize";

    public static final String JDBC_MAX_IDLE_TIME = "maxIdleTime";

    public static final String JDBC_MAX_POOL_SIZE = "maxPoolSize";

    public static final String JDBC_MAX_STATEMENTS = "maxStatements";

    public static final String JDBC_MIN_POOL_SIZE = "minPoolSize";

    public static final String JDBC_PROPERTY_CYCLE = "propertyCycle";

    public abstract DataSource createDataSource ( Properties paramProperties ) throws SQLException;

    public abstract ConnectionPoolDataSource createConnectionPoolDataSource ( Properties paramProperties ) throws SQLException;

    public abstract XADataSource createXADataSource ( Properties paramProperties ) throws SQLException;

    public abstract Driver createDriver ( Properties paramProperties ) throws SQLException;
}