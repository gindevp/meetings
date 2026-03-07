package com.gindevp.meeting.service.mapper;

import com.gindevp.meeting.domain.Setting;
import com.gindevp.meeting.service.dto.SettingDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SettingMapper extends EntityMapper<SettingDTO, Setting> {}
