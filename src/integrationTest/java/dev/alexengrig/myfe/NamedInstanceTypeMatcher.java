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

package dev.alexengrig.myfe;

import org.assertj.swing.core.GenericTypeMatcher;

import java.awt.*;

public class NamedInstanceTypeMatcher<T extends Component> extends GenericTypeMatcher<T> {

    private final String name;

    public NamedInstanceTypeMatcher(Class<T> supportedType, String name) {
        super(supportedType);
        this.name = name;
    }

    @Override
    protected boolean isMatching(T component) {
        // Check instanceof in org.assertj.swing.core.GenericTypeMatcher#matches
        return name.equals(component.getName());
    }

}
