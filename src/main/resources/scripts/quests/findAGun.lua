local Supplier = luajava.bindClass("java.util.function.Supplier")

local quest = luajava.new(
        QuestClass,
        "find_a_gun",
        "Find a Gun",
        luajava.createProxy("java.util.function.Supplier", {
            get = function()
                return Player.hasItem("pistol")
            end
        })
)

quest:setOnCompleteRun(luajava.createProxy("java.lang.Runnable", {
    run = function()
        print("🔥 Quest Complete: find_a_gun")
    end
}))

Quest.add(quest)