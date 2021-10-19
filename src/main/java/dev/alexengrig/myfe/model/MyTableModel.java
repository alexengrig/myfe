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

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class MyTableModel extends DefaultTableModel {

    public MyTableModel(List<? extends MyPath> paths, Object[] columnNames) {
        super(convertToVector(paths), convertToVector(columnNames));
    }

    private static Vector<Vector<?>> convertToVector(List<? extends MyPath> data) {
        return data.stream()
                .map(path -> new Vector<>(List.of(path.getName(), path.getExtension())))
                .collect(Collectors.toCollection(Vector::new));
    }

    public void update(List<? extends MyPath> paths) {
        setDataVector(convertToVector(paths), columnIdentifiers);
    }

}
