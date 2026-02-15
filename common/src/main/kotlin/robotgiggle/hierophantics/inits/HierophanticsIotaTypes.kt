package robotgiggle.hierophantics.inits

import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import robotgiggle.hierophantics.iotas.*

object HierophanticsIotaTypes : HierophanticsRegistrar<IotaType<*>>(HexRegistries.IOTA_TYPE, { HexIotaTypes.REGISTRY }) {
    val MIND_REFERENCE = register("mind_reference", { MindReferenceIota.TYPE })
    val TRIGGER = register("trigger", { TriggerIota.TYPE })
    val MISHAP_THROWER = register("mishap_thrower", { MishapThrowerIota.TYPE })
}