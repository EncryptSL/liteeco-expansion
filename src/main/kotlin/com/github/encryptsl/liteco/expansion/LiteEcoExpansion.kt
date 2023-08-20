package com.github.encryptsl.liteco.expansion

import encryptsl.cekuj.net.LiteEco
import encryptsl.cekuj.net.api.economy.LiteEcoEconomyAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

class LiteEcoExpansion : PlaceholderExpansion() {

    companion object {
        const val PLUGIN_NAME = "LiteEco"
        const val IDENTIFIER = "liteeco"
        const val AUTHOR_NAME = "EncryptSL"
        const val PAPI_VERSION = "1.0.5"
    }

    lateinit var liteEco: LiteEco

    override fun getIdentifier(): String = IDENTIFIER

    override fun getAuthor(): String = AUTHOR_NAME

    override fun getVersion(): String = PAPI_VERSION

    override fun getRequiredPlugin(): String = PLUGIN_NAME

    override fun persist(): Boolean = true

    override fun canRegister(): Boolean {
        liteEco = Bukkit.getPluginManager().getPlugin(requiredPlugin) as LiteEco
        return if (liteEco != null) true else false
    }

    override fun onRequest(player: OfflinePlayer?, identifier: String): String? {
        if (player == null) return null
        val args = identifier.split("_")
        val rank = args.getOrNull(2)?.toIntOrNull()

        return when (identifier) {
            "balance" -> liteEco.api.getBalance(player).toString()
            "balance_formatted" -> liteEco.api.fullFormatting(liteEco.api.getBalance(player))
            "balance_compacted" -> liteEco.api.compacted(liteEco.api.getBalance(player))
            "top_rank_player" -> nameByRank(1)
            else -> rank?.let {
                when {
                    identifier.startsWith("top_formatted_") -> liteEco.api.fullFormatting(balanceByRank(rank))
                    identifier.startsWith("top_compacted_") -> liteEco.api.compacted(balanceByRank(rank))
                    identifier.startsWith("top_balance_") -> balanceByRank(rank).toString()
                    identifier.startsWith("top_player_") -> nameByRank(rank)
                    else -> null
                }
            }
        }
    }

    private fun nameByRank(rank: Int): String {
        val topBalance = topBalance()
        return if (rank in 1..topBalance.size) {
            val playerUuid = topBalance.keys.elementAt(rank - 1)
            Bukkit.getOfflinePlayer(UUID.fromString(playerUuid)).name ?: "UNKNOWN"
        } else {
            "EMPTY"
        }
    }

    private fun balanceByRank(rank: Int): Double {
        val topBalance = topBalance()
        return if (rank in 1..topBalance.size) {
            topBalance.values.elementAt(rank - 1)
        } else {
            0.0
        }
    }

    private fun topBalance(): LinkedHashMap<String, Double> {
        return liteEco.api.getTopBalance()
            .filterKeys { uuid -> Bukkit.getOfflinePlayer(UUID.fromString(uuid)).hasPlayedBefore() }
            .toList()
            .sortedByDescending { (_, balance) -> balance }
            .toMap()
            .let { LinkedHashMap<String, Double>(it) }
    }
}