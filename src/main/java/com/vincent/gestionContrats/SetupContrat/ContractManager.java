package com.vincent.gestionContrats.SetupContrat;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
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
    private static final double PENALTY_COST = 500.0;

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
        List<String> contracts = offeredContracts.get(playerId);

        if (contracts == null) {
            contracts = new ArrayList<>();
            offeredContracts.put(playerId, contracts);  // Assure que la liste est enregistrée
        } else {
            contracts.add(contract);
        }

        if (contracts.size() >= MAX_CONTRACTS) {
            return false;
        }

        System.out.println("Mes contracts "+contracts);
        System.out.println("Nombre de contracts au total "+contracts.size());

        return true;
    }

    // Méthode pour signer un contrat
    public boolean signContract(Player player) {
        UUID playerId = player.getUniqueId();
        List<String> contracts = offeredContracts.get(playerId);

        if (contracts == null || contracts.isEmpty()) return false;

        String contract = contracts.remove(0);  // Retirer le premier contrat proposé
        double contractCost = getContractCost(contract);

        if (economy.has(player, contractCost)) {
            economy.withdrawPlayer(player, contractCost);  // Retirer le montant
            signedContracts.computeIfAbsent(playerId, k -> new ArrayList<>()).add(contract);
            return true;
        }

        return false;
    }

    // Récupérer le coût du contrat selon son type
    private double getContractCost(String contract) {
        if (contract.equals(CONTRACT_1000)) {
            return CONTRACT_COST_1000;
        } else if (contract.equals(CONTRACT_5000)) {
            return CONTRACT_COST_5000;
        } else {
            return CONTRACT_COST_8000;
        }
    }

    public boolean removeContractWithPenalty(Player player) {
        UUID playerId = player.getUniqueId();
        List<String> contracts = signedContracts.get(playerId);

        if (contracts == null || contracts.isEmpty()) return false;

        if (economy.has(player, PENALTY_COST)) {
            economy.withdrawPlayer(player, PENALTY_COST);
            contracts.remove(0);  // Retirer le premier contrat de la liste signée
            return true;
        }

        return false;
    }

    public boolean hasContract(UUID playerId) {
        return offeredContracts.containsKey(playerId) && !offeredContracts.get(playerId).isEmpty();
    }

    public List<String> getActiveContracts(UUID playerId) {
        return signedContracts.getOrDefault(playerId, new ArrayList<>());
    }
}
