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

import dev.alexengrig.myfe.domain.FeFile;
import dev.alexengrig.myfe.model.FeFileImageModel;
import dev.alexengrig.myfe.model.event.FeFileImageModelEvent;
import dev.alexengrig.myfe.model.event.FeFileImageModelListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Image component.
 */
public class MyImage extends JPanel {

    private final FeFileImageModel model;

    private final ImageCanvas canvas = new ImageCanvas();

    private BufferedImage image;

    public MyImage(FeFileImageModel model) {
        this.model = model;
        init();
    }

    private void init() {
        initListeners();
        setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(canvas);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initListeners() {
        model.addFeFileImageModelListener(new ModelListener());
    }

    private void handleChangeFile(FeFile file, byte[] data) {
        try {
            image = ImageIO.read(new ByteArrayInputStream(data));
            canvas.setToolTipText(file.getName());
            canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
            canvas.revalidate();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Image: " + file.getPath(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ImageCanvas extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, null);
            }
        }

    }

    private class ModelListener implements FeFileImageModelListener {

        @Override
        public void changeFile(FeFileImageModelEvent event) {
            handleChangeFile(event.getFile(), event.getData());
        }

    }

}
