package ovh.marlon.craftainer.app.base.test

import ovh.marlon.craftainer.app.base.AbstractAppBase

class Application: AbstractAppBase("Test") {

    override fun start() {
        terminal.println("Your application starts here!")
    }
}