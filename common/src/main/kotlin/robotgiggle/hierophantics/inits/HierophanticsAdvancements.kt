package robotgiggle.hierophantics.inits

import com.google.gson.JsonObject
import robotgiggle.hierophantics.Hierophantics
import net.minecraft.advancements.critereon.SimpleCriterionTrigger
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.server.level.ServerPlayer
import net.minecraft.advancements.critereon.ContextAwarePredicate
import net.minecraft.advancements.critereon.DeserializationContext
import net.minecraft.resources.ResourceLocation

object HierophanticsAdvancements {
    lateinit var EMBED_MIND: EmbedMindCriterion
    lateinit var WASTE_MIND: WasteMindCriterion
    lateinit var ALL_TRIGGERS: AllTriggersCriterion
	lateinit var FUSE_VILLAGERS: FuseVillagersCriterion
	lateinit var FUSE_TO_SELF: FuseToSelfCriterion
    
    @JvmStatic
    fun init() {
        EMBED_MIND = CriteriaTriggers.register(EmbedMindCriterion())
        WASTE_MIND = CriteriaTriggers.register(WasteMindCriterion())
        ALL_TRIGGERS = CriteriaTriggers.register(AllTriggersCriterion())
		FUSE_VILLAGERS = CriteriaTriggers.register(FuseVillagersCriterion())
		FUSE_TO_SELF = CriteriaTriggers.register(FuseToSelfCriterion())
    }
}

abstract class BaseCriterion<T : BaseCriterion.BaseCondition>(private val id: ResourceLocation) : SimpleCriterionTrigger<T>() {
	override fun createInstance(obj: JsonObject, playerPredicate: ContextAwarePredicate, predicateDeserializer: DeserializationContext): T = createCondition()
	protected abstract fun createCondition(): T

	fun trigger(player: ServerPlayer) = trigger(player) { true }
	override fun getId(): ResourceLocation = id

	abstract class BaseCondition(id: ResourceLocation) : AbstractCriterionTriggerInstance(id, ContextAwarePredicate.ANY)
}

class EmbedMindCriterion : BaseCriterion<EmbedMindCriterion.Condition>(Hierophantics.id("embed_mind")) {
	override fun createCondition() = Condition()
	class Condition : BaseCondition(Hierophantics.id("embed_mind"))
}

class WasteMindCriterion : BaseCriterion<WasteMindCriterion.Condition>(Hierophantics.id("waste_mind")) {
    override fun createCondition() = Condition()
	class Condition : BaseCondition(Hierophantics.id("waste_mind"))
}

class AllTriggersCriterion : BaseCriterion<AllTriggersCriterion.Condition>(Hierophantics.id("all_triggers")) {
	override fun createCondition() = Condition()
	class Condition : BaseCondition(Hierophantics.id("all_triggers"))
}

class FuseVillagersCriterion : BaseCriterion<FuseVillagersCriterion.Condition>(Hierophantics.id("fuse_villagers")) {
	override fun createCondition() = Condition()
	class Condition : BaseCondition(Hierophantics.id("fuse_villagers"))
}

class FuseToSelfCriterion : BaseCriterion<FuseToSelfCriterion.Condition>(Hierophantics.id("fuse_to_self")) {
	override fun createCondition() = Condition()
	class Condition : BaseCondition(Hierophantics.id("fuse_to_self"))
}