package com.vincent.gestionContrats.events;

import com.vincent.gestionContrats.GestionContrats;
import com.vincent.gestionContrats.SetupContrat.ContractManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class InventoryGUI implements Listener {
    private final Player player;
    private final ContractManager contractManager;
    private final Inventory inventory;

    public InventoryGUI(Player player, ContractManager contractManager) {
        this.player = player;
        this.contractManager = contractManager;
        this.inventory = Bukkit.createInventory(null, 9, ChatColor.BLUE + "Contrats Disponibles");
    }

    public void open() {
        List<String> availableContracts = List.of(
                contractManager.getContractByChoice(1),
                contractManager.getContractByChoice(2),
                contractManager.getContractByChoice(3)
        );

        for (int i = 0; i < availableContracts.size(); i++) {
            String contract = availableContracts.get(i);
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta meta = (BookMeta) book.getItemMeta();
            meta.setTitle(ChatColor.GOLD + "Contrat #" + (i + 1));
            meta.addPage(ChatColor.YELLOW + contract);
            book.setItemMeta(meta);

            inventory.setItem(i, book);
        }

        player.openInventory(inventory);
        Bukkit.getServer().getPluginManager().registerEvents(this, GestionContrats.getInstance());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(ChatColor.BLUE + "Contrats Disponibles")) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() != Material.WRITTEN_BOOK) return;

        // Récupérer et signer le contrat
        String contract = ((BookMeta) clickedItem.getItemMeta()).getPages().get(0).replace(ChatColor.YELLOW.toString(), "");
        if (contractManager.offerContract(player, contract)) {
            player.sendMessage(ChatColor.GREEN + "Contrat ajouté avec succès !");
        }
        player.closeInventory();
    }
}

