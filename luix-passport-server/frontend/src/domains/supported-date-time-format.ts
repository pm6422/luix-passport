import { z } from "zod"

export const supportedDateTimeFormatSchema = z.object({
  id: z.string().trim().min(1, { message: "Required" }),
  displayName: z.string().trim().min(1, { message: "Required" }),
  dateTimeFormat: z.string().trim().min(1, { message: "Required" }),
  dateFormat: z.string().trim().min(1, { message: "Required" }),
  timeFormat: z.string().trim().min(1, { message: "Required" }),
  example: z.string().trim().min(1, { message: "Required" }),
  preset: z.boolean()
})

export type SupportedDateTimeFormat = z.infer<typeof supportedDateTimeFormatSchema>