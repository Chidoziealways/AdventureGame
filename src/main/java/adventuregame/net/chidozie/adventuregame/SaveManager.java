package adventuregame.net.chidozie.adventuregame;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Executor;

public class SaveManager {
    private final DataFixer dataFixer;

    public SaveManager() {
        DataFixerBuilder builder = new DataFixerBuilder(1);
        builder.addSchema(1, Schema::new); // Example schema
        DataFixerBuilder dataFixerBuilder = new DataFixerBuilder(20);
        this.dataFixer = builder.build((Executor) dataFixerBuilder.build(new Executor() {
            @Override
            public void execute(@NotNull Runnable command) {

            }
        }));
    }

    public void savePlayer(Player player, String currentScreen, Map<String, Object> screenState) {
        PlayerState playerState = new PlayerState(player, currentScreen, screenState);
        JsonOps ops = JsonOps.INSTANCE;
        Dynamic<CompoundTag> dynamic = playerState.serialize(ops, player);

        File saveDir = new File("Saves");
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        File file = new File(saveDir, "playerState.nbt");
        try {
            CompoundTag compound = dynamic.getValue();
            NBTUtil.write(compound, file);
            System.out.println("Player state saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }

