/*
 * This file is part of the openSCADA project
 * Copyright (C) 2010-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.external.jdbc.pool;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link DataSourceFactory} implementation for PostgreSQL
 * 
 * @author Jens Reimann
 */
public class DataSourceFactoryPool implements DataSourceFactory
{

    private final static Logger logger = LoggerFactory.getLogger ( DataSourceFactoryPool.class );

    private final BundleContext context;

    private final ServiceReference<DataSourceFactory> reference;

    private final Object lock = new Object ();

    private final Map<Properties, org.apache.tomcat.jdbc.pool.DataSource> dataSources = new ConcurrentHashMap<Properties, org.apache.tomcat.jdbc.pool.DataSource> ();

    private final Map<Properties, org.apache.tomcat.jdbc.pool.XADataSource> xaDataSources = new ConcurrentHashMap<Properties, org.apache.tomcat.jdbc.pool.XADataSource> ();

    public DataSourceFactoryPool ( final BundleContext context, final ServiceReference<DataSourceFactory> reference )
    {
        logger.debug ( "constructor () called (reference = {})", reference );
        this.context = context;
        this.reference = reference;
    }

    @Override
    public DataSource createDataSource ( final Properties props ) throws SQLException
    {
        logger.trace ( "createDataSource () called" );
        return createPooledDataSource ( props );
    }

    @Override
    public ConnectionPoolDataSource createConnectionPoolDataSource ( final Properties props ) throws SQLException
    {
        logger.trace ( "createConnectionPoolDataSource () called" );
        return createPooledDataSource ( props );
    }

    private org.apache.tomcat.jdbc.pool.DataSource createPooledDataSource ( final Properties props ) throws SQLException
    {
        org.apache.tomcat.jdbc.pool.DataSource ds;
        synchronized ( this.lock )
        {
            ds = this.dataSources.get ( props );
            if ( ds == null )
            {
                logger.trace ( "org.apache.tomcat.jdbc.pool.DataSource will be created" );
                ds = new org.apache.tomcat.jdbc.pool.DataSource ( makePoolConfiguration ( props, false ) );
                this.dataSources.put ( props, ds );
            }
        }
        return ds;
    }

    @Override
    public XADataSource createXADataSource ( final Properties props ) throws SQLException
    {
        logger.trace ( "createXADataSource () called" );
        org.apache.tomcat.jdbc.pool.XADataSource ds;
        synchronized ( this.lock )
        {
            ds = this.xaDataSources.get ( props );
            if ( ds == null )
            {
                logger.trace ( "org.apache.tomcat.jdbc.pool.XADataSource will be created" );
                ds = new org.apache.tomcat.jdbc.pool.XADataSource ( makePoolConfiguration ( props, true ) );
                this.dataSources.put ( props, ds );
            }
        }
        return ds;
    }

    @Override
    public Driver createDriver ( final Properties props ) throws SQLException
    {
        logger.trace ( "createDriver () called" );
        // createDriver will always directly call createDriver of the original dataSource
        final DataSourceFactory originalDataSource = this.context.getService ( this.reference );
        if ( originalDataSource != null )
        {
            return originalDataSource.createDriver ( props );
        }
        else
        {
            logger.warn ( "originalDataSource was null!" );
            return null;
        }
    }

    private PoolConfiguration makePoolConfiguration ( final Properties props, final boolean isXa ) throws SQLException
    {
        logger.warn ( "makePoolConfiguration () called" );
        final PoolProperties poolProperties = new PoolProperties ();

        final DataSourceFactory originalDataSourceFactory = this.context.getService ( this.reference );
        if ( originalDataSourceFactory == null )
        {
            logger.warn ( "originalDataSource was null!" );
        }
        if ( isXa )
        {
            poolProperties.setDataSource ( originalDataSourceFactory.createXADataSource ( props ) );
        }
        else
        {
            poolProperties.setDataSource ( originalDataSourceFactory.createDataSource ( props ) );
        }
        return poolProperties;
    }

    public void dispose ()
    {
        logger.debug ( "dispose () called" );
        synchronized ( this.lock )
        {
            for ( final org.apache.tomcat.jdbc.pool.DataSource ds : this.dataSources.values () )
            {
                ds.close ( true );
            }
            this.dataSources.clear ();
            for ( final org.apache.tomcat.jdbc.pool.XADataSource ds : this.xaDataSources.values () )
            {
                ds.close ( true );
            }
            this.xaDataSources.clear ();
        }
    }
}
