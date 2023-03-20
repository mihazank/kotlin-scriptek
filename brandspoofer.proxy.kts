import me.marvin.proxy.utils.*

import me.marvin.proxy.networking.*
import me.marvin.proxy.networking.packet.*
import me.marvin.proxy.utils.*
import me.marvin.proxy.utils.ByteBufUtils.*

import io.netty.buffer.*
import io.netty.channel.*

import com.google.gson.*

import java.awt.*
import java.io.*
import java.nio.charset.*
import java.net.*
import java.net.http.*
import java.security.*
import java.util.*

object Constants {
    /**
     * Ha Unit-ot returnol, akkor nem írja felül a brandet
     * Ha null-t returnol, akkor nem küldi el a brandet
     * Ha string-et returnol, akkor a string-et küldi el mint brandet
     */
    val CLIENT_BRAND: Any? = "vanilla"
}

object Rewriter : PacketListener {
    override fun priority(): Byte = 0

    override fun handle(type: PacketType, buf: ByteBuf, send: Channel, recv: ChannelHandlerContext, version: Version): Tristate {
        if (type == PacketTypes.Play.Client.PLUGIN_MESSAGE) {
            IndexRollback.readerManual(buf).use {
                val channel = readString(buf)

                if (channel == "minecraft:brand") {
                    if (Constants.CLIENT_BRAND is Unit) {
                        return Tristate.NOT_SET
                    } else if (Constants.CLIENT_BRAND is String) {
                        val brand = readString(buf)

                        if (brand == Constants.CLIENT_BRAND) {
                            return Tristate.NOT_SET
                        } else {
                            val rewritten = Unpooled.buffer()
                            writeVarInt(buf, type.id(version))
                            writeString(buf, channel)
                            writeString(buf, Constants.CLIENT_BRAND)
                            recv.writeAndFlush(rewritten)
                            return Tristate.TRUE
                        }
                    } else if (Constants.CLIENT_BRAND == null) {
                        return Tristate.TRUE
                    }
                }
            }
        }
        return Tristate.NOT_SET
    }
}

// TODO: commands
@Entrypoint
fun entry() {
    logger.info("[Brand Spoofer] Started!")
    logger.info("[Brand Spoofer] Brand rewriting: ${if (Constants.CLIENT_BRAND == null) "cancel" else (if (Constants.CLIENT_BRAND is String) "'${Constants.CLIENT_BRAND}'" else "none")}" )
    proxy.registerListeners(Rewriter)
}

@Destructor
fun destruct() {
    proxy.unregisterListener(Rewriter)
    logger.info("[Brand Spoofer] Stopped!")
}
