/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.engine.test.jobexecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.flowable.engine.impl.asyncexecutor.FindExpiredJobsCmd;
import org.flowable.engine.impl.asyncexecutor.ResetExpiredJobsCmd;
import org.flowable.engine.impl.cmd.AcquireJobsCmd;
import org.flowable.engine.impl.persistence.entity.JobInfoEntity;
import org.flowable.engine.impl.persistence.entity.JobEntity;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.runtime.Job;
import org.flowable.engine.runtime.JobQuery;
import org.flowable.engine.test.Deployment;

/**
 * @author Joram Barrez
 */
public class ResetExpiredJobsTest extends PluggableFlowableTestCase {

    @Deployment
    public void testResetExpiredJobs() {

        // This first test 'mimics' the async executor:
        // first the job will be acquired via the lowlevel API instead of using threads
        // and then they will be reset, using the lowlevel API again.

        Date startOfTestTime = new Date();
        processEngineConfiguration.getClock().setCurrentTime(startOfTestTime);

        // Starting process instance will make one job ready
        runtimeService.startProcessInstanceByKey("myProcess");
        assertEquals(1, managementService.createJobQuery().count());

        // Running the 'reset expired' logic should have no effect now
        int expiredJobsPagesSize = processEngineConfiguration.getAsyncExecutorResetExpiredJobsPageSize();
        List<? extends JobInfoEntity> expiredJobs = managementService.executeCommand(new FindExpiredJobsCmd(expiredJobsPagesSize, processEngineConfiguration.getJobEntityManager()));
        assertEquals(0, expiredJobs.size());
        assertJobDetails(false);

        // Run the acquire logic. This should lock the job
        managementService.executeCommand(new AcquireJobsCmd(processEngineConfiguration.getAsyncExecutor()));
        assertJobDetails(true);

        // Running the 'reset expired' logic should have no effect, the lock time is not yet passed
        expiredJobs = managementService.executeCommand(new FindExpiredJobsCmd(expiredJobsPagesSize, processEngineConfiguration.getJobEntityManager()));
        assertEquals(0, expiredJobs.size());
        assertJobDetails(true);

        // Move clock to past the lock time
        Date newDate = new Date(startOfTestTime.getTime() + processEngineConfiguration.getAsyncExecutor().getAsyncJobLockTimeInMillis() + 10000);
        processEngineConfiguration.getClock().setCurrentTime(newDate);

        // Running the reset logic should now reset the lock
        expiredJobs = managementService.executeCommand(new FindExpiredJobsCmd(expiredJobsPagesSize,  processEngineConfiguration.getJobEntityManager()));
        assertTrue(expiredJobs.size() > 0);

        List<String> jobIds = new ArrayList<String>();
        for (JobInfoEntity jobEntity : expiredJobs) {
            jobIds.add(jobEntity.getId());
        }

        managementService.executeCommand(new ResetExpiredJobsCmd(jobIds, processEngineConfiguration.getJobEntityManager()));
        assertJobDetails(false);

        // And it can be re-acquired
        managementService.executeCommand(new AcquireJobsCmd(processEngineConfiguration.getAsyncExecutor()));
        assertJobDetails(true);

        // Start two new process instances, those jobs should not be locked
        runtimeService.startProcessInstanceByKey("myProcess");
        runtimeService.startProcessInstanceByKey("myProcess");
        assertEquals(3, managementService.createJobQuery().count());
        assertJobDetails(true);

        List<Job> unlockedJobs = managementService.createJobQuery().unlocked().list();
        assertEquals(2, unlockedJobs.size());
        for (Job job : unlockedJobs) {
            JobEntity jobEntity = (JobEntity) job;
            assertNull(jobEntity.getLockOwner());
            assertNull(jobEntity.getLockExpirationTime());
        }
    }

    protected void assertJobDetails(boolean locked) {
        JobQuery jobQuery = managementService.createJobQuery();

        if (locked) {
            jobQuery.locked();
        }

        Job job = jobQuery.singleResult();
        assertTrue(job instanceof JobEntity);
        JobEntity jobEntity = (JobEntity) job;

        if (locked) {
            assertNotNull(jobEntity.getLockOwner());
            assertNotNull(jobEntity.getLockExpirationTime());
        } else {
            assertNull(jobEntity.getLockOwner());
            assertNull(jobEntity.getLockExpirationTime());
        }
    }

}
