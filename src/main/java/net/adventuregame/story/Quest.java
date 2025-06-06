package net.adventuregame.story;

import java.util.function.Supplier;

public class Quest {
    private final String id;
    private final String description;
    private boolean completed = false;
    private Supplier<Boolean> completionCondition;

    public Quest(String id, String description, Supplier<Boolean> completionCondition) {
        this.id = id;
        this.description = description;
        this.completionCondition = completionCondition;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        if (completed) return true;
        if (completionCondition.get()) {
            completed = true;
            return true;
        }
        return false;
    }

    public void forceComplete() {
        completed = true;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setCompletionCondition(Supplier<Boolean> condition) {
        this.completionCondition = condition;
    }
}

