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

import dev.alexengrig.myfe.domain.FtpConnectionConfig;
import dev.alexengrig.myfe.view.FeTab;
import dev.alexengrig.myfe.view.FeTabFactory;
import dev.alexengrig.myfe.view.FeTabbedPane;
import dev.alexengrig.myfe.view.event.FeMenuBarEvent;
import dev.alexengrig.myfe.view.event.FeMenuBarListener;
import dev.alexengrig.myfe.view.event.FeTabEvent;
import dev.alexengrig.myfe.view.event.FeTabListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.net.URL;

/**
 * Main frame of file explorer.
 */
public final class MyfeApplication extends JFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String TITLE = "myfe";
    private static final String ICON_FILENAME = "myfe.png";

    private final FeTabbedPane tabbedPane = new FeTabbedPane();
    //TODO: DI
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
        setIcon();
        componentsInit();
    }

    private void setIcon() {
        URL resource = MyfeApplication.class.getClassLoader().getResource(ICON_FILENAME);
        if (resource == null) {
            throw new IllegalStateException("No " + ICON_FILENAME);
        }
        ImageIcon imageIcon = new ImageIcon(resource);
        setIconImage(imageIcon.getImage());
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
            LOGGER.error("Exception of changing Look & Feel: {}", lafClassName, e);
        }
    }

    private void handleOpenArchive(String path) {
        String tabTitle = tabFactory.getArchiveTabTitle(path);
        if (tabbedPane.hasTab(tabTitle)) {
            tabbedPane.openTab(tabTitle);
        } else {
            try {
                FeTab tab = tabFactory.createArchiveTab(path);
                tabbedPane.openNewTab(tab);
            } catch (Exception e) {
                LOGGER.error("Exception of open archive {}", path, e);
                JOptionPane.showMessageDialog(
                        this,
                        e.getMessage(),
                        "Open archive " + path,
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleConnectToFTPServer(FtpConnectionConfig connectionConfig) {
        String tabTitle = tabFactory.getFtpTabTitle(connectionConfig);
        if (tabbedPane.hasTab(tabTitle)) {
            tabbedPane.openTab(tabTitle);
        } else {
            try {
                FeTab tab = tabFactory.createFtpTab(connectionConfig);
                tabbedPane.openNewTab(tab);
            } catch (Exception e) {
                LOGGER.error("Exception of connection to FTP server {}", connectionConfig.getInfo(), e);
                JOptionPane.showMessageDialog(
                        this,
                        e.getMessage(),
                        "Connect to FTP server " + connectionConfig.getInfo(),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
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
