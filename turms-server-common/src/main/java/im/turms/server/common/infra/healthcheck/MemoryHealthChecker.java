/*
 * Copyright (C) 2019 The Turms Project
 * https://github.com/turms-im/turms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.turms.server.common.infra.healthcheck;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.OperatingSystemMXBean;
import com.sun.management.VMOption;
import im.turms.server.common.infra.logging.core.logger.Logger;
import im.turms.server.common.infra.logging.core.logger.LoggerFactory;
import im.turms.server.common.infra.logging.core.model.LogLevel;
import im.turms.server.common.infra.property.env.common.healthcheck.MemoryHealthCheckProperties;
import io.netty.util.internal.PlatformDependent;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.List;
import java.util.Optional;

/**
 * @author James Chen
 * @implNote JVM Total Memory:
 * 1. Off-Heap:
 * a. Mapped files, b. Direct buffers c. Java stacks d. Metaspace (Class metadata: Constant pool, Field & Method data),
 * e. Native code (JVM internal use only): PC registers, Native method stacks, Code cache,
 * Structures used & allocated by native libraries(e.g IO libraries), Shared libraries of the JVM, etc.
 * f. etc
 * 2. Heap (eden, survivor, old)
 * <a href="https://www.oracle.com/technetwork/tutorials/tutorials-1876574.html">
 * Getting Started with the G1 Garbage Collector</a>
 * @see jdk.internal.access.JavaNioAccess#getDirectBufferPool
 * @see io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
 */
public final class MemoryHealthChecker extends HealthChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryHealthChecker.class);

    private final OperatingSystemMXBean operatingSystemBean;

    private final long maxAvailableMemory;
    private final long maxAvailableDirectMemory;
    private final long maxDirectMemory;
    private final long maxHeapMemory;
    private final int minFreeSystemMemory;
    private final long totalPhysicalMemorySize;

    private long usedAvailableMemory;
    private long usedDirectMemory;
    private long usedHeapMemory;
    private long usedNonHeapMemory;
    private long usedSystemMemory;
    private long freeSystemMemory;

    private final BufferPoolMXBean directBufferPoolBean;
    private final MemoryMXBean memoryMXBean;

    private final int directMemoryWarningThresholdPercentage;
    private final int heapMemoryWarningThresholdPercentage;
    private final int minMemoryWarningIntervalMillis;
    private long lastDirectMemoryWarningTimestamp;
    private long lastHeapMemoryWarningTimestamp;

    private final int heapMemoryGcThresholdPercentage;
    private final int minHeapMemoryGcIntervalMillis;
    private long lastHeapMemoryGcTimestamp;

    public MemoryHealthChecker(MemoryHealthCheckProperties properties) {
        operatingSystemBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        HotSpotDiagnosticMXBean diagnosticBean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        VMOption disableExplicitGC = diagnosticBean.getVMOption("DisableExplicitGC");
        if (!"false".equals(disableExplicitGC.getValue())) {
            throw new IllegalStateException("\"DisableExplicitGC\" is enabled while it should be disabled");
        }
        // update memory properties
        List<BufferPoolMXBean> poolBeans = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
        Optional<BufferPoolMXBean> pool = poolBeans
                .stream()
                .filter(bean -> "direct".equals(bean.getName()))
                .findFirst();
        if (pool.isEmpty()) {
            List<String> names = poolBeans
                    .stream()
                    .map(BufferPoolMXBean::getName)
                    .toList();
            String s = "Cannot find the direct buffer pool management bean from the pool beans: "
                    + String.join(", ", names);
            throw new IllegalStateException(s);
        }
        directBufferPoolBean = pool.get();
        memoryMXBean = ManagementFactory.getMemoryMXBean();

        // "-XX:MaxDirectMemorySize" or "Runtime.getRuntime().maxMemory()"
        maxDirectMemory = PlatformDependent.maxDirectMemory();
        if (maxDirectMemory < 0) {
            throw new IllegalStateException("Cannot detect the max direct memory: " + maxDirectMemory);
        }
        maxAvailableDirectMemory = (long) (maxDirectMemory * (properties.getMaxAvailableDirectMemoryPercentage() / 100F));
        maxHeapMemory = memoryMXBean.getHeapMemoryUsage().getMax();
        if (maxHeapMemory < 0) {
            throw new IllegalStateException("Cannot detect the max heap memory: " + maxHeapMemory);
        }
        totalPhysicalMemorySize = operatingSystemBean.getTotalMemorySize();
        maxAvailableMemory = (long) (totalPhysicalMemorySize * (properties.getMaxAvailableMemoryPercentage() / 100F));
        int minAvailableMemory = 1000 * 1024 * 1024;
        if (maxAvailableMemory < minAvailableMemory) {
            throw new IllegalStateException("The max available memory is too small to run. Actual: %s. Expected: >= %s"
                    .formatted(asMbString(maxAvailableMemory), asMbString(minAvailableMemory)));
        }
        if (maxAvailableMemory < maxHeapMemory) {
            throw new IllegalStateException("The max available memory %s should not be less than the max heap memory %s"
                    .formatted(asMbString(maxAvailableMemory), asMbString(maxHeapMemory)));
        }
        int estimatedMaxNonHeapMemory = 256 * 1024 * 1024;
        if (maxAvailableMemory > maxAvailableDirectMemory + maxHeapMemory + estimatedMaxNonHeapMemory) {
            LOGGER.warn("The max available memory %s is larger than the total of the available direct memory %s, the max heap memory %s, and the estimated max non-heap memory %s, "
                    .formatted(asMbString(maxAvailableMemory), asMbString(maxAvailableDirectMemory), asMbString(maxHeapMemory), asMbString(estimatedMaxNonHeapMemory))
                    + "which indicates that some memory will never be used by the server");
        }

        this.directMemoryWarningThresholdPercentage = properties.getDirectMemoryWarningThresholdPercentage();
        this.heapMemoryWarningThresholdPercentage = properties.getHeapMemoryWarningThresholdPercentage();
        this.minMemoryWarningIntervalMillis = properties.getMinMemoryWarningIntervalSeconds() * 1000;
        this.minFreeSystemMemory = properties.getMinFreeSystemMemoryBytes();
        this.heapMemoryGcThresholdPercentage = properties.getHeapMemoryGcThresholdPercentage();
        this.minHeapMemoryGcIntervalMillis = properties.getMinHeapMemoryGcIntervalSeconds() * 1000;
    }

    @Override
    public boolean isHealthy() {
        return usedAvailableMemory < maxAvailableMemory
                || usedDirectMemory < maxAvailableDirectMemory
                || freeSystemMemory > minFreeSystemMemory;
    }

    @Override
    public void updateHealthStatus() {
        // No need to call "UnpooledByteBufAllocator.DEFAULT.metric().usedDirectMemory()"
        // and "PooledByteBufAllocator.DEFAULT.metric().usedDirectMemory()"
        // because we have requested Netty to create DirectBuffer instances via its constructor with the counter supported by JDK
        usedDirectMemory = directBufferPoolBean.getMemoryUsed();
        usedHeapMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
        // Non-heap memory pools: [CodeHeap 'non-nmethods', CodeHeap 'non-profiled nmethods', CodeHeap 'profiled nmethods',
        // Compressed Class Space, Metaspace]
        // via ManagementFactory.getMemoryPoolMXBeans().stream().filter(bean -> bean.getType() == MemoryType.NON_HEAP)
        // .map(MemoryPoolMXBean::getName).sorted().toList().toString()
        usedNonHeapMemory = memoryMXBean.getNonHeapMemoryUsage().getUsed();
        usedAvailableMemory = usedDirectMemory + usedHeapMemory + usedNonHeapMemory;
        freeSystemMemory = operatingSystemBean.getFreeMemorySize();
        usedSystemMemory = totalPhysicalMemorySize - freeSystemMemory;

        tryLog();
    }

    private void tryLog() {
        boolean isHealthy = isHealthy();
        LogLevel logLevel = isHealthy ? LogLevel.DEBUG : LogLevel.WARN;
        if (LOGGER.isEnabled(logLevel)) {
            LOGGER.log(logLevel, "Used system memory: {}/{}; "
                            + "Used available memory: {}/{}; "
                            + "Used direct memory: {}/{}/{}; "
                            + "Used heap memory: {}/{}; "
                            + "Used non-heap memory: {}",
                    asMbString(usedSystemMemory),
                    asMbString(totalPhysicalMemorySize),

                    asMbString(usedAvailableMemory),
                    asMbString(maxAvailableMemory),

                    asMbString(usedDirectMemory),
                    asMbString(maxAvailableDirectMemory),
                    asMbString(maxDirectMemory),

                    asMbString(usedHeapMemory),
                    asMbString(maxHeapMemory),

                    asMbString(usedNonHeapMemory));
        }
        long now = System.currentTimeMillis();
        float usedMemoryPercentage = 100F * usedDirectMemory / maxDirectMemory;
        if (directMemoryWarningThresholdPercentage > 0 && directMemoryWarningThresholdPercentage < usedMemoryPercentage
                && minMemoryWarningIntervalMillis < (now - lastDirectMemoryWarningTimestamp)) {
            lastDirectMemoryWarningTimestamp = now;
            LOGGER.warn("The used direct memory has exceeded the warning threshold: {}/{}/{}/{}",
                    asMbString(usedDirectMemory), asMbString(maxDirectMemory), usedMemoryPercentage, directMemoryWarningThresholdPercentage);
        }
        usedMemoryPercentage = 100F * usedHeapMemory / maxHeapMemory;
        if (heapMemoryWarningThresholdPercentage > 0 && heapMemoryWarningThresholdPercentage < usedMemoryPercentage
                && minMemoryWarningIntervalMillis < (now - lastHeapMemoryWarningTimestamp)) {
            lastHeapMemoryWarningTimestamp = now;
            LOGGER.warn("The used heap memory has exceeded the warning threshold: {}/{}/{}/{}",
                    asMbString(usedHeapMemory), asMbString(maxHeapMemory), usedMemoryPercentage, heapMemoryWarningThresholdPercentage);
        }
        // TODO: add tests
        if (!isHealthy && heapMemoryGcThresholdPercentage > 0 && heapMemoryGcThresholdPercentage < usedMemoryPercentage
                && minHeapMemoryGcIntervalMillis < (now - lastHeapMemoryGcTimestamp)) {
            lastHeapMemoryGcTimestamp = now;
            LOGGER.info("Trying to start GC because the available memory has exceeded and the used heap memory has exceeded the GC threshold: {}/{}/{}/{}",
                    asMbString(usedHeapMemory), asMbString(maxHeapMemory), usedMemoryPercentage, heapMemoryGcThresholdPercentage);
            System.gc();
        }
    }

    private String asMbString(long bytes) {
        return bytes / 1024 / 1024 + "MB";
    }

}
