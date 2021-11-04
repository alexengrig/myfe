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

package dev.alexengrig.myfe.model;

import dev.alexengrig.myfe.view.FeContentTable;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Model of file explorer content table.
 *
 * @see FeContentTable
 */
//TODO: Extends AbstractTableModel
public class FeContentTableModel extends DefaultTableModel {

    private List<? extends FePath> paths;
    private String filteredType;

    public FeContentTableModel(List<? extends FePath> paths) {
        super(convertToVector(paths), convertToVector(new Object[]{"Name", "Type"}));
        this.paths = paths;
    }

    private static Vector<Vector<?>> convertToVector(List<? extends FePath> data) {
        return data.stream()
                .map(path -> new Vector<>(List.of(path.getName(), path.getExtension())))
                .collect(Collectors.toCollection(Vector::new));
    }

    public FePath getPathAt(int index) {
        return paths.get(index);
    }

    public void setPaths(List<? extends FePath> paths) {
        this.paths = paths;
        this.filteredType = null;
        setDataVector(convertToVector(paths), columnIdentifiers);
    }

    public String getFilteredType() {
        return filteredType;
    }

    public void setFilteredType(String filteredType) {
        this.filteredType = filteredType;
        fireTableStructureChanged();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}
