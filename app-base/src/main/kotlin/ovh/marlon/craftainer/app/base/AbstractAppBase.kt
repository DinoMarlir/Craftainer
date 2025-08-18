package ovh.marlon.craftainer.app.base

import com.github.ajalt.mordant.terminal.Terminal
import ovh.marlon.craftainer.app.base.utils.Global.ascii

abstract class AbstractAppBase(val name: String) {
    val terminal = Terminal()

    abstract fun start()

    fun run() {
        terminal.println(ascii)
        start()
    }
}