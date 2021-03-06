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
package org.terasology.web.io;

import com.google.gson.JsonElement;
import org.terasology.web.resources.EventEmittingResource;
import org.terasology.web.resources.ReadableResource;
import org.terasology.web.resources.ResourceAccessException;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

class JsonSessionResourceObserver implements Consumer<ReadableResource>, BiConsumer<EventEmittingResource, Object> {

    private JsonSession session;
    private BiConsumer<String, JsonElement> readableResourceObserver;
    private BiConsumer<String, JsonElement> eventResourceObserver;

    JsonSessionResourceObserver(JsonSession session) {
        this.session = session;
    }

    void setReadableResourceObserver(BiConsumer<String, JsonElement> readableResourceObserver) {
        this.readableResourceObserver = readableResourceObserver;
    }

    void setEventResourceObserver(BiConsumer<String, JsonElement> eventResourceObserver) {
        this.eventResourceObserver = eventResourceObserver;
    }

    //for readable resource updates
    @Override
    public void accept(ReadableResource resource) {
        if (readableResourceObserver != null) {
            JsonElement newData;
            try {
                newData = session.readResource(resource);
            } catch (ResourceAccessException ex) {
                return;
            }
            readableResourceObserver.accept(resource.getName(), newData);
        }
    }

    //for events
    @Override
    public void accept(EventEmittingResource resource, Object eventData) {
        if (eventResourceObserver != null) {
            eventResourceObserver.accept(resource.getName(), session.serializeEvent(eventData));
        }
    }
}
