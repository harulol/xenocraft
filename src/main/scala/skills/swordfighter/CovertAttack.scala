package skills.swordfighter

import dev.hawu.plugins.xenocraft.skills.Skill
import dev.hawu.plugins.xenocraft.data.SkillType
import dev.hawu.plugins.xenocraft.data.User

/** The skill handler for the skill that reduces aggro generation.
  */
object CovertAttack extends Skill(SkillType.COVERT_ATTACK):
  private val AGGRO_REDUCTION = 0.65

  override def apply(user: User): Unit = user.autoAggroGeneration -= AGGRO_REDUCTION

  override def unapply(user: User): Unit = user.autoAggroGeneration += AGGRO_REDUCTION
