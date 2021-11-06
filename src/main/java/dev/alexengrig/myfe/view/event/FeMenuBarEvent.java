/*
 * Copyright 2021 Alexengrig Dev.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.alexengrig.myfe.view.event;

import dev.alexengrig.myfe.config.FtpConnectionConfig;

public class FeMenuBarEvent {

    private final String archivePath;
    private final FtpConnectionConfig ftpConnectionConfig;
    private final String lookAndFeelClassName;

    public FeMenuBarEvent(String archivePath, FtpConnectionConfig ftpConnectionConfig, String lookAndFeelClassName) {
        this.archivePath = archivePath;
        this.ftpConnectionConfig = ftpConnectionConfig;
        this.lookAndFeelClassName = lookAndFeelClassName;
    }

    public static FeMenuBarEvent archivePath(String archivePath) {
        return new FeMenuBarEvent(archivePath, null, null);
    }

    public static FeMenuBarEvent ftpConnectionConfig(FtpConnectionConfig ftpConnectionConfig) {
        return new FeMenuBarEvent(null, ftpConnectionConfig, null);
    }

    public static FeMenuBarEvent lookAndFeelClassName(String className) {
        return new FeMenuBarEvent(null, null, className);
    }

    public String getArchivePath() {
        return archivePath;
    }

    public FtpConnectionConfig getFtpConnectionConfig() {
        return ftpConnectionConfig;
    }

    public String getLookAndFeelClassName() {
        return lookAndFeelClassName;
    }

}
