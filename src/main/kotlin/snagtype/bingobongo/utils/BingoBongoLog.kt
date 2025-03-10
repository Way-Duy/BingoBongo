package snagtype.bingobongo.utils

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/*
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
//import javax.annotation.Nonnull;


object BingoBongoLog {
    private const val LOGGER_PREFIX = "BMR:"
    private const val SERVER_SUFFIX = "S"
    private const val CLIENT_SUFFIX = "C"

    private val SERVER: Logger = LogManager.getFormatterLogger(
        LOGGER_PREFIX + SERVER_SUFFIX
    )
    private val CLIENT: Logger = LogManager.getFormatterLogger(
        LOGGER_PREFIX + CLIENT_SUFFIX
    )

    private const val BLOCK_UPDATE = "Block Update of %s @ ( %s )"

    private const val DEFAULT_EXCEPTION_MESSAGE = "Exception: "

    private val logger: Logger
        /**
         * Returns a [Logger] logger suitable for the effective side (client/server).
         *
         * @return a suitable logger instance
         */
        get() = if (true) SERVER else CLIENT

    val isLogEnabled: Boolean
        /**
         * Indicates of the global log is enabled or disabled.
         *
         * By default it is enabled.
         *
         * @return true when the log is enabled.
         */
        get() = true

    /**
     * Logs a formatted message with a specific log level.
     *
     * This uses [String.format] as opposed to the [ParameterizedMessage] to allow a more
     * flexible formatting.
     *
     * The output can be globally disabled via the configuration file.
     *
     * @param level the intended level.
     * @param message the message to be formatted.
     * @param params the parameters used for [String.format].
     */
    fun log(level: Level?, message: String, vararg params: Any?) {
        if (isLogEnabled) {
            val formattedMessage = String.format(message, *params)
            val logger = logger

            logger.log(level, formattedMessage)
        }
    }

    /**
     * Log an exception with a custom message formated via [String.format]
     *
     * Similar to [BingoBongoLog.log].
     *
     * @see BingoBongoLog.log
     * @param level the intended level.
     * @param exception
     * @param message the message to be formatted.
     * @param params the parameters used for [String.format].
     */
    fun log(level: Level?, exception: Throwable?, message: String, vararg params: Any?) {
        if (isLogEnabled) {
            val formattedMessage = String.format(message, *params)
            val logger = logger

            logger.log(level, formattedMessage, exception)
        }
    }

    /**
     * @see BingoBongoLog.log
     * @param format
     * @param params
     */
    fun info(format: String, vararg params: Any?) {
        log(Level.INFO, format, *params)
    }

    /**
     * Log exception as [Level.INFO]
     *
     * @see BingoBongoLog.log
     * @param exception
     */
    fun info(exception: Throwable?) {
        log(Level.INFO, exception, DEFAULT_EXCEPTION_MESSAGE)
    }

    /**
     * Log exception as [Level.INFO]
     *
     * @see BingoBongoLog.log
     * @param exception
     * @param message
     */
    fun info(exception: Throwable?, message: String) {
        log(Level.INFO, exception, message)
    }

    /**
     * @see BingoBongoLog.log
     * @param format
     * @param params
     */
    fun warn(format: String, vararg params: Any?) {
        log(Level.WARN, format, *params)
    }

    /**
     * Log exception as [Level.WARN]
     *
     * @see BingoBongoLog.log
     * @param exception
     */
    fun warn(exception: Throwable?) {
        log(Level.WARN, exception, DEFAULT_EXCEPTION_MESSAGE)
    }

    /**
     * Log exception as [Level.WARN]
     *
     * @see BingoBongoLog.log
     * @param exception
     * @param message
     */
    fun warn(exception: Throwable?, message: String) {
        log(Level.WARN, exception, message)
    }

    /**
     * @see BingoBongoLog.log
     * @param format
     * @param params
     */
    fun error(format: String, vararg params: Any?) {
        log(Level.ERROR, format, *params)
    }

    /**
     * Log exception as [Level.ERROR]
     *
     * @see BingoBongoLog.log
     * @param exception
     */
    fun error(exception: Throwable?) {
        log(Level.ERROR, exception, DEFAULT_EXCEPTION_MESSAGE)
    }

    /**
     * Log exception as [Level.ERROR]
     *
     * @see BingoBongoLog.log
     * @param exception
     * @param message
     */
    fun error(exception: Throwable?, message: String) {
        log(Level.ERROR, exception, message)
    }
    /**
     * Log message as [Level.DEBUG]
     *
     * @see BingoBongoLog.log
     * @param format
     * @param data
     */
}
