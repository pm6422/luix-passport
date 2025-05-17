package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.SupportedTimezone;

import java.util.List;

public interface SupportedTimezoneService {
    void updateUtcOffset();

    List<SupportedTimezone> findAll();
}
