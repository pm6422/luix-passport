import { z } from "zod"
import { BaseCriteria } from "./base/base-criteria"

export const scheduleExecutionLogSchema = z.object({
  id: z.string().trim().min(1, { message: "Required" }),
  scheduleName: z.string().optional().nullable(),
  startAt: z.string().optional().nullable(),
  endAt: z.string().optional().nullable(),
  durationMs: z.number().optional().nullable(),
  status: z.string().optional().nullable(),
  node: z.string().optional().nullable(),
  parameters: z.string().optional().nullable(),
  error: z.string().optional().nullable(),
})

export type ScheduleExecutionLog = z.infer<typeof scheduleExecutionLogSchema>

export const initialScheduleExecutionLogState: ScheduleExecutionLog = {
  id: "",
  scheduleName: "",
  startAt: "",
  endAt: "",
  durationMs: 0,
  status: "",
  node: "",
  parameters: "",
  error: ""
}
export const scheduleExecutionLogCriteriaSchema = z.object({
  scheduleName: z.string().optional().nullable(),
  status: z.string().optional().nullable(),
})

export type ScheduleExecutionLogCriteriaSchema = z.infer<typeof scheduleExecutionLogCriteriaSchema> & BaseCriteria

export const initialScheduleExecutionLogCriteriaState: ScheduleExecutionLogCriteriaSchema = {
  scheduleName: ""
}