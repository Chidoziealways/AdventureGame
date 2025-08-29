local mobPaths = {}
local pathCooldowns = {}
local PATH_RECALCULATE_DELAY = 1.0

Entity.on {
    type = "HostileMob",
    --UpdateAI
    updateAI = function(mob)
        --print("Updating AI")
        local enemies = mob:getNearbyEnemies(500)
        local target = enemies[1]
        if not target then
            mobPaths[mob:getID()] = nil
            pathCooldowns[mob:getID()] = nil
            return
        end

        local currentTime = os.clock()
        local currentPos = mob:getPosition()
        local targetPos = target:getPosition()

        local currentPath = mobPaths[mob:getID()]
        local lastPathTime = pathCooldowns[mob:getID()] or 0

        -- Recalculate only if enough time has passed or path is invalid
        --print("Checking if path should be recalculated")
        --print("Current Path:", currentPath)
        --print("Target Position:", targetPos)
        --print("Last Path Time:", lastPathTime, "Current Time:", currentTime)
        --print("Time delta:", currentTime - lastPathTime, "Delay:", PATH_RECALCULATE_DELAY)
        if (not currentPath or not posEquals(currentPath.targetPos, targetPos)) and (currentTime - lastPathTime > PATH_RECALCULATE_DELAY) then
            pathCooldowns[mob:getID()] = currentTime
          --  print("Invoking findPathAsync...")
            mob:findPathAsync(currentPos, targetPos, function(path)
            --    print("Before anything")
              --  print("Finding Path Async")
                if path and #path > 0 then
                    -- Convert Lua table to Java ArrayList
                    local success, jList = pcall(function()
                        return luajava.newInstance("java.util.ArrayList")
                    end)

                    if success then
                --        print("jList created:", jList)
                    else
                  --      print("Error creating jList:", jList)
                    end

                    for i, vec in ipairs(path) do
                        if vec and vec.x and vec.y and vec.z then
                            local javaVec = luajava.new(Vector3f, vec.x, vec.y, vec.z)
                            jList:add(javaVec)
                    --        print("✅ Added Vector3f(" .. vec.x .. ", " .. vec.y .. ", " .. vec.z .. ") to list")
                        else
                      --      print("❌ Invalid vec at index " .. i .. ": " .. tostring(vec))
                        end
                    end

                    --print("SETTING mobPaths", mob:getID(), "->", path)
                    mobPaths[mob:getID()] = {
                        path = path,
                        targetPos = targetPos,
                        stepIndex = 1
                    }
                    --print("MobPaths: " ..mobPaths)
                    mob:setPath(jList)
                    mob:setTarget(target)
                    --print("✅ Successfully called mob:setPath with " .. jList:size() .. " nodes")
                    --print("Path Found")
                else
                    -- Path failed (no path found)
                    --print("No Path Found")
                    mobPaths[mob:getID()] = nil
                end
                --print("Found Async Path")
            end)
            --print("Returning waiting for Async Result")
            return -- Don't continue, wait for async result
        end

        if distance(currentPos, target:getPosition()) <= 4.0 then
            mob:attack(target)
        end

        --print("Finished Updating AI")
    end,
    --TickAI
    tickAI = function(mob)
        --print("GETTING mobPaths", mob:getID(), "->", mobPaths[mob:getID()])
        local currentPath = mobPaths[mob:getID()]
        if currentPath == nil then
        else
          --  print("CurrentPath: " .. tostring(currentPath))
        end
        if not currentPath or not currentPath.path then
            return
        else
          --  print("CurrentPath is not null")
        end

        --print("Hello")

        local step = currentPath.stepIndex
        --print("Step: ")
        local path = currentPath.path
        --print("Path: ")
        local nextPos = path[step]
        --print("NextPos: ")
        local currentPos = mob:getPosition()
        --print("CurrentPos: ")

        if nextPos then
            --print("About to MoveTowards")
            --print("nextPos:", nextPos.x, nextPos.y, nextPos.z)
            local nextPosVec = luajava.new(Vector3f, nextPos.x, nextPos.y, nextPos.z)
            --print("Constructed Vector3f:", nextPosVec)
            -- safety: avoid normalizing a zero vector
            if distance(currentPos, nextPosVec) < 0.05 then
                --print("Already at target — skipping move")
            else
                mob:moveTowards(nextPosVec)
                --print("TickAI: Moving to", nextPos)
            end

            if distance(currentPos, nextPos) < 0.1 then
                currentPath.stepIndex = step + 1

                if currentPath.stepIndex > #currentPath.path then
                    mobPaths[mob:getID()] = null
                end
            end
        else
            --print("TickAI: No next position")
        end
    end,
    --OnDeath
    onDeath = function(mob)
        local id = mob:getID()
        mobPaths[id] = nil
        pathCooldowns[id] = nil
        print("☠️ Mob " .. tostring(id) .. " died. Cleaned up paths.")
    end,
    --OnAttack
    onAttack = function(target, mob)
        local pos = mob:getPosition()
        local targetPos = target:getPosition()
        local damage = 5

        print("target object:", target)
        print("target.takeDamage:", target.takeDamage)

        if distance(pos, targetPos) <= 4.0 then
            print("mob.get:", mob.get)
            target:takeDamage(damage, mob:get())
        end
    end
}
