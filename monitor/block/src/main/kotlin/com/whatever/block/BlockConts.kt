package com.whatever.block

import java.text.SimpleDateFormat
import java.util.Locale

// 默认采样间隔时间
internal const val DEFAULT_SAMPLE_INTERVAL = 300L

internal const val DEFAULT_BLOCK_THRESHOLD_MILLIS = 3000L

internal const val DEFAULT_MAX_ENTRY_COUNT = 100

internal const val SEPARATOR = "\r\n"
internal const val BLANK = " | "

const val KV = " = "

const val NEW_INSTANCE_METHOD = "newInstance: "

const val KEY_QUA = "qua"
const val KEY_MODEL = "model"
const val KEY_API = "api-level"
const val KEY_IMEI = "imei"
const val KEY_UID = "uid"
const val KEY_CPU_CORE = "cpu-core"
const val KEY_CPU_BUSY = "cpu-busy"
const val KEY_CPU_RATE = "cpu-rate"
const val KEY_TIME_COST = "time"
const val KEY_THREAD_TIME_COST = "thread-time"
const val KEY_TIME_COST_START = "time-start"
const val KEY_TIME_COST_END = "time-end"
const val KEY_STACK = "stack"
const val KEY_PROCESS = "process"
const val KEY_VERSION_NAME = "versionName"
const val KEY_VERSION_CODE = "versionCode"
const val KEY_NETWORK = "network"
const val KEY_TOTAL_MEMORY = "totalMemory"
const val KEY_FREE_MEMORY = "freeMemory"

internal val TIME_FORMATTER = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US)

// CPU
internal const val BUFFER_SIZE = 1000

internal const val MAX_ENTRY_COUNT = 10
