package org.apache.shenyu.admin.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.admin.mapper.NamespaceMapper;
import org.apache.shenyu.admin.mapper.PluginNsRelMapper;
import org.apache.shenyu.admin.model.dto.NamespaceDTO;
import org.apache.shenyu.admin.model.entity.NamespaceDO;
import org.apache.shenyu.admin.model.entity.PluginNsRelDO;
import org.apache.shenyu.admin.model.page.CommonPager;
import org.apache.shenyu.admin.model.page.PageResultUtils;
import org.apache.shenyu.admin.model.query.NamespaceQuery;
import org.apache.shenyu.admin.model.vo.NamespaceVO;
import org.apache.shenyu.admin.model.vo.PluginVO;
import org.apache.shenyu.admin.service.NamespaceService;
import org.apache.shenyu.admin.service.PluginService;
import org.apache.shenyu.admin.transfer.NamespaceTransfer;
import org.apache.shenyu.admin.utils.ShenyuResultMessage;
import org.apache.shenyu.common.dto.PluginData;
import org.apache.shenyu.common.utils.UUIDUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NamespaceServiceImpl implements NamespaceService {

    private NamespaceMapper namespaceMapper;

    private PluginNsRelMapper pluginNsRelMapper;

    private PluginService pluginService;

    public NamespaceServiceImpl(NamespaceMapper namespaceMapper, PluginNsRelMapper pluginNsRelMapper, PluginService pluginService) {
        this.namespaceMapper = namespaceMapper;
        this.pluginNsRelMapper = pluginNsRelMapper;
        this.pluginService = pluginService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NamespaceVO createOrUpdate(NamespaceDTO namespaceDTO) {
        return StringUtils.isBlank(namespaceDTO.getId()) && StringUtils.isBlank(namespaceDTO.getNamespaceId()) ? this.create(namespaceDTO) : this.update(namespaceDTO);
    }

    @Override
    public CommonPager<NamespaceVO> listByPage(NamespaceQuery namespaceQuery) {
        return PageResultUtils.result(namespaceQuery.getPageParameter(),
                () -> namespaceMapper.countByQuery(namespaceQuery),
                () -> namespaceMapper.selectByQuery(namespaceQuery)
                        .stream()
                        .map(NamespaceTransfer.INSTANCE::mapToVo)
                        .collect(Collectors.toList()));
    }

    @Override
    public String delete(String namespaceId) {
        //是否要查
        namespaceMapper.deleteByNamespaceId(namespaceId);
        return ShenyuResultMessage.DELETE_SUCCESS;
    }

    @Override
    public NamespaceVO findById(String namespaceId) {
        return NamespaceTransfer.INSTANCE.mapToVo(namespaceMapper.selectById(namespaceId));
    }

    @Override
    public List<NamespaceVO> list() {
        List<NamespaceDO> namespaceDOS = namespaceMapper.selectAll();
        return namespaceDOS.stream().map(NamespaceTransfer.INSTANCE::mapToVo).collect(Collectors.toList());
    }

    private NamespaceVO create(final NamespaceDTO namespaceDTO) {
        if (namespaceDTO == null) {
            return null;
        }
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        String id = UUIDUtils.getInstance().generateShortUuid();
        //todo:封装到shenyu-common包下
        String namespaceId = UUID.randomUUID().toString().replace("-","");
        NamespaceDO namespaceDO = NamespaceDO.builder()
                .id(id)
                .namespaceId(namespaceId)
                .name(namespaceDTO.getName())
                .description(namespaceDTO.getDescription())
                .dateCreated(currentTime)
                .dateUpdated(currentTime)
                .build();
        List<PluginData> pluginData = pluginService.listAll();
        //创建新命名空间下的默认插件组配置
        List<PluginNsRelDO> pluginNsRelList = pluginData.stream().map(s -> PluginNsRelDO.builder()
                .id(UUIDUtils.getInstance().generateShortUuid())
                .pluginId(s.getId())
                .config(s.getConfig())
                .sort(s.getSort())
                .enabled(s.getEnabled())
                .namespaceId(namespaceId)
                .dateCreated(currentTime)
                .dateUpdated(currentTime)
                .build()).collect(Collectors.toList());
        namespaceMapper.insert(namespaceDO);
        pluginNsRelMapper.batchSave(pluginNsRelList);
        return NamespaceTransfer.INSTANCE.mapToVo(namespaceDO);
    }

    private NamespaceVO update(final NamespaceDTO namespaceDTO) {
        if (Objects.isNull(namespaceDTO) || Objects.isNull(namespaceDTO.getNamespaceId())) {
            return null;
        }
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        NamespaceDO namespaceDO = NamespaceDO.builder()
                .namespaceId(namespaceDTO.getNamespaceId())
                .name(namespaceDTO.getName())
                .description(namespaceDTO.getDescription())
                .dateUpdated(currentTime)
                .build();
        return namespaceMapper.updateSelective(namespaceDO) > 0 ? NamespaceTransfer.INSTANCE.mapToVo(namespaceDO) : null;
    }
}
