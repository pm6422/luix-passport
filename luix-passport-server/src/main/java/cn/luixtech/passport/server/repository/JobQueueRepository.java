package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.JobQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data JPA repository for the {@link JobQueue} entity.
 */
@Repository
public interface JobQueueRepository extends JpaRepository<JobQueue, String> {

    @Query(nativeQuery = true,
            value = "SELECT * FROM job_queue WHERE status = 'pending' " +
                    "ORDER BY created_at ASC FOR UPDATE SKIP LOCKED LIMIT 1")
    JobQueue findNextPendingJob();

    @Query(nativeQuery = true,
            value = "SELECT * FROM job_queue WHERE status = 'pending' " +
                    "ORDER BY created_at ASC FOR UPDATE SKIP LOCKED LIMIT :limit")
    List<JobQueue> findPendingJobs(@Param("limit") int limit);

}