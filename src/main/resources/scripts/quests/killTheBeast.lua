local quest = luajava.new(
        QuestClass,
        "kill_the_beast",
        "Defeat the beast in the cave.",
        luajava.createProxy("java.util.function.Supplier", {
            get = function()
                return Player.hasKilled("cave_beast")
            end
        })
)

quest:setOnCompleteRun(luajava.createProxy("java.lang.Runnable", {
    run = function()
        print("🔥 Quest Complete: kill_the_beast")
    end
}))

Quest.add(quest)