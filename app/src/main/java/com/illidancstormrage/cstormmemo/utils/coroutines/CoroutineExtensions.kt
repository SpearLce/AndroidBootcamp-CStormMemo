package com.illidancstormrage.cstormmemo.utils.coroutines

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun LifecycleCoroutineScope.launchAtIo(block: suspend () -> Unit): Job {
    return launch(Dispatchers.IO) {
        block()
    }
}

fun LifecycleCoroutineScope.launchAtMain(block: suspend () -> Unit): Job {
    return launch(Dispatchers.Main) {
        block()
    }
}

fun CoroutineScope.launchAtIo(block: suspend () -> Unit): Job {
    return launch(Dispatchers.IO) {
        block()
    }
}

fun CoroutineScope.launchAtMain(block: suspend () -> Unit): Job {
    return launch(Dispatchers.Main) {
        block()
    }
}
