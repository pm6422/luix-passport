package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.LoginLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, String> {

  Page<LoginLog> findByUsernameOrderByLoginAtDesc(String username, Pageable pageable);
}
