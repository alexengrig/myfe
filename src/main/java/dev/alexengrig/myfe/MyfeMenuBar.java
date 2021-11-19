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
import dev.alexengrig.myfe.model.LookAndFeelModel;
import dev.alexengrig.myfe.model.event.LookAndFeelModelEvent;
import dev.alexengrig.myfe.model.event.LookAndFeelModelListener;
import dev.alexengrig.myfe.view.FtpConnectDialog;
import dev.alexengrig.myfe.view.event.FeMenuBarEvent;
import dev.alexengrig.myfe.view.event.FeMenuBarListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

public class MyfeMenuBar extends JMenuBar {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<FeMenuBarListener> listeners = new LinkedList<>();

    private final JFrame parentFrame;
    private final LookAndFeelModel lafModel;

    public MyfeMenuBar(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.lafModel = new LookAndFeelModel();
        this.lafModel.addLookAndFeelModelListener(new LafListener());
        add(new FileMenu());
        add(new ViewMenu());
    }

    public void addFeMenuBarListener(FeMenuBarListener listener) {
        listeners.add(listener);
    }

    public void removeFeMenuBarListener(FeMenuBarListener listener) {
        listeners.remove(listener);
    }

    private void fireOpenArchive(FeMenuBarEvent event) {
        LOGGER.debug("Fire open archive: {}", event);
        for (FeMenuBarListener listener : listeners) {
            listener.openArchive(event);
        }
    }

    private void fireConnectToFtpServer(FeMenuBarEvent event) {
        LOGGER.debug("Fire connect to FTP server: {}", event);
        for (FeMenuBarListener listener : listeners) {
            listener.connectToFtpServer(event);
        }
    }

    private void fireChangeLookAndFeel(FeMenuBarEvent event) {
        LOGGER.debug("Fire change Look & Feel: {}", event);
        for (FeMenuBarListener listener : listeners) {
            listener.changeLookAndFeel(event);
        }
    }

    private class LafListener implements LookAndFeelModelListener {

        @Override
        public void change(LookAndFeelModelEvent event) {
            String className = event.getClassName();
            fireChangeLookAndFeel(FeMenuBarEvent.lookAndFeelClassName(className));
        }

    }

    private class FileMenu extends JMenu {

        public FileMenu() {
            super("File");
            add(new OpenArchiveMenuItem());
            add(new ConnectToFtpServerMenuItem());
        }

    }

    private class OpenArchiveMenuItem extends JMenuItem {

        public OpenArchiveMenuItem() {
            setAction(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("Open archive");
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    chooser.setAcceptAllFileFilterUsed(false);
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setFileFilter(new FileNameExtensionFilter("Archive", "JAR", "ZIP"));
                    int result = chooser.showOpenDialog(parentFrame);
                    if (result != JFileChooser.APPROVE_OPTION) {
                        return;
                    }
                    File file = chooser.getSelectedFile();
                    if (!file.exists()) {
                        JOptionPane.showMessageDialog(
                                parentFrame,
                                "Archive doesn't exist: " + file,
                                "Open archive",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    //FIXME: Add spinner
                    fireOpenArchive(FeMenuBarEvent.archivePath(file.getAbsolutePath()));
                }
            });
            setMnemonic('O');
            setText("Open archive...");
        }

    }

    private class ConnectToFtpServerMenuItem extends JMenuItem {

        public ConnectToFtpServerMenuItem() {
            setAction(new ConnectToFtpServerAction());
            setMnemonic('C');
            setText("Connect to FTP server...");
        }

        private void handleConnect(FtpConnectionConfig config) {
            fireConnectToFtpServer(FeMenuBarEvent.ftpConnectionConfig(config));
        }

        private class ConnectToFtpServerAction extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                FtpConnectDialog dialog = new FtpConnectDialog(parentFrame, "Connect to FTP server", ConnectToFtpServerMenuItem.this::handleConnect);
                dialog.setVisible(true);
            }

        }

    }

    private class ViewMenu extends JMenu {

        public ViewMenu() {
            super("View");
            add(new LookAndFeelMenuItem());
        }

    }

    private class LookAndFeelMenuItem extends JMenu {

        public LookAndFeelMenuItem() {
            super("Look & Feel");
            for (String lafName : lafModel.getAllNames()) {
                JRadioButtonMenuItem lafOptionMenuItem = new JRadioButtonMenuItem(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String thisLafName = ((JRadioButtonMenuItem) e.getSource()).getText();
                        lafModel.setByName(thisLafName);
                    }
                });
                lafOptionMenuItem.setText(lafName);
                lafOptionMenuItem.setModel(new JToggleButton.ToggleButtonModel() {
                    @Override
                    public boolean isSelected() {
                        return lafName.equals(lafModel.getCurrentName());
                    }
                });
                add(lafOptionMenuItem);
            }
        }

    }

}
