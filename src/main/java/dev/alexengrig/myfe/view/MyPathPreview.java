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

import dev.alexengrig.myfe.model.MyFile;
import dev.alexengrig.myfe.model.MyPath;
import dev.alexengrig.myfe.model.MyPathModel;
import dev.alexengrig.myfe.model.event.MyPathModelEvent;
import dev.alexengrig.myfe.model.event.MyPathModelListener;
import dev.alexengrig.myfe.service.MyPathPreviewBackgroundService;
import dev.alexengrig.myfe.util.MyPathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandles;

public class MyPathPreview extends JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MyPathModel model;
    private final MyPathPreviewBackgroundService backgroundService;

    public MyPathPreview(MyPathModel model, MyPathPreviewBackgroundService backgroundService) {
        super(new BorderLayout());
        this.model = model;
        this.backgroundService = backgroundService;
        init();
    }

    private void init() {
        addPreviewComponent(model.getPath());
        model.addMyPathModelListener(new ModelListener());
    }

    private void handleChangePath(MyPath path) {
        LOGGER.debug("Handle change path: {}", path);
        removeAll();
        addPreviewComponent(path);
        revalidate(); //FIXME: It's slow
        repaint();
    }

    private void addPreviewComponent(MyPath path) {
        JComponent component = createPreviewComponent(path);
        add(component);
    }

    private JComponent createPreviewComponent(MyPath path) {
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

    private JComponent createImagePreviewComponent(MyFile file) {
        return new MyImage(file);
    }

    private JComponent createTextPreviewComponent(MyFile file) {
        MyText component = new MyText();
        backgroundService.loadTextPreview(file, component::append);
        return component;
    }

    /**
     * Events from model.
     *
     * @see MyPathPreview#handleChangePath(MyPath)
     */
    private class ModelListener implements MyPathModelListener {

        @Override
        public void changePath(MyPathModelEvent event) {
            handleChangePath(event.getPath());
        }

    }

}
