package com.mshdabiola.worker.util

import com.mshdabiola.naijaludo.model.Pawn
import com.mshdabiola.naijaludo.model.player.HumanPlayer
import com.mshdabiola.naijaludo.model.player.Player
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Converter {
    fun gameToString(players: List<Player>, pawns: List<Pawn>, id: Long): Pair<String, String> {

        val pawnPojoList = pawns.map { pawn ->
            val playerId = players.indexOfFirst { pawn.color in it.colors }

            pawn.toPawnPojo(playerId = playerId, gameId = id)
        }
        val playerPojoList =
            players.mapIndexed { index, player ->
                player.toPlayerPojo(
                    index,
                    id,
                    player is HumanPlayer,
                )
            }

        val playerString = Json.encodeToString(playerPojoList)
        val pawnString = Json.encodeToString(pawnPojoList)
        return Pair(pawnString, playerString)
    }
}