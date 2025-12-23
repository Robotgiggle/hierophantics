package robotgiggle.hierophantics.inits

import com.google.gson.JsonObject
import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.patterns.*
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.util.Identifier

object HierophanticsAdvancements {
    lateinit var EMBED_MIND: EmbedMindCriterion
    lateinit var WASTE_MIND: WasteMindCriterion
    lateinit var ALL_TRIGGERS: AllTriggersCriterion
	lateinit var FUSE_VILLAGERS: FuseVillagersCriterion
	lateinit var FUSE_TO_SELF: FuseToSelfCriterion
    
    @JvmStatic
    fun init() {
        EMBED_MIND = Criteria.register(EmbedMindCriterion())
        WASTE_MIND = Criteria.register(WasteMindCriterion())
        ALL_TRIGGERS = Criteria.register(AllTriggersCriterion())
		FUSE_VILLAGERS = Criteria.register(FuseVillagersCriterion())
		FUSE_TO_SELF = Criteria.register(FuseToSelfCriterion())
    }
}

abstract class BaseCriterion<T : BaseCriterion.BaseCondition>(private val id: Identifier) : AbstractCriterion<T>() {
	override fun conditionsFromJson(obj: JsonObject, playerPredicate: LootContextPredicate, predicateDeserializer: AdvancementEntityPredicateDeserializer): T = createCondition()
	protected abstract fun createCondition(): T

	fun trigger(player: ServerPlayerEntity) = trigger(player) { true }
	override fun getId(): Identifier = id

	abstract class BaseCondition(id: Identifier) : AbstractCriterionConditions(id, LootContextPredicate.EMPTY)
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