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

package org.apache.shenyu.admin.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.admin.model.dto.SelectorDTO;
import org.apache.shenyu.admin.model.entity.SelectorDO;
import org.apache.shenyu.admin.model.page.CommonPager;
import org.apache.shenyu.admin.model.query.SelectorQuery;
import org.apache.shenyu.admin.model.query.SelectorQueryCondition;
import org.apache.shenyu.admin.model.result.ConfigImportResult;
import org.apache.shenyu.admin.model.vo.SelectorVO;
import org.apache.shenyu.admin.utils.Assert;
import org.apache.shenyu.common.dto.SelectorData;
import org.apache.shenyu.common.enums.SelectorTypeEnum;
import org.apache.shenyu.register.common.dto.MetaDataRegisterDTO;

import java.util.List;
import java.util.Objects;

/**
 * this is selector service.
 */
public interface SelectorService extends PageService<SelectorQueryCondition, SelectorVO> {

    /**
     * Register string.
     *
     * @param selectorDTO the selector dto
     * @return the string
     */
    String registerDefault(SelectorDTO selectorDTO);

    /**
     * handler selector need upstream check.
     *
     * @param dto             {@link MetaDataRegisterDTO}
     * @param pluginName      rpc type
     * @param selectorHandler the selector handler
     * @return the id of selector.
     */
    String registerDefault(MetaDataRegisterDTO dto, String pluginName, String selectorHandler);

    /**
     * create or update selector.
     *
     * @param selectorDTO {@linkplain SelectorDTO}
     * @return rows int
     */
    default int createOrUpdate(SelectorDTO selectorDTO) {
        if (Objects.equals(SelectorTypeEnum.CUSTOM_FLOW.getCode(), selectorDTO.getType())) {
            Assert.notNull(selectorDTO.getMatchMode(), "if type is custom, matchMode is not null");
            Assert.notEmpty(selectorDTO.getSelectorConditions(), "if type is custom, selectorConditions is not empty");
            selectorDTO.getSelectorConditions().forEach(selectorConditionDTO -> {
                Assert.notBlack(selectorConditionDTO.getParamType(), "if type is custom, paramType is not empty");
                Assert.notBlack(selectorConditionDTO.getParamName(), "if type is custom, paramName is not empty");
            });
        }
        return StringUtils.isEmpty(selectorDTO.getId()) ? create(selectorDTO) : update(selectorDTO);
    }

    /**
     * create  selector.
     * <ul>
     *     <li>1. create selector[selector]</li>
     *     <li>2. add selector condition[selector_condition]</li>
     *     <li>3. add selector permission [data_permission]</li>
     *     <li>4. update divide upstream</li>
     * </ul>
     *
     * @param selectorDTO {@linkplain SelectorDTO}
     * @return rows int
     */
    int create(SelectorDTO selectorDTO);

    /**
     * update selector.
     *
     * @param selectorDTO {@linkplain SelectorDTO}
     * @return rows int
     */
    int update(SelectorDTO selectorDTO);

    /**
     * update selective selector.
     *
     * @param selectorDO {@linkplain SelectorDO}
     * @return rows int
     */
    int updateSelective(SelectorDO selectorDO);

    /**
     * delete selectors.
     *
     * @param ids         primary key.
     * @param namespaceId namespaceId.
     * @return rows int
     */
    int delete(List<String> ids, String namespaceId);

    /**
     * find selector by id.
     *
     * @param id          primary key.
     * @param namespaceId namespaceId.
     * @return {@linkplain SelectorVO}
     */
    SelectorVO findById(String id, String namespaceId);

    /**
     * find selector by name.
     *
     * @param name        the name
     * @param namespaceId namespaceId.
     *                    Therefore, it is recommended to: {@linkplain SelectorService#findListByName(java.lang.String, java.lang.String)}
     * @return selector do
     * @deprecated sice 2.6.0  Deprecated. By querying under this condition, multiple data are usually obtained.
     */
    @Deprecated
    SelectorDO findByName(String name, String namespaceId);

    /**
     * find selector list by name.
     *
     * @param name        name
     * @param namespaceId namespaceId.
     * @return list
     */
    List<SelectorDO> findListByName(String name, String namespaceId);

    /**
     * Find by name and plugin id selector do.
     *
     * @param name        the name
     * @param pluginName  the plugin name
     * @param namespaceId the namespaceId
     * @return the selector do
     */
    SelectorDO findByNameAndPluginName(String name, String pluginName, String namespaceId);

    /**
     * findByNameAndPluginNameForUpdate.
     *
     * @param name        name
     * @param pluginName  pluginName
     * @param namespaceId namespaceId.
     * @return SelectorDO
     */
    SelectorDO findByNameAndPluginNameForUpdate(String name, String pluginName, String namespaceId);

    /**
     * Find selectorDO list by name and plugin name list.
     *
     * @param name        name
     * @param pluginNames pluginNames
     * @return selectorDO list
     */
    List<SelectorDO> findByNameAndPluginNames(String name, List<String> pluginNames);

    /**
     * Build by name selector data.
     *
     * @param name        the name
     * @param namespaceId namespaceId.
     * @return the selector data
     */
    SelectorData buildByName(String name, String namespaceId);

    /**
     * Build by name selector data.
     *
     * @param name        the name
     * @param pluginName  the plugin name
     * @param namespaceId the namespaceId
     * @return the selector data
     */
    SelectorData buildByName(String name, String pluginName, String namespaceId);

    /**
     * find page of selector by query.
     *
     * @param selectorQuery {@linkplain SelectorQuery}
     * @return {@linkplain CommonPager}
     */
    CommonPager<SelectorVO> listByPageWithPermission(SelectorQuery selectorQuery);

    /**
     * find page of selector by query.
     *
     * @param selectorQuery selectorQuery
     * @return CommonPager
     */
    CommonPager<SelectorVO> listByPage(SelectorQuery selectorQuery);

    /**
     * Find by plugin id list.
     *
     * @param pluginId    the plugin id
     * @param namespaceId the namespaceId
     * @return the list
     */
    List<SelectorData> findByPluginId(String pluginId, String namespaceId);

    /**
     * List all list.
     *
     * @return the list
     */
    List<SelectorData> listAll();

    /**
     * List all export vo list.
     *
     * @return the vo list
     */
    List<SelectorVO> listAllData();

    /**
     * Import the plugin selector list.
     *
     * @param selectorList the plugin selector list
     * @return config import result
     */
    ConfigImportResult importData(List<SelectorDTO> selectorList);

    /**
     * Enabled string.
     *
     * @param ids         the ids
     * @param enabled     the enable
     * @param namespaceId namespaceId
     * @return the result
     */
    Boolean enabled(List<String> ids, Boolean enabled, String namespaceId);
}
