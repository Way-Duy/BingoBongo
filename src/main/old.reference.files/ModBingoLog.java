package com.snagtype.modbingo;/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
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


import javax.annotation.Nonnull;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import net.minecraft.util.math.BlockPos;

import java.io.File;


public final class ModBingoLog
{
    private static final String LOGGER_PREFIX = "BMR:";
    private static final String SERVER_SUFFIX = "S";
    private static final String CLIENT_SUFFIX = "C";

    private static final Logger SERVER = LogManager.getFormatterLogger( LOGGER_PREFIX + SERVER_SUFFIX );
    private static final Logger CLIENT = LogManager.getFormatterLogger( LOGGER_PREFIX + CLIENT_SUFFIX );

    private static final String BLOCK_UPDATE = "Block Update of %s @ ( %s )";

    private static final String DEFAULT_EXCEPTION_MESSAGE = "Exception: ";

    private ModBingoLog()
    {
    }

    /**
     * Returns a {@link Logger} logger suitable for the effective side (client/server).
     *
     * @return a suitable logger instance
     */
    private static Logger getLogger()
    {
        return true ? SERVER : CLIENT;
    }

    /**
     * Indicates of the global log is enabled or disabled.
     *
     * By default it is enabled.
     *
     * @return true when the log is enabled.
     */
    public static boolean isLogEnabled()
    {
        return true;
    }

    /**
     * Logs a formatted message with a specific log level.
     *
     * This uses {@link String#format(String, Object...)} as opposed to the {@link ParameterizedMessage} to allow a more
     * flexible formatting.
     *
     * The output can be globally disabled via the configuration file.
     *
     * @param level the intended level.
     * @param message the message to be formatted.
     * @param params the parameters used for {@link String#format(String, Object...)}.
     */
    public static void log( @Nonnull final Level level, @Nonnull final String message, final Object... params )
    {
        if( ModBingoLog.isLogEnabled() )
        {
            final String formattedMessage = String.format( message, params );
            final Logger logger = getLogger();

            logger.log( level, formattedMessage );
        }
    }

    /**
     * Log an exception with a custom message formated via {@link String#format(String, Object...)}
     *
     * Similar to {@link ModBingoLog#log(Level, String, Object...)}.
     *
     * @see ModBingoLog#log(Level, String, Object...)
     *
     * @param level the intended level.
     * @param exception
     * @param message the message to be formatted.
     * @param params the parameters used for {@link String#format(String, Object...)}.
     */
    public static void log( @Nonnull final Level level, @Nonnull final Throwable exception, @Nonnull String message, final Object... params )
    {
        if( ModBingoLog.isLogEnabled() )
        {
            final String formattedMessage = String.format( message, params );
            final Logger logger = getLogger();

            logger.log( level, formattedMessage, exception );
        }
    }

    /**
     * @see ModBingoLog#log(Level, String, Object...)
     * @param format
     * @param params
     */
    public static void info( @Nonnull final String format, final Object... params )
    {
        log( Level.INFO, format, params );
    }

    /**
     * Log exception as {@link Level#INFO}
     *
     * @see ModBingoLog#log(Level, Throwable, String, Object...)
     *
     * @param exception
     */
    public static void info( @Nonnull final Throwable exception )
    {
        log( Level.INFO, exception, DEFAULT_EXCEPTION_MESSAGE );
    }

    /**
     * Log exception as {@link Level#INFO}
     *
     * @see ModBingoLog#log(Level, Throwable, String, Object...)
     *
     * @param exception
     * @param message
     */
    public static void info( @Nonnull final Throwable exception, @Nonnull final String message )
    {
        log( Level.INFO, exception, message );
    }

    /**
     * @see ModBingoLog#log(Level, String, Object...)
     * @param format
     * @param params
     */
    public static void warn( @Nonnull final String format, final Object... params )
    {
        log( Level.WARN, format, params );
    }

    /**
     * Log exception as {@link Level#WARN}
     *
     * @see ModBingoLog#log(Level, Throwable, String, Object...)
     *
     * @param exception
     */
    public static void warn( @Nonnull final Throwable exception )
    {
        log( Level.WARN, exception, DEFAULT_EXCEPTION_MESSAGE );
    }

    /**
     * Log exception as {@link Level#WARN}
     *
     * @see ModBingoLog#log(Level, Throwable, String, Object...)
     *
     * @param exception
     * @param message
     */
    public static void warn( @Nonnull final Throwable exception, @Nonnull final String message )
    {
        log( Level.WARN, exception, message );
    }

    /**
     * @see ModBingoLog#log(Level, String, Object...)
     * @param format
     * @param params
     */
    public static void error( @Nonnull final String format, final Object... params )
    {
        log( Level.ERROR, format, params );
    }

    /**
     * Log exception as {@link Level#ERROR}
     *
     * @see ModBingoLog#log(Level, Throwable, String, Object...)
     *
     * @param exception
     */
    public static void error( @Nonnull final Throwable exception )
    {
        log( Level.ERROR, exception, DEFAULT_EXCEPTION_MESSAGE );
    }

    /**
     * Log exception as {@link Level#ERROR}
     *
     * @see ModBingoLog#log(Level, Throwable, String, Object...)
     *
     * @param exception
     * @param message
     */
    public static void error( @Nonnull final Throwable exception, @Nonnull final String message )
    {
        log( Level.ERROR, exception, message );
    }

    /**
     * Log message as {@link Level#DEBUG}
     *
     * @see ModBingoLog#log(Level, String, Object...)
     * @param format
     * @param data
     */

}
