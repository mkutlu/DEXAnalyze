package com.aarw.dexanalyze.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

object TestDispatchers {
    fun createTestDispatcher(): TestDispatcher = StandardTestDispatcher()
}
