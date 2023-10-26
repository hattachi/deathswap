package me.flag.ddeath;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;

public class Ddeath extends JavaPlugin {
    private boolean gameStarted = false;

    @Override
    public void onEnable() {
        Objects.requireNonNull(this.getCommand("deathswap")).setExecutor(this);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("deathswap")) {
            if (sender instanceof Player) {
                if (gameStarted) {
                    sender.sendMessage(text("게임이 이미 시작되었습니다.", NamedTextColor.RED));
                    return false;
                } else {
                    if (getServer().getOnlinePlayers().size() != 2) {
                        sender.sendMessage(text("플레이어가 2명이 아닙니다.", NamedTextColor.RED));
                    } else {
                        gameStarted = true;
                        sender.sendMessage(text("데스 스왑 시작."));

                        try {
                            Player otherPlayer = (Player) getServer().getOnlinePlayers().toArray()[1];

                            if (otherPlayer != null) {
                                sender.sendMessage(text("상대 플레이어: ").append(otherPlayer.displayName()));

                                new BukkitRunnable() {
                                    int countdown = 300; // 초기 카운트다운 값

                                    @Override
                                    public void run() {
                                        if (countdown <= 10 && countdown > 0) {
                                            sender.sendMessage(text("카운트다운: " + countdown));
                                            otherPlayer.sendMessage(text("카운트다운: " + countdown));

                                            // 띵소리 재생
                                            for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                                                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.PLAYERS, 1, 1);
                                            }
                                        } else if (countdown == 0) {
                                            sender.sendMessage(text("스왑 중..."));
                                            otherPlayer.sendMessage(text("스왑 중..."));
                                            swapPlayerLocations((Player) getServer().getOnlinePlayers().toArray()[0], (Player) getServer().getOnlinePlayers().toArray()[1]);
                                            countdown = 300; // 1분 카운트다운 재시작
                                        }
                                        countdown--;
                                    }
                                }.runTaskTimer(this, 0, 20);
                            } else {
                                sender.sendMessage(text("상대 플레이어를 찾을 수 없습니다.", NamedTextColor.RED));
                                gameStarted = false;
                            }
                        } catch (IndexOutOfBoundsException e) {
                            sender.sendMessage(text("상대 플레이어를 찾을 수 없습니다.", NamedTextColor.RED));
                            gameStarted = false;
                        }
                    }
                }
            } else {
                sender.sendMessage(text("이 명령어는 플레이어만 사용할 수 있습니다.", NamedTextColor.RED));
                return false;
            }
        }
        return false;
    }


    private void swapPlayerLocations(Player player1, Player player2) {
        Location location1 = player1.getLocation();
        Location location2 = player2.getLocation();

        player1.teleportAsync(location2);
        player2.teleportAsync(location1);
    }
}
