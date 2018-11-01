package guzhijistudio.transfile.utils

import org.ini4j.Ini
import java.io.File
import java.io.IOException

object Config {
    private const val CONFIG_FILE = "./transfile.ini"
    var LOADED = false
    var DEVICE_NAME = ""
    var GROUP_ADDR = ""
    var DIR = ""

    init {
        LOADED = false
        val configFile = File(CONFIG_FILE)
        if (configFile.canRead()) {
            try {
                val config = Ini(configFile)
                val section = config["transfile"]
                if (section != null) {
                    DEVICE_NAME = section["device_name"] ?: ""
                    GROUP_ADDR = section["group_addr"] ?: ""
                    DIR = section["dir"] ?: ""
                    LOADED = true
                }
            } catch (_: Exception) {
            }
        }
    }

    fun save(): Boolean {
        return try {
            val configFile = File(CONFIG_FILE)
            if (!configFile.exists())
                configFile.createNewFile()
            val config = Ini(configFile)
            val section = config["transfile"] ?: config.add("transfile")
            section["device_name"] = DEVICE_NAME
            section["group_addr"] = GROUP_ADDR
            section["dir"] = DIR
            config.store()
            LOADED = true
            true
        } catch (_: IOException) {
            false
        }
    }

}