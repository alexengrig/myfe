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

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class MyPathTableHeader extends JTableHeader {

    public MyPathTableHeader(TableColumnModel columnModel) {
        super(columnModel);
        init();
    }

    private void init() {
        TableColumn column = columnModel.getColumn(1); //FIXME: Magic number
        column.setHeaderRenderer(new MyTableCellHeaderRenderer());
    }

    private static class MyTableCellHeaderRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            //TODO: Add combobox
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

    }

}
