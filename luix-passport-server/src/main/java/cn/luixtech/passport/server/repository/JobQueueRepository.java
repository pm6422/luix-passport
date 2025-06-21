package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.JobQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link JobQueue} entity.
 */
@Repository
public interface JobQueueRepository extends JpaRepository<JobQueue, String> {

    @Query(nativeQuery = true,
            value = "UPDATE job_queue SET status = 'processing', processed_at = NOW() " +
                    "WHERE id = (SELECT id FROM job_queue WHERE status = 'pending' " +
                    "ORDER BY priority DESC, created_at ASC FOR UPDATE SKIP LOCKED LIMIT 1) " +
                    "RETURNING *")
    Optional<JobQueue> lockAndGetNextPendingJob();

    @Query(nativeQuery = true,
            value = "UPDATE job_queue SET status = 'processing', processed_at = NOW() " +
                    "WHERE id IN (SELECT id FROM job_queue WHERE status = 'pending' " +
                    "ORDER BY priority DESC, created_at ASC FOR UPDATE SKIP LOCKED LIMIT :limit) " +
                    "RETURNING *")
    List<JobQueue> lockAndGetNextPendingJobs(@Param("limit") int limit);

    @Query("SELECT j FROM JobQueue j WHERE j.status = :status AND j.broadcastFlag = :broadcastFlag " +
            "ORDER BY j.priority DESC, j.createdAt ASC")
    List<JobQueue> findByStatusAndBroadcast(@Param("status") String status,
                                            @Param("broadcastFlag") boolean broadcastFlag);

}