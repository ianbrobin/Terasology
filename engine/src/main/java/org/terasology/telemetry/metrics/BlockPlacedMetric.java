/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.telemetry.metrics;

import com.snowplowanalytics.snowplow.tracker.events.Unstructured;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.CoreRegistry;
import org.terasology.telemetry.GamePlayStatsComponent;
import org.terasology.telemetry.TelemetryCategory;
import org.terasology.telemetry.TelemetryField;

import java.util.Map;
import java.util.Optional;

/**
 * A players statistic metric for blocks placed.
 */
@TelemetryCategory(id = "blockPlaced",
        displayName = "${engine:menu#telemetry-block-placed}",
        isOneMapMetric = true
)
public final class BlockPlacedMetric extends Metric {

    public static final String SCHEMA_BLOCK_PLACED = "iglu:org.terasology/blockPlaced/jsonschema/1-0-0";

    // The telemetry field is not actually used here, it's for documentation.
    @TelemetryField
    private Map blockPlacedMap;

    private LocalPlayer localPlayer;

    @Override
    public Optional<Unstructured> getUnstructuredMetric() {
        createTelemetryFieldToValue();
        return getUnstructuredMetric(SCHEMA_BLOCK_PLACED, telemetryFieldToValue);
    }

    @Override
    public Map<String, ?> createTelemetryFieldToValue() {
        localPlayer = CoreRegistry.get(LocalPlayer.class);
        EntityRef playerEntity = localPlayer.getCharacterEntity();
        if (playerEntity.hasComponent(GamePlayStatsComponent.class)) {
            GamePlayStatsComponent gamePlayStatsComponent = playerEntity.getComponent(GamePlayStatsComponent.class);
            telemetryFieldToValue.clear();
            telemetryFieldToValue.putAll(gamePlayStatsComponent.blockPlacedMap);
            return telemetryFieldToValue;
        } else {
            return telemetryFieldToValue;
        }
    }
}
