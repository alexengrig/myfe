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

package dev.alexengrig.myfe.view;

import dev.alexengrig.myfe.config.FtpConnectionConfig;
import dev.alexengrig.myfe.model.MyTextDocument;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FtpConnectDialog extends JDialog {

    private final MyTextDocument hostModel = new MyTextDocument();
    private final MyTextDocument portModel = new MyTextDocument();
    private final MyTextDocument usernameModel = new MyTextDocument();
    private final MyTextDocument passwordModel = new MyTextDocument();

    private final JLabel hostLabel = new JLabel("Host:");
    private final JLabel portLabel = new JLabel("Port:");
    private final JLabel usernameLabel = new JLabel("Username:");
    private final JLabel passwordLabel = new JLabel("Password:");

    private final JTextField hostField = new JTextField(hostModel, null, 0);
    private final JTextField portField = new JTextField(portModel, null, 0);
    private final JTextField usernameField = new JTextField(usernameModel, null, 0);
    private final JTextField passwordField = new JPasswordField(passwordModel, null, 0);

    private final ConnectAction connectAction;

    public FtpConnectDialog(JFrame owner, String title, ConnectAction connectAction) {
        super(owner, title, true);
        this.connectAction = connectAction;
        setName("ftp-connect");
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        init();
        pack();
        setResizable(false);
    }

    private void init() {
        JPanel contentPanel = createContentPanel();
        getContentPane().add(contentPanel);
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        hostField.setName("host");
        contentPanel.add(createLabeledTextField(hostLabel, hostField));
        contentPanel.add(Box.createVerticalStrut(12));
        portField.setName("port");
        contentPanel.add(createLabeledTextField(portLabel, portField));
        contentPanel.add(Box.createVerticalStrut(12));
        usernameField.setName("username");
        contentPanel.add(createLabeledTextField(usernameLabel, usernameField));
        contentPanel.add(Box.createVerticalStrut(12));
        passwordField.setName("password");
        contentPanel.add(createLabeledTextField(passwordLabel, passwordField));
        contentPanel.add(Box.createVerticalStrut(16));
        contentPanel.add(createConnectButton());

        setSize(hostField, portField, usernameField, passwordField);
        setSize(hostLabel, portLabel, usernameLabel, passwordLabel);

        return contentPanel;
    }

    private JPanel createLabeledTextField(JLabel label, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(label);
        panel.add(Box.createHorizontalStrut(12));
        panel.add(textField);
        return panel;
    }

    private JButton createConnectButton() {
        JButton connectButton = new JButton();
        connectButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FtpConnectionConfig config = createFtpConnectionConfig();
                connectAction.connectPerformed(config);
                FtpConnectDialog.this.setVisible(false);
                FtpConnectDialog.this.dispose();
            }
        });
        connectButton.setText("Connect"); // Should be after setAction
        connectButton.setName("connect");
        return connectButton;
    }

    private FtpConnectionConfig createFtpConnectionConfig() {
        String host = hostModel.getText();
        int port = FtpConnectionConfig.DEFAULT_PORT;
        try {
            port = Integer.parseInt(portModel.getText());
        } catch (Exception ignore) {
        }
        String username = usernameModel.getText();
        char[] password = passwordModel.getChars();
        if (username.isBlank()) {
            return FtpConnectionConfig.anonymous(host, port);
        } else {
            return FtpConnectionConfig.user(host, port, username, password);
        }
    }

    private void setSize(JLabel label, JLabel... labels) {
        Dimension size = label.getPreferredSize();
        size.width = 100;
        label.setPreferredSize(size);
        for (JLabel other : labels) {
            other.setPreferredSize(size);
        }
    }

    private void setSize(JTextField field, JTextField... fields) {
        Dimension size = field.getPreferredSize();
        size.width = 100;
        field.setPreferredSize(size);
        for (JTextField other : fields) {
            other.setPreferredSize(size);
        }
    }

    public interface ConnectAction {

        void connectPerformed(FtpConnectionConfig config);

    }

}
