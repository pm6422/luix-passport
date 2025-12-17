import { z } from "zod"

export interface PromoCode {
  id: string
  code: string
  type: string
  durationDays: number
  enabled: boolean
  usedAt?: string
  usedBy?: string
  expiresAt?: string
  batchId?: string
  createdAt: string
  updatedAt: string
  createdBy?: string
  updatedBy?: string
}

export const generatePromoCodeSchema = z.object({
  count: z.coerce.number().min(1, "Count must be at least 1").max(1000, "Count cannot exceed 1000"),
  type: z.string().min(1, "Type is required"),
  durationDays: z.coerce.number().min(1, "Duration must be at least 1 day"),
  expiry: z.string().optional(),
  batchId: z.string().optional()
})

export type GeneratePromoCode = z.infer<typeof generatePromoCodeSchema>

export const redeemPromoCodeSchema = z.object({
  code: z.string().min(1, "Code is required")
})

export type RedeemPromoCode = z.infer<typeof redeemPromoCodeSchema>

export const initialGeneratePromoCodeState: GeneratePromoCode = {
  count: 10,
  type: "ROLE_MEMBER_PRO",
  durationDays: 30,
  expiry: undefined,
  batchId: ""
}

export const initialRedeemPromoCodeState: RedeemPromoCode = {
  code: ""
}
