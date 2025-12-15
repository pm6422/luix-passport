import { z } from "zod"
import { BaseCriteria } from "./base/base-criteria"

export const scheduleLogSchema = z.object({
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

export type ScheduleLog = z.infer<typeof scheduleLogSchema>

export const initialScheduleLogState: ScheduleLog = {
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

export const scheduleLogCriteriaSchema = z.object({
  scheduleName: z.string().optional().nullable(),
  status: z.string().optional().nullable(),
})

export type ScheduleLogCriteriaSchema = z.infer<typeof scheduleLogCriteriaSchema> & BaseCriteria

export const initialScheduleLogCriteriaState: ScheduleLogCriteriaSchema = {
  scheduleName: ""
}
