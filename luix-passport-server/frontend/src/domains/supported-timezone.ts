import { z } from "zod"

export const supportedTimezoneSchema = z.object({
  id: z.string().trim().min(1, { message: "Required" }),
  utcOffset: z.string().trim().min(1, { message: "Required" }),
  preset: z.boolean()
})

export type SupportedTimezone = z.infer<typeof supportedTimezoneSchema>