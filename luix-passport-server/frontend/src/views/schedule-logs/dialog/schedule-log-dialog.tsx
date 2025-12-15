import { ReactNode } from 'react'
import { FormDialog } from "@/components/custom/dialog/form-dialog"
import { type ScheduleLog, scheduleLogSchema, initialScheduleLogState } from "@/domains/schedule-log"
import { ScheduleLogService } from "@/services/schedule-log-service"
import { ScheduleLogFormFields } from "./schedule-log-form-fields"

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
  const loadEntity = async (entityId?: string | null) => {
    if (entityId) {
      const { data } = await ScheduleLogService.findById(entityId)
      return { entity: data }
    }
    return { entity: initialScheduleLogState }
  }
  return (
    <FormDialog<ScheduleLog>
      entityName={entityName}
      id={id}
      schema={scheduleLogSchema}
      defaultValues={initialScheduleLogState}
      readonly={true}
      onLoad={loadEntity}
      renderFields={(form) => (
        <ScheduleLogFormFields form={form} />
      )}
    >
      {children}
    </FormDialog>
  )
}
