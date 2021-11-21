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

import dev.alexengrig.myfe.model.FeFile;
import dev.alexengrig.myfe.model.FeFileImageModel;
import dev.alexengrig.myfe.model.FePath;
import dev.alexengrig.myfe.model.FeSelectedPathModel;
import dev.alexengrig.myfe.model.MyTextDocument;
import dev.alexengrig.myfe.model.event.FeSelectedPathModelEvent;
import dev.alexengrig.myfe.model.event.FeSelectedPathModelListener;
import dev.alexengrig.myfe.service.ContentPreviewBackgroundService;
import dev.alexengrig.myfe.util.FePathUtil;
import dev.alexengrig.myfe.util.logging.LazyLogger;
import dev.alexengrig.myfe.util.logging.LazyLoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandles;

/**
 * Preview of {@link FePath}.
 */
public class FePathPreview extends JPanel {

    private static final LazyLogger LOGGER = LazyLoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FeSelectedPathModel model;
    private final ContentPreviewBackgroundService backgroundService;

    private final MyTextDocument textModel;
    private final FeFileImageModel imageModel;

    private final MyText textView;
    private final MyImage imageView;

    public FePathPreview(FeSelectedPathModel model, ContentPreviewBackgroundService backgroundService) {
        super(new GridBagLayout());
        this.model = model;
        this.backgroundService = backgroundService;
        this.textModel = new MyTextDocument();
        this.imageModel = new FeFileImageModel();
        this.textView = new MyText(textModel);
        this.imageView = new MyImage(imageModel);
        init();
    }

    private void init() {
        initListeners();
        addComponents();
        handleChangePath(model.getPath());
    }

    private void initListeners() {
        model.addSelectedFePathModelListener(new ModelListener());
    }

    private void addComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = gbc.weighty = 1.0;
        add(textView, gbc);
        imageView.setVisible(false);
        add(imageView, gbc);
    }

    private void handleChangePath(FePath path) {
        LOGGER.debug("Handle change path: {}", path);
        if (path == null) {
            setNotSelectedFilePreviewText();
        } else {
            if (path.isFile()) {
                FeFile file = path.asFile();
                if (FePathUtil.isImage(file)) {
                    setFileImage(file);
                    return;
                } else if (FePathUtil.isText(file)) {
                    setFilePreviewText(file);
                    return;
                }
            }
            setAvailablePreviewText();
        }
    }

    private void setNotSelectedFilePreviewText() {
        imageView.setVisible(false);
        textModel.setText("Select an element to preview");
        textView.setVisible(true);
    }

    private void setFileImage(FeFile file) {
        backgroundService.loadImageData(file, imageData -> {
            textView.setVisible(false);
            imageModel.setFileData(file, imageData);
            imageView.setVisible(true);
        });
    }

    private void setFilePreviewText(FeFile file) {
        backgroundService.loadTextPreview(file, text -> {
            LOGGER.debug("Got preview text for: {}", file);
            imageView.setVisible(false);
            textModel.setText(text);
            textView.setVisible(true);
        });
    }

    private void setAvailablePreviewText() {
        imageView.setVisible(false);
        textModel.setText("No preview available");
        textView.setVisible(true);
    }

    /**
     * Events from model.
     *
     * @see FePathPreview#handleChangePath(FePath)
     */
    private class ModelListener implements FeSelectedPathModelListener {

        @Override
        public void changePath(FeSelectedPathModelEvent event) {
            handleChangePath(event.getPath());
        }

    }

}
