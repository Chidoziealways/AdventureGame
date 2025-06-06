package net.adventuregame.story;

import java.util.List;

public class Dialogue {
    private final String speaker;
    final List<String> lines;
    int currentLine = 0;

    public Dialogue(String speaker, List<String> lines) {
        this.speaker = speaker;
        this.lines = lines;
    }

    public boolean hasNext() {
        return currentLine < lines.size();
    }

    public String nextLine() {
        if (hasNext()) {
            return lines.get(currentLine++);
        }
        return null;
    }

    public void reset() {
        currentLine = 0;
    }

    public String getSpeaker() {
        return speaker;
    }
}

