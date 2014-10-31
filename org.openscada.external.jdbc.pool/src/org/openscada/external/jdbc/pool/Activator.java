/*
 * This file is part of the openSCADA project
 * Copyright (C) 2010-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.external.jdbc.pool;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator
{
    // The plug-in ID
    public static final String PLUGIN_ID = "org.openscada.external.jdbc.pool";

    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    private ServiceTracker<DataSourceFactory, DataSourceFactory> tracker;

    private final ConcurrentMap<DataSourceFactory, ServiceRegistration<DataSourceFactory>> dataSourceFactories = new ConcurrentHashMap<DataSourceFactory, ServiceRegistration<DataSourceFactory>> ();

    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        logger.info ( "{} is starting ...", PLUGIN_ID );
        this.tracker = new ServiceTracker<DataSourceFactory, DataSourceFactory> ( context, DataSourceFactory.class, null ) {
            @Override
            public DataSourceFactory addingService ( final ServiceReference<DataSourceFactory> reference )
            {
                addPoolDataSourceFactory ( this.context, reference );
                return super.addingService ( reference );
            }

            @Override
            public void removedService ( final ServiceReference<DataSourceFactory> reference, final DataSourceFactory service )
            {
                removePoolDataSourceFactory ( this.context, reference, service );
                super.removedService ( reference, service );
            }

            @Override
            public void modifiedService ( final ServiceReference<DataSourceFactory> reference, final DataSourceFactory service )
            {
                removePoolDataSourceFactory ( this.context, reference, service );
                addPoolDataSourceFactory ( this.context, reference );
                super.modifiedService ( reference, service );
            }
        };
        this.tracker.open ( true );

        logger.info ( "{} started", PLUGIN_ID );
    }

    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        logger.info ( "{} stopping", PLUGIN_ID );
        this.tracker.close ();
        // just to make sure every possible instance is removed
        for ( final ServiceRegistration<DataSourceFactory> serviceHandle : this.dataSourceFactories.values () )
        {
            if ( serviceHandle != null )
            {
                final DataSourceFactoryPool pool = (DataSourceFactoryPool)context.getService ( serviceHandle.getReference () );
                serviceHandle.unregister ();
                if ( pool != null )
                {
                    pool.dispose ();
                }
            }
        }
        this.dataSourceFactories.clear ();
        logger.info ( "{} stopped", PLUGIN_ID );
    }

    private void addPoolDataSourceFactory ( final BundleContext context, final ServiceReference<DataSourceFactory> reference )
    {
        final boolean isConnectionPool = Boolean.parseBoolean ( "" + reference.getProperty ( "isConnectionPool" ) );
        if ( isConnectionPool )
        {
            return;
        }
        this.dataSourceFactories.put ( context.getService ( reference ), createPool ( context, reference ) );
    }

    private void removePoolDataSourceFactory ( final BundleContext context, final ServiceReference<DataSourceFactory> reference, final DataSourceFactory service )
    {
        final ServiceRegistration<DataSourceFactory> serviceHandle = this.dataSourceFactories.remove ( service );
        if ( serviceHandle != null )
        {
            final DataSourceFactoryPool pool = (DataSourceFactoryPool)context.getService ( serviceHandle.getReference () );
            serviceHandle.unregister ();
            if ( pool != null )
            {
                pool.dispose ();
            }
        }
    }

    private ServiceRegistration<DataSourceFactory> createPool ( final BundleContext context, final ServiceReference<DataSourceFactory> reference )
    {

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "DataSourceFactory which provides Postgres access" );
        properties.put ( DataSourceFactory.OSGI_JDBC_DRIVER_CLASS, reference.getProperty ( DataSourceFactory.OSGI_JDBC_DRIVER_CLASS ) );
        properties.put ( DataSourceFactory.OSGI_JDBC_DRIVER_NAME, reference.getProperty ( DataSourceFactory.OSGI_JDBC_DRIVER_NAME ) + " via org.apache.tomcat.jdbc.pool" );
        properties.put ( DataSourceFactory.OSGI_JDBC_DRIVER_VERSION, reference.getProperty ( DataSourceFactory.OSGI_JDBC_DRIVER_VERSION ) );
        properties.put ( Constants.SERVICE_RANKING, Integer.getInteger ( "org.openscada.external.jdbc.pool.serviceRanking", 10 ) );
        properties.put ( "isConnectionPool", true );
        return context.registerService ( DataSourceFactory.class, new DataSourceFactoryPool ( context, reference ), properties );
    }
}
