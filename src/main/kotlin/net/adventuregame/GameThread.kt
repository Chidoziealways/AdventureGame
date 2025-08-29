package net.adventuregame

import java.util.concurrent.ConcurrentLinkedQueue

object GameThread {
    private val mainQueue = ConcurrentLinkedQueue<() -> Unit>()

    fun runOnMainThread(task: () -> Unit) {
        mainQueue.add(task)
    }

    fun tick() {
        while (true) {
            val task = mainQueue.poll() ?: break
            task()
        }
    }
}

val luaMainThreadQueue = ConcurrentLinkedQueue<() -> Unit>()

fun processLuaMainThreadQueue() {
    while (true) {
        val task = luaMainThreadQueue.poll() ?: break
        task()
    }
}