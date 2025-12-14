import { UseFormReturn } from "react-hook-form"
import { DateTime } from "@/components/custom/display/date-time"
import { type ScheduleExecutionLog } from "@/domains/schedule-execution-log"

interface ScheduleExecutionLogFormFieldsProps {
  form: UseFormReturn<ScheduleExecutionLog>
}

export function ScheduleExecutionLogFormFields({
  form
}: ScheduleExecutionLogFormFieldsProps) {
  return (
    <>
      <div>
        <h3 className="text-sm font-medium">ID</h3>
        <p className="mt-2 text-sm text-muted-foreground">
          {form.getValues("id")}
        </p>
      </div>

      <div>
        <h3 className="text-sm font-medium">Schedule Name</h3>
        <p className="mt-2 text-sm text-muted-foreground">
          {form.getValues("scheduleName")}
        </p>
      </div>

      <div>
        <h3 className="text-sm font-medium">Start At</h3>
        <p className="mt-2 text-sm text-muted-foreground">
          <DateTime value={form.getValues("startAt") ?? ""}/>
        </p>
      </div>

      <div>
        <h3 className="text-sm font-medium">End At</h3>
        <p className="mt-2 text-sm text-muted-foreground">
          <DateTime value={form.getValues("endAt") ?? ""}/>
        </p>
      </div>

      <div>
        <h3 className="text-sm font-medium">Duration(ms)</h3>
        <p className="mt-2 text-sm text-muted-foreground">
          {form.getValues("durationMs")}
        </p>
      </div>

      <div>
        <h3 className="text-sm font-medium">Status</h3>
        <p className="mt-2 text-sm text-muted-foreground">
          {form.getValues("status")}
        </p>
      </div>

      <div>
        <h3 className="text-sm font-medium">Node</h3>
        <p className="mt-2 text-sm text-muted-foreground">
          {form.getValues("node")}
        </p>
      </div>

      <div>
        <h3 className="text-sm font-medium">Parameters</h3>
        <p className="mt-2 text-sm text-muted-foreground">
          {form.getValues("parameters")}
        </p>
      </div>

      <div>
        <h3 className="text-sm font-medium">Error</h3>
        <p className="mt-2 text-sm text-muted-foreground">
          {form.getValues("error")}
        </p>
      </div>
    </>
  )
}
