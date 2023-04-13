package com.mshdabiola.worker.util

import com.mshdabiola.database.model.PawnEntity
import com.mshdabiola.database.model.PlayerEntity
import com.mshdabiola.naijaludo.model.Pawn
import com.mshdabiola.naijaludo.model.player.Player
import kotlinx.serialization.Serializable

@Serializable
data class PlayerPojo(
    val id: Int,
    val gameId: Long,
    val name: String,
    val win: Int,
    val isCurrent: Boolean,
    val isHuman: Boolean,
)

fun Player.toPlayerPojo(id: Int, gameId: Long, isHuman: Boolean) =
    PlayerPojo(
        id,
        gameId,
        name,
        win,
        isCurrent,
        isHuman,
    )

fun PlayerPojo.toPlayerEntity() = PlayerEntity(id, gameId, name, win, isCurrent, isHuman)


@Serializable
data class PawnPojo(
    val id: Int = 0,
    val gameId: Long,
    val currentPos: Int,
    val playerId: Int,
)

fun Pawn.toPawnPojo(
    playerId: Int,
    gameId: Long,
) = PawnPojo(
    this.idx,
    gameId,
    currentPos,
    playerId,
)

fun PawnPojo.toPawnEntity() = PawnEntity(id, gameId, currentPos, playerId)

