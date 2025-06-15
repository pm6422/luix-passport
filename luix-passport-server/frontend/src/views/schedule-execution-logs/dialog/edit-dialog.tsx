import { useState, useEffect, ReactNode } from 'react'
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Dialog, DialogTrigger } from "@/components/ui/dialog"
import SaveDialogContent from "@/components/custom/dialog/save-dialog-content"
import { type ScheduleExecutionLog, scheduleExecutionLogSchema, initialScheduleExecutionLogState } from "@/domains/schedule-execution-log"
import { ScheduleExecutionLogService } from "@/services/schedule-execution-log-service.ts"
import { DateTime } from "@/components/custom/date-time"

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
  const [open, setOpen] = useState(false)
  const form = useForm<ScheduleExecutionLog>({
    resolver: zodResolver(scheduleExecutionLogSchema),
    defaultValues: initialScheduleExecutionLogState
  })

  useEffect(() => {
    if (!open) {
      return
    }

    if (id) {
      ScheduleExecutionLogService.findById(id).then(r => {
        form.reset(r.data)
      })
    }
  }, [form, id, open])

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {children}
      </DialogTrigger>
      <SaveDialogContent<ScheduleExecutionLog> entityName={entityName} id={id} form={form} setOpen={setOpen} readonly={true}>

        <div>
          <h3 className="text-sm font-medium">ID</h3>
          <p className="mt-2 text-sm text-muted-foreground">
            {form.getValues("id")}
          </p>
        </div>

        {/*<div>*/}
        {/*  <h3 className="text-sm font-medium">Lock Until</h3>*/}
        {/*  <p className="mt-2 text-sm text-muted-foreground">*/}
        {/*    <DateTime value={form.getValues("lockUntil") ?? ""}/>*/}
        {/*  </p>*/}
        {/*</div>*/}

        {/*<div>*/}
        {/*  <h3 className="text-sm font-medium">Locked At</h3>*/}
        {/*  <p className="mt-2 text-sm text-muted-foreground">*/}
        {/*    <DateTime value={form.getValues("lockedAt") ?? ""}/>*/}
        {/*  </p>*/}
        {/*</div>*/}

        {/*<div>*/}
        {/*  <h3 className="text-sm font-medium">Locked By</h3>*/}
        {/*  <p className="mt-2 text-sm text-muted-foreground">*/}
        {/*    {form.getValues("lockedBy")}*/}
        {/*  </p>*/}
        {/*</div>*/}
      </SaveDialogContent>
    </Dialog>
  )
}