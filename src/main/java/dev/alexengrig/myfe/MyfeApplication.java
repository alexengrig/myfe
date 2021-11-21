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

package dev.alexengrig.myfe;

import dev.alexengrig.myfe.config.FtpConnectionConfig;
import dev.alexengrig.myfe.view.FeTab;
import dev.alexengrig.myfe.view.FeTabFactory;
import dev.alexengrig.myfe.view.FeTabbedPane;
import dev.alexengrig.myfe.view.event.FeMenuBarEvent;
import dev.alexengrig.myfe.view.event.FeMenuBarListener;
import dev.alexengrig.myfe.view.event.FeTabEvent;
import dev.alexengrig.myfe.view.event.FeTabListener;

import javax.swing.*;
import java.awt.*;

public final class MyfeApplication extends JFrame {

    private static final String TITLE = "myfe";

    private final FeTabbedPane tabbedPane = new FeTabbedPane();
    private final FeTabFactory tabFactory = new FeTabFactory();

    public MyfeApplication() {
        super(TITLE);
        init();
        pack();
        setLocationRelativeTo(null); // center of screen
        setVisible(true);
    }

    private void init() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(createFramePreferredSize());
        componentsInit();
    }

    private Dimension createFramePreferredSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Dimension(screenSize.width / 2, screenSize.height / 2);
    }

    private void componentsInit() {
        menuBarInit();
        tabbedPaneInit();
    }

    private void menuBarInit() {
        MyfeMenuBar menuBar = new MyfeMenuBar(this);
        menuBar.addFeMenuBarListener(new MenuBarListener());
        setJMenuBar(menuBar);
    }

    private void tabbedPaneInit() {
        FeTab defaultTab = tabFactory.createDefaultTab();
        defaultTab.addFeTabListener(new TabListener());
        tabbedPane.setDefaultTab(defaultTab);
        add(tabbedPane);
    }

    private void handleChangeLookAndFeel(String lafClassName) {
        try {
            UIManager.setLookAndFeel(lafClassName);
            SwingUtilities.updateComponentTreeUI(this);
            pack();
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void handleOpenArchive(String path) {
        String tabTitle = tabFactory.getArchiveTabTitle(path);
        if (tabbedPane.hasTab(tabTitle)) {
            tabbedPane.openTab(tabTitle);
        } else {
            FeTab archiveTab = tabFactory.createArchiveTab(path);
            tabbedPane.openTab(archiveTab);
        }
    }

    private void handleConnectToFTPServer(FtpConnectionConfig connectionConfig) {
        FeTab ftpTab = tabFactory.createFtpTab(connectionConfig);
        tabbedPane.openTab(ftpTab);
    }

    private class MenuBarListener implements FeMenuBarListener {

        @Override
        public void changeLookAndFeel(FeMenuBarEvent event) {
            handleChangeLookAndFeel(event.getLookAndFeelClassName());
        }

        @Override
        public void openArchive(FeMenuBarEvent event) {
            handleOpenArchive(event.getArchivePath());
        }

        @Override
        public void connectToFtpServer(FeMenuBarEvent event) {
            handleConnectToFTPServer(event.getFtpConnectionConfig());
        }

    }

    private class TabListener implements FeTabListener {

        @Override
        public void openArchive(FeTabEvent event) {
            handleOpenArchive(event.getFile().getPath());
        }

    }

}
