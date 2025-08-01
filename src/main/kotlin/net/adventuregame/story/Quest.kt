package net.adventuregame.story

import java.util.function.Supplier

class Quest(val id: String?, val description: String?, private var completionCondition: Supplier<Boolean>) {
    private var completed = false

    var onComplete: (() -> Unit)? = null

    fun setOnCompleteRun(callback: () -> Unit) {
        this.onComplete = callback
    }

    fun isCompleted(): Boolean {
        if (completed) return true
        if (completionCondition.get()) {
            completed = true
            onComplete?.invoke()
            return true
        }
        return false
    }

    fun forceComplete() {
        completed = true
    }

    fun setCompleted(completed: Boolean) {
        this.completed = completed
    }

    fun setCompletionCondition(condition: Supplier<Boolean>) {
        this.completionCondition = condition
    }
}

