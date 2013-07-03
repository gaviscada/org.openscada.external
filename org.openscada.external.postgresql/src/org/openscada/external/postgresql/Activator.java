/*
 * This file is part of the openSCADA project
 * Copyright (C) 2010-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.external.postgresql;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jdbc.DataSourceFactory;
import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator
{
    // The plug-in ID
    public static final String PLUGIN_ID = "org.openscada.external.postgresql";

    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    private DataSourceFactoryImpl dataSourceFactory;

    private ServiceRegistration<?> serviceHandle;

    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        logger.info ( "{} is starting ...", PLUGIN_ID );
        this.dataSourceFactory = new DataSourceFactoryImpl ();

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( Constants.SERVICE_VENDOR, "openSCADA.org" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "DataSourceFactory which provides Postgres access" );
        properties.put ( DataSourceFactory.OSGI_JDBC_DRIVER_CLASS, Driver.class.getName () );
        properties.put ( DataSourceFactory.OSGI_JDBC_DRIVER_NAME, "Postgres JDBC Driver" );
        properties.put ( DataSourceFactory.OSGI_JDBC_DRIVER_VERSION, "9.1-901.jdbc3" );
        this.serviceHandle = context.registerService ( DataSourceFactory.class, this.dataSourceFactory, properties );

        logger.info ( "{} started", PLUGIN_ID );
    }

    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        logger.info ( "{} stopping", PLUGIN_ID );
        if ( this.serviceHandle != null )
        {
            this.serviceHandle.unregister ();
            this.serviceHandle = null;
        }
        this.dataSourceFactory = null;
        logger.info ( "{} stopped", PLUGIN_ID );
    }

}
