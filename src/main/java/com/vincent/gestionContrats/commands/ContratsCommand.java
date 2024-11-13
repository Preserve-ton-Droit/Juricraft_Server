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
            player.sendMessage(ChatColor.YELLOW + "Commandes disponibles :");
            player.sendMessage(ChatColor.YELLOW + "/contrats consulter [1|2|3] - Consulter et choisir un contrat disponible");
            player.sendMessage(ChatColor.YELLOW + "/contrats accepter - Accepter le contrat proposé");
            player.sendMessage(ChatColor.YELLOW + "/contrats supprimer - Supprimer un contrat avec une pénalité de 500 Coins");
            player.sendMessage(ChatColor.YELLOW + "/contrats mescontrats - Afficher vos contrats actifs");
            return true;
        }

        // Commande consulter avec un argument pour choisir le contrat
        if (args[0].equalsIgnoreCase("consulter")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Veuillez spécifier un numéro de contrat : 1, 2 ou 3.");
                return true;
            }

            int contractChoice;
            try {
                contractChoice = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "L'argument doit être un nombre (1, 2 ou 3).");
                return true;
            }

            String contract = contractManager.getContractByChoice(contractChoice);

            if (contract == null) {
                player.sendMessage(ChatColor.RED + "Choix de contrat invalide. Utilisez 1, 2 ou 3.");
                return true;
            }

            if (contractManager.offerContract(player, contract)) {
                player.sendMessage(ChatColor.GREEN + "Contrat disponible :");
                player.sendMessage(ChatColor.YELLOW + contract);
                player.sendMessage(ChatColor.AQUA + "Utilisez /contrats accepter pour signer ce contrat.");
            } else {
                player.sendMessage(ChatColor.RED + "Vous avez atteint la limite de contrats actifs.");
            }

            return true;
        }

        // Commande accepter
        if (args[0].equalsIgnoreCase("accepter")) {
            if (!contractManager.hasContract(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "Vous n'avez sélectionné aucun contrat.");
                return true;
            }

            if (contractManager.signContract(player)) {
                player.sendMessage(ChatColor.GREEN + "Vous avez signé le contrat avec succès !");
            } else {
                player.sendMessage(ChatColor.RED + "Vous n'avez pas assez de fonds pour signer ce contrat.");
            }
            return true;
        }

        // Commande supprimer
        if (args[0].equalsIgnoreCase("supprimer")) {
            if (contractManager.removeContractWithPenalty(player)) {
                player.sendMessage(ChatColor.GREEN + "Vous avez supprimé un contrat avec succès, une pénalité de 500 Coins a été appliquée.");
            } else {
                player.sendMessage(ChatColor.RED + "Vous n'avez pas assez de fonds pour payer la pénalité, ou vous n'avez aucun contrat actif.");
            }
            return true;
        }

        // Commande mescontrats
        if (args[0].equalsIgnoreCase("mescontrats")) {
            List<String> contracts = contractManager.getActiveContracts(player.getUniqueId());
            if (contracts.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Vous n'avez aucun contrat actif.");
            } else {
                player.sendMessage(ChatColor.GREEN + "Vos contrats actifs :");
                for (String contract : contracts) {
                    player.sendMessage(ChatColor.YELLOW + "- " + contract);
                }
            }
            return true;
        }

        player.sendMessage(ChatColor.RED + "Commande inconnue. Utilisez /contrats pour voir les commandes disponibles.");
        return true;
    }
}
