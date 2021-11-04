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
import dev.alexengrig.myfe.model.FePath;
import dev.alexengrig.myfe.model.SelectedFePathModel;
import dev.alexengrig.myfe.model.event.SelectedFePathModelEvent;
import dev.alexengrig.myfe.model.event.SelectedFePathModelListener;
import dev.alexengrig.myfe.service.ContentPreviewBackgroundService;
import dev.alexengrig.myfe.util.MyPathUtil;
import dev.alexengrig.myfe.util.logging.LazyLogger;
import dev.alexengrig.myfe.util.logging.LazyLoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandles;

public class MyPathPreview extends JPanel {

    private static final LazyLogger LOGGER = LazyLoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final SelectedFePathModel model;
    private final ContentPreviewBackgroundService backgroundService;

    public MyPathPreview(SelectedFePathModel model, ContentPreviewBackgroundService backgroundService) {
        super(new BorderLayout());
        this.model = model;
        this.backgroundService = backgroundService;
        init();
    }

    private void init() {
        addPreviewComponent(model.getPath());
        model.addSelectedFePathModelListener(new ModelListener());
    }

    private void handleChangePath(FePath path) {
        LOGGER.debug("Handle change path: {}", path);
        removeAll();
        addPreviewComponent(path);
        revalidate(); //FIXME: It's slow
        repaint();
    }

    private void addPreviewComponent(FePath path) {
        JComponent component = createPreviewComponent(path);
        add(component);
    }

    private JComponent createPreviewComponent(FePath path) {
        if (path == null) {
            return createEmptyPreviewComponent();
        } else {
            if (path.isFile()) {
                if (MyPathUtil.isImage(path.asFile())) {
                    return createImagePreviewComponent(path.asFile());
                } else if (MyPathUtil.isText(path.asFile())) {
                    return createTextPreviewComponent(path.asFile());
                }
            }
            return createAvailablePreviewComponent();
        }
    }

    private JComponent createEmptyPreviewComponent() {
        return new JTextArea("Select an element to preview");
    }

    private JComponent createAvailablePreviewComponent() {
        return new JTextArea("No preview available");
    }

    private JComponent createImagePreviewComponent(FeFile file) {
        return new MyImage(file);
    }

    private JComponent createTextPreviewComponent(FeFile file) {
        MyText component = new MyText();
        backgroundService.loadTextPreview(file, str -> {
            LOGGER.debug("Got preview text for: {}", file);
            component.append(str);
        });
        return component;
    }

    /**
     * Events from model.
     *
     * @see MyPathPreview#handleChangePath(FePath)
     */
    private class ModelListener implements SelectedFePathModelListener {

        @Override
        public void changePath(SelectedFePathModelEvent event) {
            handleChangePath(event.getPath());
        }

    }

}
