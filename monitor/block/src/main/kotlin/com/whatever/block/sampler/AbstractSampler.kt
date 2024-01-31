package com.whatever.block.sampler

import com.whatever.block.BlockCanary
import com.whatever.block.DEFAULT_SAMPLE_INTERVAL
import com.whatever.block.core.HandlerThreadFactory
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 采样工作流.
 */
abstract class AbstractSampler(var mSamplerInterval: Long = DEFAULT_SAMPLE_INTERVAL) {

    protected val mShouldSampler = AtomicBoolean(false)

    abstract fun doSampler()

    private val mRunnable: Runnable = Runnable {
        doSampler()
    }

    open fun start() {
        if (mShouldSampler.get()) {
            return
        }
        mShouldSampler.set(true)
        HandlerThreadFactory.getTimerThreadHandler()?.removeCallbacks(mRunnable)
        HandlerThreadFactory.getTimerThreadHandler()
            ?.postDelayed(mRunnable, BlockCanary.getConfig().blockThreshold())
    }

    open fun stop() {
        if (!mShouldSampler.get()) {
            return
        }
        mShouldSampler.set(false)
        HandlerThreadFactory.getTimerThreadHandler()?.removeCallbacks(mRunnable)
    }

}