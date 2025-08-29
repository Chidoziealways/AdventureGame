package net.adventuregame.story

import java.util.function.Supplier

class Quest(val id: String?, val description: String?, private var completionCondition: Supplier<Boolean>) {
    private var completed = false

    var onComplete: (() -> Unit)? = null

    fun setOnCompleteRun(r: Runnable) {
        onComplete = { r.run() }
    }

    fun isCompleted(): Boolean {
        if (completed) return false  // already completed previously, so not “just completed”

        if (completionCondition.get()) {
            completed = true
            onComplete?.invoke() // only triggers once
            return true  // yes, just completed now
        }

        return false  // not completed yet
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

