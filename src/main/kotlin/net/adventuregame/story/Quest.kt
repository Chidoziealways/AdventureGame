package net.adventuregame.story

import java.util.function.Supplier

class Quest(val id: String?, val description: String?, private var completionCondition: Supplier<Boolean>) {
    private var completed = false

    fun isCompleted(): Boolean {
        if (completed) return true
        if (completionCondition.get()) {
            completed = true
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

