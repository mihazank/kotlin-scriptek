import me.marvin.proxy.networking.*
import me.marvin.proxy.networking.packet.*

import java.util.*

interface Validator {
    fun validate(type: PacketType): Boolean
    infix fun xor(validator: Validator) = combine(validator, Boolean::xor)
    infix fun and(validator: Validator) = combine(validator, Boolean::and)
    infix fun or(validator: Validator) = combine(validator, Boolean::or)

    private fun combine(validator: Validator, combiner: (Boolean, Boolean) -> Boolean): Validator {
        return object : Validator {
            override fun validate(type: PacketType): Boolean {
                return combiner(this@Validator.validate(type), validator.validate(type))
            }
        }
    }

    class PhaseValidator(phase: ProtocolPhase, vararg phases: ProtocolPhase, private val whitelist: Boolean = true): Validator {
        val set: EnumSet<ProtocolPhase> = EnumSet.of(phase, *phases)
        override fun validate(type: PacketType): Boolean = !(set.contains(type.phase()) xor whitelist)
    }

    class TypeValidator(type: PacketType, vararg types: PacketType, private val whitelist: Boolean = true): Validator {
        val set: Set<PacketType> = setOf(type, *types)
        override fun validate(type: PacketType): Boolean = !(set.contains(type) xor whitelist)
    }

    class DirectionValidator(private val direction: ProtocolDirection): Validator {
        override fun validate(type: PacketType): Boolean = (direction == type.direction())
    }
}