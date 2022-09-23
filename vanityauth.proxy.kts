@file:Import("gui.kt")

import me.marvin.proxy.utils.*

import com.google.gson.*

import java.net.*
import java.net.http.*
import java.util.*
import java.util.concurrent.atomic.*
import java.awt.*
import java.io.*

import javax.swing.*
import javax.swing.text.*

import java.nio.charset.StandardCharsets

object Constants {
    val JOIN_URL = URI.create("https://kliens.vanityempire.hu/join.php")
    val LOGIN_URL = URI.create("https://kliens.vanityempire.hu/login.php")
    val CLIENT = HttpClient.newBuilder().build()

    val NAME_FIELD = "DqMvBP"
    val PASSWORD_FIELD = "zPvBMBgd"
    val GUI_REFERENCE = AtomicReference<Gui>(null)
}

class VanitySessionService() : SessionService {
    override fun joinServer(profile: GameProfile, token: String, serverId: String) {
        val json = JsonObject();
        json.addProperty("accessToken", token)
        json.addProperty("selectedProfile", profile.uuid().toString())
        json.addProperty("serverId", serverId)

        val response = Constants.CLIENT.send(HttpRequest.newBuilder()
            .uri(Constants.JOIN_URL)
            .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
            .header("Content-Type", "application/json")
            .build(), HttpResponse.BodyHandlers.ofString())

        logger.info("[Vanity Auth] Got response: ${response.body()}")
    }
}

@Entrypoint
fun entry() {
    logger.info("[Vanity Auth] Started!")
    logger.info("[Vanity Auth] To show the GUI, please use the command 'vanity'")

    commands.register({ _ ->
        logger.info("[Vanity Auth] Opening GUI...")

        Constants.GUI_REFERENCE.get()?.close()
        Constants.GUI_REFERENCE.set(openGui())

        return@register true
    }, "vanity")
}

@Destructor
fun destruct() {
    commands.unregister("vanity")
    Constants.GUI_REFERENCE.get()?.close()
    logger.info("[Vanity Auth] Stopped!")
}

fun JsonElement.isNotEmpty(): Boolean {
    return when (this) {
        is JsonPrimitive -> this.getAsString().isNotEmpty()
        else -> false
    }
}

fun openGui(): Gui = Gui().apply {
    open("vanity auth", { name, password ->
        logger.info("[Vanity Auth] Logging in with name '$name'...")
        try {
            val json = JsonObject()
            json.addProperty(Constants.NAME_FIELD, name)
            json.addProperty(Constants.PASSWORD_FIELD, password)
            json.addProperty("twofact", "")

            val result = JsonParser.parseString(Constants.CLIENT.send(HttpRequest.newBuilder()
                .uri(Constants.LOGIN_URL)
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build(), HttpResponse.BodyHandlers.ofString()).body())
                .getAsJsonObject()

            logger.info("[Vanity Auth] Got got response: $result")

            if (result["error"].isNotEmpty() || result["errormessage"].isNotEmpty()) {
                if (result["error"].isNotEmpty()) {
                    throw Exception(result["error"].getAsString())
                } else {
                    throw Exception(result["errormessage"].getAsString())
                }
            }

            proxy.name(result["username"].getAsString())
            proxy.uuid(result["uuid"].getAsString())
            proxy.accessToken(result["sessionId"].getAsString())

            JOptionPane.showMessageDialog(
                null,
                "successfully logged in\n(${proxy.name()})",
                "success",
                JOptionPane.INFORMATION_MESSAGE
            )

            logger.info("[Vanity Auth] Successfully logged in! ($name -> ${proxy.name()})")
            proxy.sessionService(VanitySessionService())
            logger.info("[Vanity Auth] Set session service to: " + proxy.sessionService())
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(
                null,
                e.message!!.lowercase(),
                "error during login",
                JOptionPane.ERROR_MESSAGE
            )
            
            logger.info("[Vanity Auth] Failed to log in! ($name)")
        }
    })
}