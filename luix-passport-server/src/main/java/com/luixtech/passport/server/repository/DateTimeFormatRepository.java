package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.DateTimeFormat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DateTimeFormatRepository extends JpaRepository<DateTimeFormat, String> {

    Optional<DateTimeFormat> findByPresetIsTrue();
}
