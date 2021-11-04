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

import dev.alexengrig.myfe.model.FeFooterModel;
import dev.alexengrig.myfe.model.event.FeFooterModelEvent;
import dev.alexengrig.myfe.model.event.FeFooterModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandles;

public class FeFooter extends JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FeFooterModel model;
    private final JPanel contentPane;

    public FeFooter(FeFooterModel model) {
        super(new BorderLayout());
        this.model = model;
        this.contentPane = new JPanel(new BorderLayout());
        init();
    }

    private void init() {
        add(contentPane);
        model.addFeFooterModelListener(new ModelListener());
        handleChangeNumberOfElements(model.getNumberOfElements());
    }

    private void addCounterComponent() {
        int numberOfElements = model.getNumberOfElements();
        contentPane.add(new JLabel(createCounterText(numberOfElements)), BorderLayout.WEST);
    }

    private String createCounterText(int count) {
        if (count == 1) {
            return "1 element";
        } else {
            return count + " elements";
        }
    }

    private void handleChangeNumberOfElements(Integer numberOfElements) {
        LOGGER.debug("Handle change number of elements: {}", numberOfElements);
        contentPane.removeAll();
        if (numberOfElements != null) {
            addCounterComponent();
        }
        contentPane.revalidate();
        contentPane.repaint();
    }

    private class ModelListener implements FeFooterModelListener {

        @Override
        public void changeNumberOfElements(FeFooterModelEvent event) {
            Integer numberOfElements = event.getNumberOfElements();
            handleChangeNumberOfElements(numberOfElements);
        }

    }

}
