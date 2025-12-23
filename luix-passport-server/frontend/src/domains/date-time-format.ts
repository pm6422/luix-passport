import { z } from "zod"

export const dateTimeFormatSchema = z.object({
  id: z.string().trim().min(1, { message: "Required" }),
  displayName: z.string().trim().min(1, { message: "Required" }),
  dateTimeFormat: z.string().trim().min(1, { message: "Required" }),
  dateFormat: z.string().trim().min(1, { message: "Required" }),
  timeFormat: z.string().trim().min(1, { message: "Required" }),
  example: z.string().trim().min(1, { message: "Required" }),
  preset: z.boolean()
})

export type DateTimeFormat = z.infer<typeof dateTimeFormatSchema>