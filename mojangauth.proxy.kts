@file:Repository("https://litarvan.github.io/maven")
@file:DependsOn("fr.litarvan:openauth:1.1.3")
@file:Import("gui.kt")

import me.marvin.proxy.utils.*

import java.net.*
import java.util.*
import java.util.concurrent.atomic.*
import java.awt.*
import java.io.*

import javax.swing.*
import javax.swing.text.*

import fr.litarvan.openauth.microsoft.*

object Constants {
    val GUI_REFERENCE = AtomicReference<Gui>(null)
}

@Entrypoint
fun entry() {
    logger.info("[MS Auth] Started!")
    logger.info("[MS Auth] To show the GUI, please use the command 'mojang'")

    commands.register({ _ ->
        logger.info("[Vanity Auth] Opening GUI...")

        Constants.GUI_REFERENCE.get()?.close()
        Constants.GUI_REFERENCE.set(openGui())

        return@register true
    }, "mojang")
}

@Destructor
fun destruct() {
    commands.unregister("mojang")
    logger.info("[MS Auth] Stopped!")
}

fun openGui(): Gui = Gui().apply {
   open("msft auth", { name, password ->
        logger.info("[MS Auth] Logging in with email '$name'...");
        try {
            val authenticator = MicrosoftAuthenticator();
            val result = authenticator.loginWithCredentials(name, password);

            proxy.name(result.profile.name);
            proxy.uuid(result.profile.id);
            proxy.accessToken(result.accessToken);

            JOptionPane.showMessageDialog(
                null,
                "successfully logged in\n(${proxy.name()})",
                "success",
                JOptionPane.INFORMATION_MESSAGE
            )

            logger.info("[MS Auth] Successfully logged in! ($name -> ${proxy.name()})")
            proxy.sessionService(SessionService.DEFAULT)
            logger.info("[MS Auth] Set session service to: " + proxy.sessionService())
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(
                null,
                e.message!!.lowercase(),
                "error during login",
                JOptionPane.ERROR_MESSAGE
            )

            logger.info("[MS Auth] Failed to log in! ($name)")
        }
    })
}