package com.vincent.gestionContrats.SetupContrat;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ContractManager {
    private final HashMap<UUID, List<String>> offeredContracts; // Contrats disponibles pour chaque joueur
    private final HashMap<UUID, List<String>> signedContracts;  // Contrats acceptés (signés) par chaque joueur
    private final Economy economy;
    private static final int MAX_CONTRACTS = 3;
    private static final double CONTRACT_COST_1000 = 1000.0;
    private static final double CONTRACT_COST_5000 = 5000.0;
    private static final double CONTRACT_COST_8000 = 8000.0;

    private static final String CONTRACT_1000 = "Construire une maison (1000 Coins)";
    private static final String CONTRACT_5000 = "Construire une maison (5000 Coins)";
    private static final String CONTRACT_8000 = "Construire une maison (8000 Coins)";

    public ContractManager() {
        this.offeredContracts = new HashMap<>();
        this.signedContracts = new HashMap<>();
        this.economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }

    // Méthode pour proposer un contrat spécifique en fonction du choix (1, 2, ou 3)
    public String getContractByChoice(int choice) {
        switch (choice) {
            case 1:
                return CONTRACT_1000;
            case 2:
                return CONTRACT_5000;
            case 3:
                return CONTRACT_8000;
            default:
                return null; // Contrat invalide
        }
    }

    // Méthode pour proposer un contrat au joueur
    public boolean offerContract(Player player, String contract) {
        UUID playerId = player.getUniqueId();
        List<String> contracts = offeredContracts.computeIfAbsent(playerId, k -> new ArrayList<>());

        // Vérifier si le joueur a déjà atteint la limite de contrats
        if (contracts.size() >= MAX_CONTRACTS) {
            Bukkit.getLogger().info("Limite atteinte : " + contracts);
            return false;
        }

        // Vérifier si le joueur a déjà signé ce contrat
        if (contracts.contains(contract)) {
            Bukkit.getLogger().info("Le contrat " + contract + " est déjà proposé au joueur " + player.getName());
            return false;
        }

        // Vérifier le solde du joueur en fonction du contrat
        double contractCost = 0.0;
        if (contract.equals(CONTRACT_1000)) {
            contractCost = CONTRACT_COST_1000;
        } else if (contract.equals(CONTRACT_5000)) {
            contractCost = CONTRACT_COST_5000;
        } else if (contract.equals(CONTRACT_8000)) {
            contractCost = CONTRACT_COST_8000;
        }

        // Vérifier si le joueur a suffisamment d'argent
        if (economy.getBalance(player) < contractCost) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent pour accepter ce contrat.");
            return false;  // Empêcher l'ajout du contrat si le joueur n'a pas assez d'argent
        }

        // Si le joueur a suffisamment d'argent, prélever la somme
        economy.withdrawPlayer(player, contractCost);  // Retirer l'argent du joueur

        // Ajouter le contrat au joueur
        contracts.add(contract);
        Bukkit.getLogger().info("Contrat proposé : " + contract + " pour le joueur " + player.getName());
        Bukkit.getLogger().info("Contrats offerts : " + contracts);
        return true;
    }

    public List<String> getActiveContracts(UUID playerId) {
        List<String> contracts = offeredContracts.getOrDefault(playerId, new ArrayList<>());
        Bukkit.getLogger().info("Contrats actifs pour le joueur " + playerId + " : " + contracts);
        return contracts;
    }

    public void resetContracts(UUID playerId) {
        Bukkit.getLogger().info("Réinitialisation des contrats pour le joueur : " + playerId);
        offeredContracts.remove(playerId);
        signedContracts.remove(playerId);
    }
}