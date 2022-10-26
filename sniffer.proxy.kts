@file:Import("packetFilter.kt")

import me.marvin.proxy.networking.*
import me.marvin.proxy.networking.packet.*
import me.marvin.proxy.utils.*

import io.netty.buffer.*
import io.netty.channel.*

import java.util.*

object Constants {
    const val HEADER = "         +-------------------------------------------------+"
    val VALIDATOR: Validator = Validator.PhaseValidator(ProtocolPhase.LOGIN) or
        Validator.TypeValidator(
            PacketTypes.Play.Client.PLUGIN_MESSAGE,
            PacketTypes.Play.Server.PLUGIN_MESSAGE,
            PacketTypes.Login.Client.LOGIN_PLUGIN_RESPONSE,
            PacketTypes.Login.Server.LOGIN_PLUGIN_REQUEST,
        ) 
}

object Sniffer : PacketListener {
    override fun priority(): Byte = 0

    override fun handle(type: PacketType, buf: ByteBuf, send: Channel, recv: ChannelHandlerContext, version: Version): Tristate {
        val prefix = if (type.direction() == ProtocolDirection.CLIENT) "C->S" else "S->C"
        if (Constants.VALIDATOR.validate(type)) {
            println("$prefix ${type.phase()}.$type -> ${ByteBufUtil.prettyHexDump(buf).replace(Constants.HEADER, "")}")
        }
        return Tristate.NOT_SET
    }
}

@Entrypoint
fun entry() {
	proxy.registerListeners(Sniffer)
}

@Destructor
fun destruct() {
    proxy.unregisterListener(Sniffer)
}
