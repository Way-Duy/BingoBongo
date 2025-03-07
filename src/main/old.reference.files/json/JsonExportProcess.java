/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2015, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package export.json;


import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

//import appeng.core.logger;


/**
 * Main entry point for exporting the Json file
 *
 * makes everything threadable
 *
 * @author thatsIch
 * @version rv3 - 14.08.2015
 * @since rv3 14.08.2015
 */
public class JsonExportProcess implements Runnable
{
    private static final String FORCE_REFRESH_MESSAGE = "Force Refresh enabled. Will ignore cache and export Json content.";
    private static final String CACHE_ENABLED_MESSAGE = "Cache is enabled. Checking for new mod configurations.";
    private static final String EQUAL_CONTENT_MESSAGE = "Same mod configuration was found. Not updating Json content.";
    private static final String UNEQUAL_CONTENT_MESSAGE = "New mod configuration was found. Commencing exporting.";
    private static final String CACHE_DISABLED_MESSAGE = "Cache is disabled. Commencing exporting.";
    private static final String EXPORT_START_MESSAGE = "Item Exporting ( started )";
    private static final String EXPORT_END_MESSAGE = "Item Exporting ( ended after %s ms)";

    @Nonnull
    private final File exportDirectory;
    @Nonnull
    private final Checker<List<ModContainer>> modChecker;
    @Nonnull
    private final ExportConfig config;

    /**
     * @param exportDirectory directory where the final Json file will be exported to
     * @param config configuration to manipulate the export process
     */
    public JsonExportProcess( @Nonnull final File exportDirectory, @Nonnull final ExportConfig config )
    {
        this.exportDirectory = Preconditions.checkNotNull( exportDirectory );
        this.config = Preconditions.checkNotNull( config );

        this.modChecker = new ModListChecker( config );
    }

    /**
     * Will check and export if various config settings will lead to exporting the Json file.
     */
    @Override
    public void run()
    {
        // no priority to this thread
        Thread.yield();

        // logic when to cancel the export process
        if( this.config.isForceRefreshEnabled() )
        {
            //logger.info( FORCE_REFRESH_MESSAGE );
        }
        else
        {
            if( this.config.isCacheEnabled() )
            {
                //logger.info( CACHE_ENABLED_MESSAGE );

                final Loader loader = Loader.instance();
                final List<ModContainer> mods = loader.getActiveModList();

                if( this.modChecker.isEqual( mods ) == CheckType.EQUAL )
                {
                    //logger.info( EQUAL_CONTENT_MESSAGE );

                    return;
                }
                else
                {
                    //logger.info( UNEQUAL_CONTENT_MESSAGE );
                }
            }
            else
            {
                //logger.info( CACHE_DISABLED_MESSAGE );
            }
        }

        //logger.info( EXPORT_START_MESSAGE );
        final Stopwatch watch = Stopwatch.createStarted();

        final IForgeRegistry<Item> itemRegistry = ForgeRegistries.ITEMS;

        final Exporter exporter = new MinecraftItemJsonExporter( this.exportDirectory, itemRegistry );

        exporter.export();

        //logger.info( EXPORT_END_MESSAGE, watch.elapsed( TimeUnit.MILLISECONDS ) );
    }
}
