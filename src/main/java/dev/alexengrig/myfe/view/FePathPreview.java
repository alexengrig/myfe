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
import dev.alexengrig.myfe.domain.FePath;
import dev.alexengrig.myfe.model.FeFileImageModel;
import dev.alexengrig.myfe.model.FeSelectedPathModel;
import dev.alexengrig.myfe.model.MyTextDocument;
import dev.alexengrig.myfe.model.event.FeSelectedPathModelEvent;
import dev.alexengrig.myfe.model.event.FeSelectedPathModelListener;
import dev.alexengrig.myfe.service.ContentPreviewBackgroundService;
import dev.alexengrig.myfe.util.FePathUtil;
import dev.alexengrig.myfe.util.logging.LazyLogger;
import dev.alexengrig.myfe.util.logging.LazyLoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.invoke.MethodHandles;

/**
 * Preview of {@link FePath}.
 */
public class FePathPreview extends JPanel {

    private static final LazyLogger LOGGER = LazyLoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FeSelectedPathModel model;
    private final ContentPreviewBackgroundService previewService;

    private final MyTextDocument textModel;
    private final FeFileImageModel imageModel;

    private final MyText textView;
    private final JButton loadButton;
    private final MyImage imageView;

    private Runnable loadAction;

    public FePathPreview(FeSelectedPathModel model, ContentPreviewBackgroundService previewService) {
        this.model = model;
        this.previewService = previewService;
        this.textModel = new MyTextDocument();
        this.imageModel = new FeFileImageModel();
        this.textView = new MyText(textModel);
        this.imageView = new MyImage(imageModel);
        this.loadButton = new JButton();
        init();
    }

    private void init() {
        initListeners();
        initComponents();
        handleChangePath(model.getPath());
    }

    private void initListeners() {
        model.addSelectedFePathModelListener(new ModelListener());
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        textView.setName("preview-text");
        add(textView);
        loadButton.setName("load");
        loadButton.setAction(new LoadAction());
        loadButton.setText("Load preview");
        loadButton.setAlignmentX(CENTER_ALIGNMENT);
        loadButton.setVisible(false);
        add(loadButton);
        imageView.setVisible(false);
        add(imageView);
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
        loadButton.setVisible(false);
        textModel.setText("Select an element to preview");
        textView.setVisible(true);
    }

    private void setFileImage(FeFile file) {
        loadAction = () -> previewService.loadImageData(file, imageData -> {
            LOGGER.debug("Got image data for: {}", file);
            textView.setVisible(false);
            loadButton.setVisible(false);
            imageModel.setFileData(file, imageData);
            imageView.setVisible(true);
        });
        if (previewService.isLazy()) {
            textView.setVisible(false);
            imageView.setVisible(false);
            loadButton.setEnabled(true);
            loadButton.setText("Load image: " + file.getName());
            loadButton.setVisible(true);
        } else {
            loadAction.run();
        }
    }

    private void setFilePreviewText(FeFile file) {
        loadAction = () -> previewService.loadTextPreview(file, text -> {
            LOGGER.debug("Got preview text for: {}", file);
            loadButton.setVisible(false);
            imageView.setVisible(false);
            textModel.setText(text);
            textView.setVisible(true);
        });
        if (previewService.isLazy()) {
            textView.setVisible(false);
            imageView.setVisible(false);
            loadButton.setEnabled(true);
            loadButton.setText("Load text: " + file.getName());
            loadButton.setVisible(true);
        } else {
            loadAction.run();
        }
    }

    private void setAvailablePreviewText() {
        imageView.setVisible(false);
        loadButton.setVisible(false);
        textModel.setText("No preview available");
        textView.setVisible(true);
    }

    private class LoadAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent ignore) {
            loadButton.setEnabled(false);
            if (loadAction != null) {
                loadAction.run();
            }
        }

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
