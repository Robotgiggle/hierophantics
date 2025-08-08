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
    
    @JvmStatic
    fun init() {
        EMBED_MIND = Criteria.register(EmbedMindCriterion())
        WASTE_MIND = Criteria.register(WasteMindCriterion())
        ALL_TRIGGERS = Criteria.register(AllTriggersCriterion())
    }
}

class EmbedMindCriterion : AbstractCriterion<EmbedMindCriterion.Condition>() {
    override fun conditionsFromJson(jsonObject: JsonObject, lootContextPredicate: LootContextPredicate, advancementEntityPredicateDeserializer: AdvancementEntityPredicateDeserializer) = Condition()
	fun trigger(player: ServerPlayerEntity) = trigger(player) { true }
	override fun getId() = ID

	class Condition : AbstractCriterionConditions(ID, LootContextPredicate.EMPTY)
	companion object {
		val ID: Identifier = Hierophantics.id("embed_mind")
	}
}

class WasteMindCriterion : AbstractCriterion<WasteMindCriterion.Condition>() {
    override fun conditionsFromJson(jsonObject: JsonObject, lootContextPredicate: LootContextPredicate, advancementEntityPredicateDeserializer: AdvancementEntityPredicateDeserializer) = Condition()
	fun trigger(player: ServerPlayerEntity) = trigger(player) { true }
	override fun getId() = ID

	class Condition : AbstractCriterionConditions(ID, LootContextPredicate.EMPTY)
	companion object {
		val ID: Identifier = Hierophantics.id("waste_mind")
	}
}

class AllTriggersCriterion : AbstractCriterion<AllTriggersCriterion.Condition>() {
    override fun conditionsFromJson(jsonObject: JsonObject, lootContextPredicate: LootContextPredicate, advancementEntityPredicateDeserializer: AdvancementEntityPredicateDeserializer) = Condition()
	fun trigger(player: ServerPlayerEntity) = trigger(player) { true }
	override fun getId() = ID

	class Condition : AbstractCriterionConditions(ID, LootContextPredicate.EMPTY)
	companion object {
		val ID: Identifier = Hierophantics.id("all_triggers")
	}
}