package com.vincent.gestionContrats.commands;

import com.vincent.gestionContrats.SetupContrat.ContractManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ContratsCommand implements CommandExecutor {
    private final ContractManager contractManager;

    public ContratsCommand(ContractManager contractManager) {
        this.contractManager = contractManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Seuls les joueurs peuvent utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // Message principal pour guider les joueurs
            player.sendMessage(ChatColor.YELLOW + "Interagissez avec le NPC pour gérer vos contrats.");
            player.sendMessage(ChatColor.YELLOW + "Commandes disponibles :");
            player.sendMessage(ChatColor.YELLOW + "/contrats mescontrats - Voir vos contrats actifs");
            player.sendMessage(ChatColor.YELLOW + "/contrats info - Afficher les détails d'un contrat");
            player.sendMessage(ChatColor.YELLOW + "/contrats admin reset [joueur] - Réinitialiser les contrats d'un joueur (admin)");
            return true;
        }

        // Commande pour voir les contrats actifs
        if (args[0].equalsIgnoreCase("mescontrats")) {
            List<String> activeContracts = contractManager.getActiveContracts(player.getUniqueId());
            if (activeContracts.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Vous n'avez aucun contrat actif.");
            } else {
                player.sendMessage(ChatColor.GREEN + "Vos contrats actifs :");
                for (String contract : activeContracts) {
                    player.sendMessage(ChatColor.YELLOW + "- " + contract);
                }
            }
            return true;
        }

        // Commande pour afficher les détails d'un contrat spécifique
        if (args[0].equalsIgnoreCase("info")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Utilisation : /contrats info [1|2|3]");
                return true;
            }

            int contractChoice;
            try {
                contractChoice = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Le numéro de contrat doit être un chiffre (1, 2 ou 3).");
                return true;
            }

            String contract = contractManager.getContractByChoice(contractChoice);
            if (contract == null) {
                player.sendMessage(ChatColor.RED + "Aucun contrat trouvé pour ce numéro. Utilisez 1, 2 ou 3.");
            } else {
                player.sendMessage(ChatColor.GREEN + "Détails du contrat #" + contractChoice + " :");
                player.sendMessage(ChatColor.YELLOW + contract);
            }
            return true;
        }

        // Commande admin pour réinitialiser les contrats d'un joueur
        if (args[0].equalsIgnoreCase("admin")) {
            if (!player.hasPermission("contrats.admin")) {
                player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'utiliser cette commande.");
                return true;
            }

            if (args.length != 3 || !args[1].equalsIgnoreCase("reset")) {
                player.sendMessage(ChatColor.RED + "Utilisation : /contrats admin reset [joueur]");
                return true;
            }

            String targetPlayerName = args[2];
            Player targetPlayer = player.getServer().getPlayer(targetPlayerName);

            if (targetPlayer == null) {
                player.sendMessage(ChatColor.RED + "Le joueur " + targetPlayerName + " n'est pas en ligne.");
                return true;
            }

            contractManager.resetContracts(targetPlayer.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Tous les contrats de " + targetPlayerName + " ont été réinitialisés.");
            targetPlayer.sendMessage(ChatColor.YELLOW + "Tous vos contrats ont été réinitialisés par un administrateur.");
            return true;
        }

        // Si la commande est inconnue
        player.sendMessage(ChatColor.RED + "Commande inconnue. Utilisez /contrats pour voir les commandes disponibles.");
        return true;
    }
}
