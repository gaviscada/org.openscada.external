/*
 * This file is part of the openSCADA project
 * Copyright (C) 2010-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.external.h2;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link DataSourceFactory} implementation for PostgreSQL
 * 
 * @author Jens Reimann
 */
public class DataSourceFactoryImpl implements DataSourceFactory
{

    private final static Logger logger = LoggerFactory.getLogger ( DataSourceFactoryImpl.class );

    private final org.h2.Driver driver = new org.h2.Driver ();

    private static abstract class Property<T extends JdbcDataSource>
    {
        public abstract void apply ( T dataSource, Object value ) throws SQLException;
    }

    private static abstract class StringProperty<T extends JdbcDataSource> extends Property<T>
    {
        @Override
        public void apply ( final T dataSource, final Object value ) throws SQLException
        {
            applyString ( dataSource, value != null ? value.toString () : null );
        }

        protected abstract void applyString ( T dataSource, String string ) throws SQLException;
    }

    private static abstract class IntegerProperty<T extends JdbcDataSource> extends Property<T>
    {
        @Override
        public void apply ( final T dataSource, final Object value ) throws SQLException
        {
            if ( value == null )
            {
                applyInteger ( dataSource, null );
            }
            else if ( value instanceof Number )
            {
                applyInteger ( dataSource, ( (Number)value ).intValue () );
            }
            else
            {
                try
                {
                    applyInteger ( dataSource, Integer.parseInt ( value.toString () ) );
                }
                catch ( final Exception e )
                {
                    throw new SQLException ( String.format ( "Unable to convert property value (%s) to integer", value ), e );
                }
            }

        }

        protected abstract void applyInteger ( T dataSource, Integer string ) throws SQLException;
    }

    private final Map<String, Property<JdbcDataSource>> properties = new HashMap<String, Property<JdbcDataSource>> ();

    private final Map<String, Property<JdbcDataSource>> poolProperties = new HashMap<String, Property<JdbcDataSource>> ();

    public DataSourceFactoryImpl ()
    {
        this.properties.put ( "applicationName", new StringProperty<JdbcDataSource> () {
            @Override
            protected void applyString ( final JdbcDataSource dataSource, final String value ) throws SQLException
            {
                dataSource.setDescription(value);
            }
        } );
        this.properties.put ( "loginTimeout", new IntegerProperty<JdbcDataSource> () {
            @Override
            protected void applyInteger ( final JdbcDataSource dataSource, final Integer value ) throws SQLException
            {
                if ( value != null )
                {
                    dataSource.setLoginTimeout ( value );
                }
            }
        } );
        this.properties.put ( "password", new StringProperty<JdbcDataSource> () {
            @Override
            protected void applyString ( final JdbcDataSource dataSource, final String value ) throws SQLException
            {
                dataSource.setPassword ( value );
            }
        } );
        this.properties.put ( "user", new StringProperty<JdbcDataSource> () {
            @Override
            protected void applyString ( final JdbcDataSource dataSource, final String value ) throws SQLException
            {
                dataSource.setUser ( value );
            }
        } );
    }

    @Override
    public DataSource createDataSource ( final Properties paramProperties ) throws SQLException
    {
        final JdbcDataSource dataSource = new JdbcDataSource ();

        setProperties ( dataSource, paramProperties, this.properties );

        return dataSource;
    }

    @Override
    public ConnectionPoolDataSource createConnectionPoolDataSource ( final Properties paramProperties ) throws SQLException
    {
        final JdbcDataSource dataSource = new JdbcDataSource ();

        setProperties ( dataSource, paramProperties, this.properties );
        setProperties ( dataSource, paramProperties, this.poolProperties );

        return dataSource;
    }

    @Override
    public XADataSource createXADataSource ( final Properties paramProperties ) throws SQLException
    {
        final JdbcDataSource dataSource = new JdbcDataSource ();

        setProperties ( dataSource, paramProperties, this.properties );

        return dataSource;
    }

    private <T extends JdbcDataSource> void setProperties ( final T dataSource, final Properties properties, final Map<String, Property<T>> handlers ) throws SQLException
    {
        for ( final Map.Entry<Object, Object> entry : properties.entrySet () )
        {
            logger.trace ( "Setting property - key: {}, value: {}", entry.getKey (), entry.getValue () );

            if ( entry.getKey () == null )
            {
                continue;
            }

            final Property<T> prop = handlers.get ( entry.getKey () );
            if ( prop == null )
            {
                logger.debug ( "Skipping property '{}' for now", entry.getKey () );
                continue;
            }

            prop.apply ( dataSource, entry.getValue () );
        }
    }

    @Override
    public Driver createDriver ( final Properties paramProperties ) throws SQLException
    {
        return this.driver;
    }

}
