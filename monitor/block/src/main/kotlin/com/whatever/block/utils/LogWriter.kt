package com.whatever.block.utils

import com.whatever.block.BlockCanary
import com.whatever.block.SEPARATOR
import com.whatever.block.core.HandlerThreadFactory
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Locale

internal class LogWriter {

    private val SAVE_DELETE_LOCK = Any()
    private val FILE_NAME_FORMATTER = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS", Locale.US)
    private val TIME_FORMATTER = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    private val OBSOLETE_DURATION = 2 * 24 * 3600 * 1000L

    /**
     * Save log to file
     *
     * @param str block info string
     * @return log file path
     */
    fun save(str: String?): String? {
        var path: String?
        synchronized(SAVE_DELETE_LOCK) {
            path = save("looper", str)
        }
        return path
    }

    /**
     * Delete obsolete log files, which is by default 2 days.
     */
    fun cleanObsolete() {
        HandlerThreadFactory.getWriteLogThreadHandler()?.post(Runnable {
            val now = System.currentTimeMillis()
            val f: Array<File>? = BlockCanary.blockCore.getLogFiles()
            if (!f.isNullOrEmpty()) {
                synchronized(SAVE_DELETE_LOCK) {
                    for (aF in f) {
                        if (now - aF.lastModified() > OBSOLETE_DURATION) {
                            aF.delete()
                        }
                    }
                }
            }
        })
    }

    @Suppress("SameParameterValue")
    private fun save(logFileName: String, str: String?): String? {
        var path = ""
        var writer: BufferedWriter? = null
        try {
            val file: File = BlockCanary.blockCore.detectedBlockDirectory()
            val time = System.currentTimeMillis()
            path = (file.absolutePath + "/"
                    + logFileName + "-"
                    + FILE_NAME_FORMATTER.format(time) + ".log")
            val out = OutputStreamWriter(FileOutputStream(path, true), "UTF-8")
            writer = BufferedWriter(out)
            writer.write(SEPARATOR)
            writer.write("**********************")
            writer.write(SEPARATOR)
            writer.write(TIME_FORMATTER.format(time) + "(write log time)")
            writer.write(SEPARATOR)
            writer.write(SEPARATOR)
            writer.write(str)
            writer.write(SEPARATOR)
            writer.flush()
            writer.close()
            writer = null
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            try {
                writer?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return path
    }
}