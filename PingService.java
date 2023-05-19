package pro.gravit.launcher.client.gui.service;

import pro.gravit.launcher.client.ServerPinger;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

public class PingService {
    private final Map<String, CompletableFuture<PingServerReport>> reports = new ConcurrentHashMap<>();

    public CompletableFuture<PingServerReport> getPingReport(String serverName) {
        CompletableFuture<PingServerReport> report = reports.get(serverName);
        if(report == null) {
            report = new CompletableFuture<>();
            reports.put(serverName, report);
        }
        return report;
    }

    public void addReports(Map<String, PingServerReport> map) {
        map.forEach((k,v) -> {
            CompletableFuture<PingServerReport> report = getPingReport(k);
            report.complete(v);
        });
    }

    public void addReport(String name, ServerPinger.Result result) {
        CompletableFuture<PingServerReport> report = getPingReport(name);
        PingServerReport value = new PingServerReport(name, result.maxPlayers, result.onlinePlayers, result.sample);
        report.complete(value);
    }

    public void clear() {
        reports.forEach((k,v) -> {
            if(!v.isDone()) {
                v.completeExceptionally(new InterruptedException());
            }
        });
        reports.clear();
    }

    public static class PingServerReport {
        public final String name;
        public final int maxPlayers;
        public final int playersOnline;
		public final ArrayList sample;
        public PingServerReport(String name, int maxPlayers, int playersOnline, ArrayList sample) {
            this.name = name;
            this.maxPlayers = maxPlayers;
            this.playersOnline = playersOnline;
			this.sample = sample;
        }
    }
}
