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

package org.flowable.dmn.engine.impl;

import java.util.List;
import java.util.Set;

import org.flowable.dmn.api.DmnHistoricDecisionExecution;
import org.flowable.dmn.api.DmnHistoricDecisionExecutionQuery;
import org.flowable.dmn.engine.impl.util.CommandContextUtil;
import org.flowable.engine.common.api.FlowableIllegalArgumentException;
import org.flowable.engine.common.impl.interceptor.CommandContext;
import org.flowable.engine.common.impl.interceptor.CommandExecutor;

/**
 * @author Tijs Rademakers
 */
public class HistoricDecisionExecutionQueryImpl extends AbstractQuery<DmnHistoricDecisionExecutionQuery, DmnHistoricDecisionExecution> implements DmnHistoricDecisionExecutionQuery {

    private static final long serialVersionUID = 1L;
    protected String id;
    protected Set<String> ids;
    protected String decisionDefinitionId;
    protected String decisionKey;
    protected String instanceId;
    protected String executionId;
    protected String activityId;
    protected String tenantId;
    protected String tenantIdLike;
    protected boolean withoutTenantId;

    public HistoricDecisionExecutionQueryImpl() {
    }

    public HistoricDecisionExecutionQueryImpl(CommandContext commandContext) {
        super(commandContext);
    }

    public HistoricDecisionExecutionQueryImpl(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    public DmnHistoricDecisionExecutionQuery id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public DmnHistoricDecisionExecutionQuery ids(Set<String> ids) {
        this.ids = ids;
        return this;
    }

    public DmnHistoricDecisionExecutionQuery decisionDefinitionId(String decisionDefinitionId) {
        if (decisionDefinitionId == null) {
            throw new FlowableIllegalArgumentException("decisionDefinitionId is null");
        }
        this.decisionDefinitionId = decisionDefinitionId;
        return this;
    }
    
    public DmnHistoricDecisionExecutionQuery decisionKey(String decisionKey) {
        if (decisionKey == null) {
            throw new FlowableIllegalArgumentException("decisionKey is null");
        }
        this.decisionKey = decisionKey;
        return this;
    }

    public DmnHistoricDecisionExecutionQuery instanceId(String instanceId) {
        if (instanceId == null) {
            throw new FlowableIllegalArgumentException("instanceId is null");
        }
        this.instanceId = instanceId;
        return this;
    }
    
    public DmnHistoricDecisionExecutionQuery executionId(String executionId) {
        if (executionId == null) {
            throw new FlowableIllegalArgumentException("executionId is null");
        }
        this.executionId = executionId;
        return this;
    }
    
    public DmnHistoricDecisionExecutionQuery activityId(String activityId) {
        if (activityId == null) {
            throw new FlowableIllegalArgumentException("activityId is null");
        }
        this.activityId = activityId;
        return this;
    }

    public DmnHistoricDecisionExecutionQuery tenantId(String tenantId) {
        if (tenantId == null) {
            throw new FlowableIllegalArgumentException("tenantId is null");
        }
        this.tenantId = tenantId;
        return this;
    }

    public DmnHistoricDecisionExecutionQuery tenantIdLike(String tenantIdLike) {
        if (tenantIdLike == null) {
            throw new FlowableIllegalArgumentException("tenantId is null");
        }
        this.tenantIdLike = tenantIdLike;
        return this;
    }

    public DmnHistoricDecisionExecutionQuery withoutTenantId() {
        this.withoutTenantId = true;
        return this;
    }

    // sorting ////////////////////////////////////////////

    public DmnHistoricDecisionExecutionQuery orderByStartTime() {
        return orderBy(HistoricDecisionExecutionQueryProperty.START_TIME);
    }

    public DmnHistoricDecisionExecutionQuery orderByEndTime() {
        return orderBy(HistoricDecisionExecutionQueryProperty.END_TIME);
    }

    public DmnHistoricDecisionExecutionQuery orderByTenantId() {
        return orderBy(HistoricDecisionExecutionQueryProperty.TENANT_ID);
    }

    // results ////////////////////////////////////////////

    public long executeCount(CommandContext commandContext) {
        checkQueryOk();
        return CommandContextUtil.getHistoricDecisionExecutionEntityManager().findHistoricDecisionExecutionCountByQueryCriteria(this);
    }

    public List<DmnHistoricDecisionExecution> executeList(CommandContext commandContext) {
        checkQueryOk();
        return CommandContextUtil.getHistoricDecisionExecutionEntityManager().findHistoricDecisionExecutionsByQueryCriteria(this);
    }

    public void checkQueryOk() {
        super.checkQueryOk();
    }

    // getters ////////////////////////////////////////////

    public String getId() {
        return id;
    }

    public Set<String> getIds() {
        return ids;
    }

    public String getDecisionDefinitionId() {
        return decisionDefinitionId;
    }
    
    public String getDecisionKey() {
        return decisionKey;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getActivityId() {
        return activityId;
    }
    
    public String getTenantId() {
        return tenantId;
    }

    public String getTenantIdLike() {
        return tenantIdLike;
    }

    public boolean isWithoutTenantId() {
        return withoutTenantId;
    }
}
