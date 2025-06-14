import { z } from "zod"
import { BaseCriteria } from "./base/base-criteria"

export const scheduleSchema = z.object({
  id: z.string().trim().min(1, { message: "Required" }),
  lockUntil: z.string().optional().nullable(),
  lockedAt: z.string().optional().nullable(),
  lockedBy: z.string().optional().nullable(),
})

export type Schedule = z.infer<typeof scheduleSchema>

export const initialScheduleState: Schedule = {
  id: "",
  lockUntil: "",
  lockedAt: "",
  lockedBy: ""
}
export const scheduleCriteriaSchema = z.object({
  id: z.string().optional().nullable(),
})

export type ScheduleCriteriaSchema = z.infer<typeof scheduleCriteriaSchema> & BaseCriteria

export const initialScheduleCriteriaState: ScheduleCriteriaSchema = {
  id: ""
}