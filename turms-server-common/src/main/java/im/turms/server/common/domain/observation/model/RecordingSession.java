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

package im.turms.server.common.domain.observation.model;

import im.turms.server.common.domain.observation.exception.DumpIllegalStateException;
import jdk.jfr.Recording;
import jdk.jfr.RecordingState;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

/**
 * @author James Chen
 */
public record RecordingSession(
        Long id,
        Recording recording,
        @Nullable
        String description
) {

    @Nullable
    public Date getCloseDate() {
        RecordingState state = recording.getState();
        return state == RecordingState.CLOSED || state == RecordingState.STOPPED
                ? Date.from(recording.getStopTime()) : null;
    }

    public Path getFilePath() {
        return recording().getDestination();
    }

    public Path getFilePath(File tempFile) throws IOException {
        synchronized (recording) {
            RecordingState state = recording.getState();
            if (state == RecordingState.RUNNING) {
                Path destination = tempFile.toPath();
                recording.dump(destination);
                return destination;
            } else if (state == RecordingState.STOPPED || state == RecordingState.CLOSED) {
                return recording.getDestination();
            } else {
                throw new DumpIllegalStateException("Failed to dump the recording ["
                        + id
                        + "] because it is in the state of \""
                        + state
                        + "\"");
            }
        }
    }

    public void deleteFile() throws IOException {
        Files.deleteIfExists(recording.getDestination().toFile().toPath());
    }

    public boolean checkIfFileExists() {
        return Files.exists(recording.getDestination().toFile().toPath());
    }

    @SneakyThrows
    public void close(boolean keepFile) {
        // 1. All states can be changed to closed
        // 2. close() will call stop() to flush data internally if it is running,
        // so we don't need to call stop() explicitly
        recording.close();
        if (!keepFile) {
            deleteFile();
        }
    }

    public boolean isRunning() {
        return recording.getState() == RecordingState.RUNNING;
    }

}
