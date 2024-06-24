/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shenyu.admin.model.event.plugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.admin.model.enums.EventTypeEnum;
import org.apache.shenyu.admin.model.event.BatchChangedEvent;
import org.apache.shenyu.admin.model.vo.PluginNamespaceVO;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * BatchPluginChangedEvent.
 */
public class BatchPluginNamespaceChangedEvent extends BatchChangedEvent {


    /**
     * Create a new {@code PluginChangedEvent}.operator is unknown.
     *
     * @param source   Current plugin state
     * @param before   Before the change plugin state
     * @param type     event type
     * @param operator operator
     */
    public BatchPluginNamespaceChangedEvent(final Collection<PluginNamespaceVO> source, final Collection<PluginNamespaceVO> before, final EventTypeEnum type, final String operator) {
        super(source, before, type, operator);
    }

    @Override
    public String buildContext() {
        PluginNamespaceVO pluginNamespaceVO = ((Collection<?>) getSource())
                .stream()
                .map(s -> (PluginNamespaceVO) s).findFirst().get();
        final String plugins = ((Collection<?>) getSource())
                .stream()
                .map(s -> ((PluginNamespaceVO) s).getName())
                .collect(Collectors.joining(","));

        return String.format("the namespace[%s]  plugins[%s] is %s", pluginNamespaceVO.getNamespaceId(), plugins, StringUtils.lowerCase(getType().getType().toString()));
    }

    @Override
    public String eventName() {
        return "pluginNamespace";
    }
}
