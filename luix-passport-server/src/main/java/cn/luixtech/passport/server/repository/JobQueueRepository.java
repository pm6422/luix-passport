package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.JobQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data JPA repository for the {@link JobQueue} entity.
 */
@Repository
public interface JobQueueRepository extends JpaRepository<JobQueue, String> {

    List<JobQueue> findByStatusOrderByCreatedAtAsc(String status);

    @Transactional
    @Modifying
    @Query("UPDATE JobQueue j SET j.status = 'processing', j.processedAt = :now " +
            "WHERE j.id = (SELECT j2.id FROM JobQueue j2 WHERE j2.status = 'pending' " +
            "ORDER BY j2.createdAt ASC LIMIT 1)")
    int lockOldestPendingJob(Instant now);

    @Query(nativeQuery = true,
            value = "SELECT * FROM job_queue WHERE status = 'pending' " +
                    "ORDER BY created_at ASC FOR UPDATE SKIP LOCKED LIMIT 1")
    JobQueue findNextPendingJob();
}