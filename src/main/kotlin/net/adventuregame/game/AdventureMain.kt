package net.adventuregame.game

import com.chidozie.core.renderEngine.WindowManager
import com.chidozie.core.scripts.LuaEngine
import joptsimple.OptionException
import joptsimple.OptionParser
import joptsimple.OptionSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

/**
 * The Main Class For my Adventure Game. Now in KOTLIN
 * @since 0.0.1
 * @author Chidozie Derek Chidozie-Uzowulu
 */
object AdventureMain {
    private val log: Logger = LoggerFactory.getLogger(AdventureMain::class.java)
    @JvmStatic
    lateinit var window: WindowManager
        private set
    var game: AdventureGame? = null
        private set

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = OptionParser()

        parser.accepts("width", "Window Height in Pixels")
            .withRequiredArg()
            .ofType(Int::class.java)
            .defaultsTo(1280)

        parser.accepts("height", "Window height in pixels")
            .withRequiredArg()
            .ofType(Int::class.java)
            .defaultsTo(720)

        parser.accepts("help", "Show this help").forHelp()

        val opts: OptionSet
        try {
            opts = parser.parse(*args)
        } catch (e: OptionException) {
            log.error("Error: {}", e.message)
            parser.printHelpOn(System.err)
            return
        }

        if (opts.has("help")) {
            parser.printHelpOn(System.out)
            return
        }

        val width = opts.valueOf("width") as Int
        val height = opts.valueOf("height") as Int

        log.info("Launching at {}x{}\n", width, height)
        // Now pass width/height into your WindowManager or GameInit
        try {
            window = WindowManager(true, width, height)
            game = AdventureGame(window)
            game!!.run()
        } catch (e: Exception) {
            log.error("Fatal error during game startup!", e)
        }
    }
}
