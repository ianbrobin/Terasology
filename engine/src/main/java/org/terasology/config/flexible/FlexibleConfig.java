/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.config.flexible;

import com.google.common.collect.Maps;
import org.terasology.assets.ResourceUrn;

import java.util.Map;

public class FlexibleConfig {
    private Map<Key<?>, Setting> settingMap;

    public FlexibleConfig() {
        this.settingMap = Maps.newHashMap();
    }

    public <V> Key<V> add(Setting<V> setting) {
        Key<V> key = new Key<>(setting.getId(), setting.getValue());

        // Maybe throw an exception?
        if (has(key))
            return null;

        settingMap.put(key, setting);
        return key;
    }

    public boolean remove(Key<?> id) {
        Setting setting = get(id);

        if (setting == null || setting.hasSubscribers())
            return false;

        settingMap.remove(id);
        return true;
    }

    public <V> Setting<V> get(Key<V> key) {
        Setting setting = settingMap.get(key);

        return key.vClass.isInstance(setting.getValue()) ? Setting.cast(setting, key.vClass) : null;
    }

    public boolean has(Key<?> id) {
        return settingMap.containsKey(id);
    }

    static final class Key<V> {
        private final ResourceUrn key;
        private final Class<V> vClass;

        @SuppressWarnings("unchecked")
        Key(ResourceUrn key, V value) {
            // this cast will always be safe unless the outside world is doing something fishy like using raw types
            this(key, (Class<V>) value.getClass());
        }

        Key(ResourceUrn key, Class<V> vClass) {
            this.key = key;
            this.vClass = vClass;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof Key<?>) && ((Key<?>) o).key.equals(key);
        }
    }
}
