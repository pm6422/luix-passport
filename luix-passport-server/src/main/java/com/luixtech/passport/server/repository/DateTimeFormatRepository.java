package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.DateTimeFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DateTimeFormatRepository extends JpaRepository<DateTimeFormat, String> {

    Optional<DateTimeFormat> findByPresetIsTrue();
}
