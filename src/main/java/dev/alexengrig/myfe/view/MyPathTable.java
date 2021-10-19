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

import dev.alexengrig.myfe.model.MyPath;
import dev.alexengrig.myfe.model.MyPathTableModel;

import javax.swing.*;
import java.util.function.Consumer;

public class MyPathTable extends JTable {

    //TODO: NPE
    private Consumer<MyPath> selectPathHandler;

    public MyPathTable(MyPathTableModel model) {
        super(model);
        init();
    }

    @Override
    public MyPathTableModel getModel() {
        return (MyPathTableModel) super.getModel();
    }

    private void init() {
        getSelectionModel().addListSelectionListener(e -> {
            if (getSelectedRowCount() != 1) return;
            int rowIndex = getSelectedRow();
            MyPath path = getModel().getPathAt(rowIndex);
            handleSelectPath(path);
        });
    }

    public void onSelectPath(Consumer<MyPath> handler) {
        this.selectPathHandler = handler;
    }

    private void handleSelectPath(MyPath path) {
        selectPathHandler.accept(path);
    }

}
