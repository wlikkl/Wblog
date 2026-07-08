package top.naccl.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.naccl.model.vo.Result;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.*;

/**
 * 服务器监控 API
 */
@RestController
@RequestMapping("/admin")
public class ServerMonitorController {

    @GetMapping("/monitor")
    public Result getServerInfo() {
        Map<String, Object> data = new HashMap<>(16);

        // CPU
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        int cpuCores = Runtime.getRuntime().availableProcessors();
        double cpuLoad = osBean.getSystemLoadAverage();
        double cpuUsage = cpuLoad >= 0 ? Math.round(cpuLoad / cpuCores * 10000) / 100.0 : 0;

        Map<String, Object> cpu = new HashMap<>(4);
        cpu.put("cores", cpuCores);
        cpu.put("load", Math.round(cpuLoad * 100) / 100.0);
        cpu.put("usage", Math.min(cpuUsage, 100));
        cpu.put("model", getCPUModel());
        data.put("cpu", cpu);

        // 内存
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();

        // 系统内存 (Linux)
        long[] systemMemory = getSystemMemory();

        Map<String, Object> memory = new HashMap<>(8);
        memory.put("jvmMax", maxMemory);
        memory.put("jvmTotal", totalMemory);
        memory.put("jvmUsed", usedMemory);
        memory.put("jvmFree", freeMemory);
        memory.put("jvmUsage", Math.round((double) usedMemory / maxMemory * 10000) / 100.0);
        memory.put("heapUsed", heapUsage.getUsed());
        memory.put("heapMax", heapUsage.getMax());
        memory.put("nonHeapUsed", nonHeapUsage.getUsed());
        if (systemMemory[0] > 0) {
            memory.put("systemTotal", systemMemory[0]);
            memory.put("systemUsed", systemMemory[1]);
            memory.put("systemFree", systemMemory[2]);
            memory.put("systemUsage", Math.round((double) systemMemory[1] / systemMemory[0] * 10000) / 100.0);
        }
        data.put("memory", memory);

        // 磁盘
        List<Map<String, Object>> disks = new ArrayList<>();
        try {
            for (FileStore store : FileSystems.getDefault().getFileStores()) {
                Map<String, Object> disk = new HashMap<>(6);
                long total = store.getTotalSpace();
                long usable = store.getUsableSpace();
                long used = total - usable;
                disk.put("mount", store.name());
                disk.put("total", total);
                disk.put("usable", usable);
                disk.put("used", used);
                disk.put("usage", total > 0 ? Math.round((double) used / total * 10000) / 100.0 : 0);
                disk.put("type", store.type());
                if (total > 0) {
                    disks.add(disk);
                }
            }
        } catch (IOException e) {
            // ignore
        }
        data.put("disks", disks);

        // 线程
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        Map<String, Object> threads = new HashMap<>(4);
        threads.put("count", threadBean.getThreadCount());
        threads.put("daemon", threadBean.getDaemonThreadCount());
        threads.put("peak", threadBean.getPeakThreadCount());
        threads.put("total", threadBean.getTotalStartedThreadCount());
        data.put("threads", threads);

        // 运行时间
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        data.put("uptime", uptime);

        // 系统信息
        Map<String, Object> sysInfo = new HashMap<>(4);
        sysInfo.put("os", System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        sysInfo.put("java", System.getProperty("java.version"));
        sysInfo.put("hostname", getHostname());
        data.put("system", sysInfo);

        // 网络 IO (从 /proc/net/dev 读取)
        Map<String, Object> network = getNetworkIO();
        data.put("network", network);

        return Result.ok("获取成功", data);
    }

    private String getCPUModel() {
        try {
            File file = new File("/proc/cpuinfo");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("model name")) {
                        reader.close();
                        return line.split(":")[1].trim();
                    }
                }
                reader.close();
            }
        } catch (Exception e) {
            // ignore
        }
        return System.getProperty("os.arch");
    }

    private long[] getSystemMemory() {
        long[] result = new long[3]; // total, used, free
        try {
            File file = new File("/proc/meminfo");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                long memTotal = 0, memFree = 0, buffers = 0, cached = 0;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("MemTotal:")) {
                        memTotal = parseMemInfo(line) * 1024;
                    } else if (line.startsWith("MemFree:")) {
                        memFree = parseMemInfo(line) * 1024;
                    } else if (line.startsWith("Buffers:")) {
                        buffers = parseMemInfo(line) * 1024;
                    } else if (line.startsWith("Cached:")) {
                        cached = parseMemInfo(line) * 1024;
                    }
                }
                reader.close();
                long used = memTotal - memFree - buffers - cached;
                result[0] = memTotal;
                result[1] = Math.max(used, 0);
                result[2] = memFree + buffers + cached;
            }
        } catch (Exception e) {
            // ignore
        }
        return result;
    }

    private long parseMemInfo(String line) {
        String[] parts = line.split(":\\s+");
        if (parts.length >= 2) {
            return Long.parseLong(parts[1].trim().split("\\s+")[0]);
        }
        return 0;
    }

    private String getHostname() {
        try {
            File file = new File("/etc/hostname");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String hostname = reader.readLine();
                reader.close();
                return hostname != null ? hostname.trim() : "unknown";
            }
        } catch (Exception e) {
            // ignore
        }
        return System.getProperty("user.name");
    }

    private Map<String, Object> getNetworkIO() {
        Map<String, Object> result = new HashMap<>(4);
        try {
            File file = new File("/proc/net/dev");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                long totalRx = 0, totalTx = 0;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.contains(":") && !line.startsWith("Inter") && !line.startsWith("face")) {
                        String[] parts = line.split(":\\s+");
                        if (parts.length >= 2) {
                            String iface = parts[0].trim();
                            if (!iface.equals("lo")) { // 排除 loopback
                                String[] stats = parts[1].trim().split("\\s+");
                                if (stats.length >= 10) {
                                    totalRx += Long.parseLong(stats[0]);
                                    totalTx += Long.parseLong(stats[8]);
                                }
                            }
                        }
                    }
                }
                reader.close();
                result.put("rxBytes", totalRx);
                result.put("txBytes", totalTx);
                result.put("rxMB", Math.round(totalRx / 1024.0 / 1024.0 * 100) / 100.0);
                result.put("txMB", Math.round(totalTx / 1024.0 / 1024.0 * 100) / 100.0);
            }
        } catch (Exception e) {
            // ignore
        }
        return result;
    }
}
