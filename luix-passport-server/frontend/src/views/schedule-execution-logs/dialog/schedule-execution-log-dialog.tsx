import { ReactNode } from 'react'
import { FormDialog } from "@/components/custom/dialog/form-dialog"
import { type ScheduleExecutionLog, scheduleExecutionLogSchema, initialScheduleExecutionLogState } from "@/domains/schedule-execution-log"
import { ScheduleExecutionLogService } from "@/services/schedule-execution-log-service.ts"
import { ScheduleExecutionLogFormFields } from "./schedule-execution-log-form-fields"

interface EditDialogProps {
  children: ReactNode,
  entityName: string,
  id?: string | null
}

export function EditDialog({
  children,
  entityName,
  id
}: EditDialogProps) {
  return (
    <FormDialog<ScheduleExecutionLog>
      entityName={entityName}
      id={id}
      schema={scheduleExecutionLogSchema}
      defaultValues={initialScheduleExecutionLogState}
      readonly={true}
      onLoad={async (entityId) => {
        if (entityId) {
          const { data } = await ScheduleExecutionLogService.findById(entityId)
          return { entity: data }
        }
        return { entity: initialScheduleExecutionLogState }
      }}
      renderFields={(form) => (
        <ScheduleExecutionLogFormFields form={form} />
      )}
    >
      {children}
    </FormDialog>
  )
}

