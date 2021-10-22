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

import dev.alexengrig.myfe.view.MyTab;
import dev.alexengrig.myfe.view.MyTabFactory;
import dev.alexengrig.myfe.view.MyTabbedPane;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class MyfeApplication extends JFrame {

    private static final String TITLE = "myfe";

    private final MyTabFactory tabFactory = new MyTabFactory();

    private MyTabbedPane tabbedPane;

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
        setJMenuBar(new MyMenuBar());
    }

    private void tabbedPaneInit() {
        MyTab defaultTab = tabFactory.createDefaultTab();
        defaultTab.component().addMyTabComponentListener(event -> handleOpenArchive(Paths.get(event.getFile().getPath())));
        tabbedPane = new MyTabbedPane(defaultTab);
        add(tabbedPane);
    }

    private void handleOpenArchive(Path path) {
        MyTab archiveTab = tabFactory.createArchiveTab(path);
        tabbedPane.addMyTab(archiveTab);
    }

    private class MyMenuBar extends JMenuBar {

        public MyMenuBar() {
            JMenu menu = createFileMenu();
            add(menu);
        }

        private JMenu createFileMenu() {
            JMenu menu = new JMenu("File");
            JMenuItem openArchiveMenuItem = new JMenuItem(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("Open archive");
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    chooser.setAcceptAllFileFilterUsed(false);
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setFileFilter(new FileNameExtensionFilter("Archive", "JAR", "ZIP"));
                    int result = chooser.showOpenDialog(MyfeApplication.this);
                    if (result != JFileChooser.APPROVE_OPTION) {
                        return;
                    }
                    File file = chooser.getSelectedFile();
                    if (!file.exists()) {
                        JOptionPane.showMessageDialog(
                                MyfeApplication.this,
                                "Archive doesn't exist: " + file,
                                "Open archive",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    handleOpenArchive(file.toPath());
                }
            });
            openArchiveMenuItem.setMnemonic('O');
            openArchiveMenuItem.setText("Open archive...");
            menu.add(openArchiveMenuItem);
            return menu;
        }

    }

}
