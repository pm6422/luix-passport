import { z } from "zod"

export const userNotificationSchema = z.object({
  id: z.string(),
  notificationId: z.string(),
  title: z.string(),
  content: z.string(),
  type: z.string(),
  receiverId: z.string(),
  status: z.string(),
  active: z.boolean(),
  createdAt: z.string(),
  modifiedAt: z.string()
})

export type UserNotification = z.infer<typeof userNotificationSchema>

export const initialUserNotificationState: UserNotification = {
  id: "",
  notificationId: "",
  title: "",
  content: "",
  type: "",
  receiverId: "",
  status: "",
  active: true,
  createdAt: "",
  modifiedAt: ""
}

export const userNotificationCriteriaSchema = z.object({
  keyword: z.string().optional().nullable()
})

export type UserNotificationCriteria = z.infer<typeof userNotificationCriteriaSchema>

export const initialDataDictCriteriaState: UserNotificationCriteria = {
  keyword: null
}