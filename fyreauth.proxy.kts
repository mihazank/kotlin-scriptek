@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("org.javassist:javassist:3.29.2-GA")

import me.marvin.proxy.utils.*

import com.google.gson.*
import javassist.ClassPool
import me.marvin.scripting.kotlin.Destructor
import me.marvin.scripting.kotlin.Entrypoint
import me.marvin.scripting.kotlin.ProxyScriptConstants.commands
import me.marvin.scripting.kotlin.ProxyScriptConstants.logger
import me.marvin.scripting.kotlin.ProxyScriptConstants.proxy
import java.io.File
import java.io.InputStream

import java.nio.file.*
import java.net.*
import java.net.http.*
import java.security.*
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.script.experimental.dependencies.DependsOn
import kotlin.script.experimental.dependencies.Repository

object Constants {
    val PAYLOAD = """
        {
           "java.specification.version":"17",
           "sun.cpu.isalist":"amd64",
           "sun.jnu.encoding":"Cp1250",
           "java.class.path":"C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/net/minecraft/launchwrapper/f1.0/launchwrapper-f1.0.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/optifine/OptiFine/1.18.2_HD_U_H6/OptiFine-1.18.2_HD_U_H6.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/optifine/launchwrapper-of/2.3/launchwrapper-of-2.3.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/com/mojang/logging/1.0.0/logging-1.0.0.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/com/mojang/blocklist/1.0.10/blocklist-1.0.10.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/com/mojang/patchy/2.2.10/patchy-2.2.10.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/com/github/oshi/oshi-core/5.8.5/oshi-core-5.8.5.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/net/java/dev/jna/jna/5.10.0/jna-5.10.0.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/net/java/dev/jna/jna-platform/5.10.0/jna-platform-5.10.0.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/org/slf4j/slf4j-api/1.8.0-beta4/slf4j-api-1.8.0-beta4.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/org/apache/logging/log4j/log4j-slf4j18-impl/2.17.0/log4j-slf4j18-impl-2.17.0.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/com/ibm/icu/icu4j/70.1/icu4j-70.1.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/com/mojang/javabridge/1.2.24/javabridge-1.2.24.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/net/sf/jopt-simple/jopt-simple/5.0.4/jopt-simple-5.0.4.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/io/netty/netty-all/4.1.68.Final/netty-all-4.1.68.Final.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/com/google/guava/failureaccess/1.0.1/failureaccess-1.0.1.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/com/google/guava/guava/31.0.1-jre/guava-31.0.1-jre.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/commons-io/commons-io/2.11.0/commons-io-2.11.0.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/commons-codec/commons-codec/1.15/commons-codec-1.15.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/com/mojang/brigadier/1.0.18/brigadier-1.0.18.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/com/mojang/datafixerupper/4.1.27/datafixerupper-4.1.27.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/com/google/code/gson/gson/2.8.9/gson-2.8.9.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/com/mojang/authlib/3.2.38/authlib-3.2.38.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/org/apache/commons/commons-compress/1.21/commons-compress-1.21.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/org/apache/httpcomponents/httpclient/4.5.13/httpclient-4.5.13.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/commons-logging/commons-logging/1.2/commons-logging-1.2.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/org/apache/httpcomponents/httpcore/4.4.14/httpcore-4.4.14.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/it/unimi/dsi/fastutil/8.5.6/fastutil-8.5.6.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/org/apache/logging/log4j/log4j-api/2.17.0/log4j-api-2.17.0.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/org/apache/logging/log4j/log4j-core/2.17.0/log4j-core-2.17.0.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/org/lwjgl/lwjgl/3.2.2/lwjgl-3.2.2.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/org/lwjgl/lwjgl-jemalloc/3.2.2/lwjgl-jemalloc-3.2.2.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/org/lwjgl/lwjgl-openal/3.2.2/lwjgl-openal-3.2.2.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/org/lwjgl/lwjgl-opengl/3.2.2/lwjgl-opengl-3.2.2.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/org/lwjgl/lwjgl-glfw/3.2.2/lwjgl-glfw-3.2.2.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/org/lwjgl/lwjgl-stb/3.2.2/lwjgl-stb-3.2.2.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/org/lwjgl/lwjgl-tinyfd/3.2.2/lwjgl-tinyfd-3.2.2.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/libraries/com/mojang/text2speech/1.12.4/text2speech-1.12.4.jar;C:/Users/user/AppData/Roaming/.fyremc.hu/bin/OptiFine 1.18.2.jar",
           "java.vm.vendor":"Oracle Corporation",
           "minecraft.launcher.brand":"FyreMC-Launcher",
           "sun.arch.data.model":"64",
           "jna.library.path":"null;./src/natives/resources/;C:\\\\Users\\\\user\\\\AppData\\\\Roaming\\\\.fyremc.hu\\\\bin\\\\natives",
           "user.variant":"",
           "java.vendor.url":"https://java.oracle.com/",
           "user.timezone":"Europe/Budapest",
           "java.vm.specification.version":"17",
           "os.name":"Windows 10",
           "sun.java.launcher":"SUN_STANDARD",
           "user.country":"HU",
           "sun.boot.library.path":"C:\\\\Users\\\\user\\\\AppData\\\\Roaming\\\\.fyremc.hu\\\\bin\\\\runtime\\\\java-runtime-beta\\\\x64\\\\bin",
           "sun.java.command":"net.minecraft.client.main.launch",
           "jdk.debug":"release",
           "minecraft.launcher.version":"0.8.9",
           "sun.cpu.endian":"little",
           "user.home":"C:\\\\Users\\\\user",
           "user.language":"hu",
           "java.specification.vendor":"Oracle Corporation",
           "java.version.date":"2021-10-19",
           "java.home":"C:\\\\Users\\\\user\\\\AppData\\\\Roaming\\\\.fyremc.hu\\\\bin\\\\runtime\\\\java-runtime-beta\\\\x64",
           "file.separator":"\\\\",
           "java.vm.compressedOopsMode":"32-bit",
           "line.separator":"\\r\\n",
           "java.vm.specification.vendor":"Oracle Corporation",
           "java.specification.name":"Java Platform API Specification",
           "java.awt.headless":"true",
           "fml.ignoreInvalidMinecraftCertificates":"true",
           "user.script":"",
           "sun.management.compiler":"HotSpot 64-Bit Tiered Compilers",
           "java.runtime.version":"17.0.1",
           "user.name":"user",
           "path.separator":";",
           "os.version":"10.0",
           "java.runtime.name":"Java(TM) SE Runtime Environment",
           "file.encoding":"Cp1250",
           "jnidispatch.path":"C:\\\\Users\\\\user\\\\AppData\\\\Local\\\\Temp\\\\jna--1940113314\\\\jna643578695274497112.dll",
           "sun.nio.ch.bugLevel":"",
           "java.vm.name":"Java HotSpot(TM) 64-Bit Server VM",
           "jna.loaded":"true",
           "java.vendor.url.bug":"https://bugreport.java.com/bugreport/",
           "java.io.tmpdir":"C:\\\\Users\\\\user\\\\AppData\\\\Local\\\\Temp\\\\",
           "java.version":"17.0.1",
           "user.dir":"C:\\\\Users\\\\user\\\\AppData\\\\Local",
           "os.arch":"amd64",
           "java.vm.specification.name":"Java Virtual Machine Specification",
           "sun.os.patch.level":"",
           "native.encoding":"Cp1250",
           "java.library.path":"C:\\\\Users\\\\user\\\\AppData\\\\Roaming\\\\.fyremc.hu\\\\bin\\\\natives",
           "java.vm.info":"mixed mode, sharing",
           "java.vendor":"Oracle Corporation",
           "java.vm.version":"17.0.1+12-LTS-39",
           "sun.io.unicode.encoding":"UnicodeLittle",
           "java.class.version":"61.0"
        }"""
    val JOIN_URL = URI.create("http://auth.fyremc.hu/game/join2.php")
    val CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()
    val RANDOM_ID = "${Math.random()}".substring(2)
}

class FyreSessionService : SessionService {
    override fun joinServer(profile: GameProfile, authenticationToken: String, serverId: String) {
        val json = JsonObject()
        json.addProperty("accessToken", authenticationToken)
        json.addProperty("selectedProfile", profile.uuid().toString())
        json.addProperty("serverId", serverId)
        json.addProperty("selectedProfileId", Constants.RANDOM_ID)

        val payload = createPayload(profile, authenticationToken, serverId)
        val firstPart = sha1Hex(
            payload +
            "&`(Z#e6uKh)+)\\\\5q" +
            serverId.substring(1, 8) +
            profile.uuid().toString().substring(0, 4)
        ).substring(0, 39)

        json.addProperty("d", firstPart + Base64.getEncoder().encodeToString(payload.toByteArray()))

        val body = json.toString()
        val response = Constants.CLIENT.send(HttpRequest.newBuilder()
            .uri(Constants.JOIN_URL)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .header("Accept", "text/html, image/jpeg, *; q=.2, */*; q=.2")
            .header("Content-Type", "application/json; charset=utf8")
            .header("User-Agent", "Java/17.0.1")
            .header("Cache-Control", "no-cache")
            .build(), HttpResponse.BodyHandlers.ofString())

        logger.info("[Fyre Auth] Got response: ${response.body()}")
    }

    fun sha1Hex(s: String): String {
        var digest = MessageDigest.getInstance("SHA-1")
        return HexFormat.of().formatHex(digest.digest(s.toByteArray()))
    }

    fun createPayload(profile: GameProfile, authenticationToken: String, serverId: String): String {
        val json = JsonParser.parseString(Constants.PAYLOAD).asJsonObject
        val temp = Files.createTempFile("fyre_", "auth")

        val result = Constants.CLIENT.send(HttpRequest.newBuilder()
            .uri(URI.create("https://auth.fyremc.hu/game/authclass.jar"))
            .GET()
            .build(), HttpResponse.BodyHandlers.ofByteArray()
        )
        
        if (result.statusCode() != 200) {
            logger.error("[Fyre Auth] https://auth.fyremc.hu/game/authclass.jar returned status code: " + result.statusCode())
            return json.toString()
        }
        
        Files.write(temp, result.body())

        val jarFile = JarFile(temp.toFile())
        val generated = jarFile.stream().filter { it.name.endsWith(".class") }
                .map { extractFmcValues(jarFile, it) }
                .max { o1, o2 -> o1.size.compareTo(o2.size) }
                .orElse(hashMapOf())
        jarFile.close()

        logger.info("Got values: ${generated}")

        generated.forEach { (k, v) -> json.addProperty(k, v) }
        json.addProperty("fmc.count", json.keySet().size + 1)

        return json.toString()
    }

    private fun extractFmcValues(jarFile: JarFile, entry: JarEntry): Map<String, String> {
        val entryStream = jarFile.getInputStream(entry)
        val ctClass = ClassPool.getDefault().makeClass(entryStream)
        entryStream.close()

        val foundPairs: MutableMap<String, String> = hashMapOf()

        ctClass.methods.filter { it.returnType.simpleName == "Map" }
                .map { it.methodInfo.constPool }
                .forEach {
                    var remember: String? = null

                    for(i in 0 until it.size) {
                        var constValueNullable: String?
                        try {
                            constValueNullable = it.getUtf8Info(i);
                        } catch (_: Exception) { continue }

                        val constValue: String = constValueNullable ?: continue

                        if(constValue.startsWith("fmc.") && constValue != "fmc.count") {
                            remember = constValue;
                        } else if(remember != null) {
                            foundPairs[remember] = constValue
                        }
                    }
                }

        return foundPairs;
    }
}

@Entrypoint
fun entry() {
    logger.info("[Fyre Auth] Started!")
    logger.info("[Fyre Auth] To use this session service, please use the builtin commands ('setname', 'settoken' and 'setuuid').")
    logger.info("[Fyre Auth] To set the authentication service, please use the command 'fyre'")

    commands.register({ _ ->
        proxy.sessionService(FyreSessionService())
        logger.info("[Fyre Auth] Set session service to: " + proxy.sessionService())

        return@register true
    }, "fyre")
}

@Destructor
fun destruct() {
    commands.unregister("fyre")
    logger.info("[Fyre Auth] Stopped!")
}
